package scala
package collection
package mutable

import java.util.concurrent.atomic.AtomicInteger

/**
  * A mutable multiset.
  */
trait MultiSet[A]
  extends collection.MultiSet[A]
    with collection.MultiSetOps[A, MultiSet, MultiSet[A]]
    with Growable[A]
    with Shrinkable [A] {

  override def iterableFactory: IterableFactory[MultiSet] = MultiSet
  override protected def fromSpecific(coll: IterableOnce[A]): MultiSet[A] = iterableFactory.from(coll)
  override protected def newSpecificBuilder: mutable.Builder[A, MultiSet[A]] = iterableFactory.newBuilder
  override def empty: MultiSet[A] = iterableFactory.empty

  override def knownSize: Int = super[Growable].knownSize
}

class MultiSetImpl[A] private[mutable] (elems: Map[A, AtomicInteger]) extends MultiSet[A] {

  def occurrences: collection.Map[A, Int] = elems.map { case (k, v) => (k, v.get) }

  def addOne(elem: A): this.type = {
    elems.getOrElseUpdate(elem, new AtomicInteger).getAndIncrement
    this
  }

  def subtractOne(elem: A): this.type = {
    elems.get(elem) match {
      case Some(n) => if (n.decrementAndGet <= 0) elems.subtractOne(elem)
      case _ =>
    }
    this
  }

  def clear(): Unit = elems.clear()
}

object MultiSet extends IterableFactory[MultiSet] {

  def from[A](source: IterableOnce[A]): MultiSet[A] = (newBuilder[A] ++= source).result()

  def empty[A]: MultiSet[A] = new MultiSetImpl[A](Map.empty)

  def newBuilder[A]: Builder[A, MultiSet[A]] = new GrowableBuilder[A, MultiSet[A]](empty)

}
