package scala
package collection
package mutable

/**
  * A mutable bag.
  */
trait Bag[A]
  extends collection.Bag[A]
    with collection.BagOps[A, Bag, Bag[A]]
    with Growable[A]
    with Shrinkable [A] {

  override def iterableFactory: IterableFactory[Bag] = Bag
  override protected def fromSpecific(coll: IterableOnce[A]): Bag[A] = iterableFactory.from(coll)
  override protected def newSpecificBuilder: mutable.Builder[A, Bag[A]] = iterableFactory.newBuilder
  override def empty: Bag[A] = iterableFactory.empty

  override def knownSize = super[Growable].knownSize
}

class BagImpl[A] private[mutable](val elems: Map[A, Int]) extends Bag[A] {

  def occurrences: collection.Map[A, Int] = elems

  def addOne(elem: A): this.type = {
    elems.updateWith(elem) {
      case None    => Some(1)
      case Some(n) => Some(n + 1)
    }
    this
  }

  def subtractOne(elem: A): this.type = {
    elems.updateWith(elem) {
      case Some(n) => if (n > 1) Some(n - 1) else None
      case None => None
    }
    this
  }

  def clear(): Unit = elems.clear()
}

object Bag extends IterableFactory[Bag] {

  def from[A](source: IterableOnce[A]): Bag[A] = (newBuilder[A] ++= source).result()

  def empty[A]: Bag[A] = new BagImpl[A](Map.empty)

  def newBuilder[A]: Builder[A, Bag[A]] = new GrowableBuilder[A, Bag[A]](empty)

}