package scala.collection

import scala.util.hashing.MurmurHash3

/**
  * A bag is an unordered collection that can contain multiple occurrences of the same value.
  *
  * Two bags are defined to be equal if they have the same elements, and the same number of occurrences of each element.
  *
  * @tparam A the element type of the collection
  */
trait Bag[A]
  extends Iterable[A]
    with BagOps[A, Bag, Bag[A]]
    with Equals {

  override protected[this] def className: String = "Bag"

  override def iterableFactory: IterableFactory[Bag] = Bag
  override protected def fromSpecific(coll: IterableOnce[A]): Bag[A] = iterableFactory.from(coll)
  override protected def newSpecificBuilder: mutable.Builder[A, Bag[A]] = iterableFactory.newBuilder
  override def empty: Bag[A] = iterableFactory.empty

  def canEqual(that: Any): Boolean = true

  override def equals(o: Any): Boolean = o match {
    case that: Bag[A] =>
      (this eq that) ||
        (that canEqual this) &&
          (this.size == that.size) && {
          try {
            occurrences forall { case (elem, n) => that.get(elem) == n }
          } catch {
            case _: ClassCastException => false
          }
        }
    case _ => false
  }

  override def hashCode(): Int = MurmurHash3.unorderedHash(occurrences, "MultiSet".##)

}

trait BagOps[A, +CC[X] <: Bag[X], +C <: Bag[A]]
  extends IterableOps[A, CC, C] {

  protected[this] def fromSpecificOccurrences(it: Iterable[(A, Int)]): C =
    fromSpecific(it.view.flatMap { case (e, n) => new View.Fill(n)(e) })

  protected[this] def fromOccurrences[E](it: Iterable[(E, Int)]): CC[E] =
    // Note new MultiSet(it.to(Map)) would be more efficient but would also loose duplicates
    iterableFactory.from(it.view.flatMap { case (e, n) => new View.Fill(n)(e) })

  /**
    * @return All the elements contained in this multiset and their number of occurrences
    */
  def occurrences: Map[A, Int]

  def iterator: Iterator[A] =
    occurrences.iterator.flatMap { case (elem, n) => new View.Fill(n)(elem) }

  /**
    * @return The number of occurrences of `elem` in this multiset
    * @param elem Element to look up
    */
  def get(elem: A): Int = occurrences.getOrElse(elem, 0)

  /**
    * @return Whether `elem` has at least one occurrence in this multiset or not
    * @param elem the element to test
    */
  def contains(elem: A): Boolean = occurrences.contains(elem)

  /**
    * @return a new multiset summing the occurrences of this multiset
    *         with the elements of `that`
    *
    * @param that the collection of elements to add to this multiset
    */
  def concat(that: IterableOnce[A]): C = fromSpecific(that match {
    case that: collection.Iterable[A] => new View.Concat(this, that)
    case _ => iterator.concat(that.iterator)
  })

  /**
    * @return a new multiset summing the occurrences of this multiset
    *         and `that` collection of occurrences
    *
    * @param that the collection of occurrences to add to this multiset
    */
  def concatOccurrences(that: Iterable[(A, Int)]): C =
    fromSpecificOccurrences(new View.Concat(occurrences, that))

  /**
    * @return a new multiset resulting from applying the given function `f`
    *         to each pair of element and its number of occurrences of this
    *         multiset and collecting the results
    * @param f the function to apply
    * @tparam B the element type of the returned collection
    */
  def mapOccurrences[B](f: ((A, Int)) => (B, Int)): CC[B] =
    fromOccurrences(new View.Map(occurrences, f))

  def collectOccurrences[B](pf: PartialFunction[(A, Int), (B, Int)]): CC[B] =
    flatMapOccurrences(kvs =>
      if (pf.isDefinedAt(kvs)) new View.Single(pf(kvs))
      else View.Empty
    )

  /**
    * @return a new multiset resulting from applying the given function `f`
    *         to each pair of element and its number of occurrences of this
    *         multiset and concatenating the results
    * @param f the function to apply
    * @tparam B the element type of the returned collection
    */
  def flatMapOccurrences[B](f: ((A, Int)) => IterableOnce[(B, Int)]): CC[B] =
    fromOccurrences(new View.FlatMap(occurrences, f))

  /**
    * @return a new multiset containing only the occurrences of elements
    *         of this multiset that satisfy the given predicate `p`
    */
  def filterOccurrences(p: ((A, Int)) => Boolean): C =
    fromSpecificOccurrences(new View.Filter(occurrences, p, isFlipped = false))

  // TODO Add more multiset operations like union and intersection

}

object Bag extends IterableFactory.Delegate(immutable.Bag)