package scala
package collection
package mutable

import java.util.concurrent.atomic.AtomicInteger

/**
  * A mutable multiset whose elements are sorted according to a given ordering.
  *
  * @tparam A Type of elements
  */
class SortedMultiSet[A] private (elems: SortedMap[A, AtomicInteger])(implicit val ordering: Ordering[A])
  extends MultiSet[A]
    with collection.SortedMultiSet[A]
    with collection.SortedMultiSetOps[A, SortedMultiSet, SortedMultiSet[A]]
    with MultiSetOps[A, MultiSet, SortedMultiSet[A]]
    with Growable[A]
    with Shrinkable[A] {

  def occurrences: collection.SortedMap[A, Int] = elems.map { case (k, v) => (k, v.get) }

  override def sortedIterableFactory: SortedIterableFactory[SortedMultiSet] = SortedMultiSet
  override protected def fromSpecific(coll: IterableOnce[A]): SortedMultiSet[A] = sortedIterableFactory.from(coll)
  override protected def newSpecificBuilder: mutable.Builder[A, SortedMultiSet[A]] = sortedIterableFactory.newBuilder[A]
  override def empty: SortedMultiSet[A] = sortedIterableFactory.empty
  override def withFilter(p: A => Boolean): SortedMultiSetOps.WithFilter[A, MultiSet, SortedMultiSet] =
    new SortedMultiSetOps.WithFilter(this, p)

  def rangeImpl(from: Option[A], until: Option[A]): SortedMultiSet[A] =
    new SortedMultiSet(elems.rangeImpl(from, until))

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

object SortedMultiSet extends SortedIterableFactory[SortedMultiSet] {

  def from[E: Ordering](it: IterableOnce[E]): SortedMultiSet[E] = (newBuilder[E] ++= it).result()

  def empty[A: Ordering]: SortedMultiSet[A] = new SortedMultiSet[A](SortedMap.empty[A, AtomicInteger])

  def newBuilder[A: Ordering]: Builder[A, SortedMultiSet[A]] = new GrowableBuilder[A, SortedMultiSet[A]](empty)

}
