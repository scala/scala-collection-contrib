package scala
package collection
package immutable

import scala.collection.mutable.{Builder, ImmutableBuilder}

/**
  * An immutable bag whose elements are sorted.
  * @tparam A Type of elements
  */
class SortedBag[A] private(elems: SortedMap[A, Int])(implicit val ordering: Ordering[A])
  extends Bag[A]
    with collection.SortedBag[A]
    with BagOps[A, Bag, SortedBag[A]]
    with collection.SortedBagOps[A, SortedBag, SortedBag[A]]
    with collection.IterableOps[A, Bag, SortedBag[A]] {

  def occurrences: SortedMap[A, Int] = elems

  override def sortedIterableFactory: SortedIterableFactory[SortedBag] = SortedBag
  override protected def fromSpecific(coll: IterableOnce[A]): SortedBag[A] = sortedIterableFactory.from(coll)
  override protected def newSpecificBuilder: mutable.Builder[A, SortedBag[A]] = sortedIterableFactory.newBuilder[A]
  override def empty: SortedBag[A] = sortedIterableFactory.empty
  override def withFilter(p: A => Boolean): SortedBagOps.WithFilter[A, Bag, SortedBag] =
    new SortedBagOps.WithFilter(this, p)

  def rangeImpl(from: Option[A], until: Option[A]): SortedBag[A] =
    new SortedBag(elems.rangeImpl(from, until))

  /**
    * @return an immutable sorted bag containing all the elements of
    *         this bag and one more occurrence of `elem`
    * @param elem the element to add
    */
  def incl(elem: A): SortedBag[A] =
    new SortedBag(elems.updatedWith(elem) {
      case None    => Some(1)
      case Some(n) => Some(n + 1)
    })

  /**
    * @return an immutable sorted bag containing all the elements of
    *         this bag and one occurrence less of `elem`
    *
    * @param elem the element to remove
    */
  def excl(elem: A): SortedBag[A] =
    new SortedBag(elems.updatedWith(elem) {
      case Some(n) => if (n > 1) Some(n - 1) else None
      case None => None
    })
}

object SortedBag extends SortedIterableFactory[SortedBag] {

  def from[A: Ordering](source: IterableOnce[A]): SortedBag[A] =
    source match {
      case sms: SortedBag[A] => sms
      case _ => (newBuilder[A] ++= source).result()
    }

  def empty[A: Ordering]: SortedBag[A] = new SortedBag[A](TreeMap.empty)

  def newBuilder[A: Ordering]: Builder[A, SortedBag[A]] =
    new ImmutableBuilder[A, SortedBag[A]](empty) {
      def addOne(elem: A): this.type = { elems = elems + elem; this }
    }

}