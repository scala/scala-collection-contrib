package scala
package collection
package mutable

/**
  * A mutable bag whose elements are sorted according to a given ordering.
  *
  * @tparam A Type of elements
  */
class SortedBag[A] private(elems: SortedMap[A, Int])(implicit val ordering: Ordering[A])
  extends Bag[A]
    with collection.SortedBag[A]
    with collection.SortedBagOps[A, SortedBag, SortedBag[A]]
    with BagOps[A, Bag, SortedBag[A]]
    with Growable[A]
    with Shrinkable[A] {

  def occurrences: collection.SortedMap[A, Int] = elems

  override def sortedIterableFactory: SortedIterableFactory[SortedBag] = SortedBag
  override protected def fromSpecific(coll: IterableOnce[A]): SortedBag[A] = sortedIterableFactory.from(coll)
  override protected def newSpecificBuilder: mutable.Builder[A, SortedBag[A]] = sortedIterableFactory.newBuilder[A]
  override def empty: SortedBag[A] = sortedIterableFactory.empty
  override def withFilter(p: A => Boolean): SortedBagOps.WithFilter[A, Bag, SortedBag] =
    new SortedBagOps.WithFilter(this, p)

  def rangeImpl(from: Option[A], until: Option[A]): SortedBag[A] =
    new SortedBag(elems.rangeImpl(from, until))

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

object SortedBag extends SortedIterableFactory[SortedBag] {

  def from[E: Ordering](it: IterableOnce[E]): SortedBag[E] = (newBuilder[E] ++= it).result()

  def empty[A: Ordering]: SortedBag[A] = new SortedBag[A](SortedMap.empty[A, Int])

  def newBuilder[A: Ordering]: Builder[A, SortedBag[A]] = new GrowableBuilder[A, SortedBag[A]](empty)

}