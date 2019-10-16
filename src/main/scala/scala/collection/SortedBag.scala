package scala.collection

import scala.annotation.unchecked.uncheckedVariance

/**
  * Bag whose elements are sorted
  * @tparam A Type of elements
  */
trait SortedBag[A]
  extends Bag[A]
    with SortedBagOps[A, SortedBag, SortedBag[A]] {

  def unsorted: Bag[A] = this

  def sortedIterableFactory: SortedIterableFactory[SortedBag] = SortedBag
  override protected def fromSpecific(coll: IterableOnce[A]): SortedBag[A] = sortedIterableFactory.from(coll)(ordering)
  override protected def newSpecificBuilder: mutable.Builder[A, SortedBag[A]] = sortedIterableFactory.newBuilder(ordering)
  override def empty: SortedBag[A] = sortedIterableFactory.empty(ordering)
  override def withFilter(p: A => Boolean): SortedBagOps.WithFilter[A, Bag, SortedBag] = new SortedBagOps.WithFilter(this, p)

}

trait SortedBagOps[A, +CC[X] <: Bag[X], +C <: Bag[A]]
  extends BagOps[A, Bag, C]
    with SortedOps[A, C] {

  def sortedIterableFactory: SortedIterableFactory[CC]

  protected def sortedFromIterable[B : Ordering](it: Iterable[B]): CC[B] = sortedIterableFactory.from(it)
  protected def sortedFromOccurrences[B : Ordering](it: Iterable[(B, Int)]): CC[B] =
    sortedFromIterable(it.view.flatMap { case (b, n) => new View.Fill(n)(b) })

  /** `this` sorted bag upcasted to an unsorted bag */
  def unsorted: Bag[A]

  def occurrences: SortedMap[A, Int]

  /**
    * Creates an iterator that contains all values from this collection
    * greater than or equal to `start` according to the ordering of
    * this collection. x.iteratorFrom(y) is equivalent to but will usually
    * be more efficient than x.from(y).iterator
    *
    * @param start The lower-bound (inclusive) of the iterator
    */
  def iteratorFrom(start: A): Iterator[A] =
    occurrences.iteratorFrom(start).flatMap { case (elem, n) => new View.Fill(n)(elem) }

  def firstKey: A = occurrences.firstKey
  def lastKey: A = occurrences.lastKey

  def rangeTo(to: A): C = {
    val i = rangeFrom(to).iterator
    if (i.isEmpty) return coll
    val next = i.next()
    if (ordering.compare(next, to) == 0)
      if (i.isEmpty) coll
      else rangeUntil(i.next())
    else
      rangeUntil(next)
  }

  /** Builds a new sorted bag by applying a function to all elements of this sorted bag.
    *
    *  @param f      the function to apply to each element.
    *  @tparam B     the element type of the returned collection.
    *  @return       a new collection resulting from applying the given function
    *                `f` to each element of this sorted bag and collecting the results.
    */
  def map[B : Ordering](f: A => B): CC[B] = sortedFromIterable(new View.Map(toIterable, f))

  /**
    * Builds a new sorted bag by applying a function to all pairs of element and its
    * number of occurrences.
    *
    * @param f  the function to apply
    * @tparam B the element type of the returned collection
    * @return   a new collection resulting from applying the given function
    *           `f` to each pair of element and its number of occurrences of this
    *           sorted bag and collecting the results.
    */
  def mapOccurrences[B : Ordering](f: ((A, Int)) => (B, Int)): CC[B] =
    sortedFromOccurrences(new View.Map(occurrences, f))

  /**
    * Builds a new collection by applying a function to all elements of this sorted
    * bag and using the elements of the resulting collections.
    *
    * @param f      the function to apply to each element.
    * @tparam B     the element type of the returned collection.
    * @return a new collection resulting from applying the given function `f` to
    *         each element of this sorted bag and concatenating the results.
    */
  def flatMap[B : Ordering](f: A => IterableOnce[B]): CC[B] = sortedFromIterable(new View.FlatMap(toIterable, f))

  /**
    * Builds a new collection by applying a function to all pairs of element and
    * its number of occurrences of this sorted bag and using the elements of
    * the resulting collections.
    *
    * @param f      the function to apply to each element.
    * @tparam B     the element type of the returned collection.
    * @return a new collection resulting from applying the given function `f` to
    *         each pair of element and its number of occurrences of this sorted
    *         bag and concatenating the results.
    */
  def flatMapOccurrences[B : Ordering](f: ((A, Int)) => IterableOnce[(B, Int)]): CC[B] =
    sortedFromOccurrences(new View.FlatMap(occurrences, f))

  /**
    * Returns a sorted bag formed from this sorted bag and another iterable
    * collection, by combining corresponding elements in pairs.
    * @param that The iterable providing the second half of each result pair
    * @param ev The ordering instance for type `B`
    * @tparam B the type of the second half of the returned pairs
    * @return a new sorted bag containing pairs consisting of corresponding elements
    *         of this sorted bag and `that`. The length of the returned collection
    *         is the minimum of the lengths of `this` and `that`
    */
  def zip[B](that: Iterable[B])(implicit ev: Ordering[B]): CC[(A @uncheckedVariance, B)] = // sound bcs of VarianceNote
    sortedFromIterable(new View.Zip(toIterable, that))(Ordering.Tuple2(ordering, implicitly))

  /**
    * @return a new collection resulting from applying the given partial
    *         function `pf` to each element on which it is defined and
    *         collecting the results
    * @param pf the partial function which filters and map this sorted bag
    * @tparam B the element type of the returned collection
    */
  def collect[B : Ordering](pf: PartialFunction[A, B]): CC[B] = flatMap(a =>
    if (pf.isDefinedAt(a)) new View.Single(pf(a))
    else View.Empty
  )

  /**
    * @return a new collection resulting from applying the given partial
    *         function `pf` to each group of occurrences on which it is defined and
    *         collecting the results
    * @param pf the partial function which filters and map this sorted bag
    * @tparam B the element type of the returned collection
    */
  def collectOccurrences[B : Ordering](pf: PartialFunction[(A, Int), (B, Int)]): CC[B] = flatMapOccurrences(a =>
    if (pf.isDefinedAt(a)) new View.Single(pf(a))
    else View.Empty
  )

  // --- Override return type of methods that returned an unsorted Bag

  override def zipWithIndex: CC[(A, Int)] =
    sortedFromIterable(new View.ZipWithIndex(toIterable))(Ordering.Tuple2(ordering, implicitly))

}

object SortedBagOps {

  /** Specialize `WithFilter` for sorted collections
    *
    * @define coll sorted collection
    */
  class WithFilter[A, +IterableCC[_], +CC[X] <: Bag[X]](
    `this`: SortedBagOps[A, CC, _] with IterableOps[A, IterableCC, _],
    p: A => Boolean
  ) extends IterableOps.WithFilter[A, IterableCC](`this`, p) {

    def map[B : Ordering](f: A => B): CC[B] =
      `this`.sortedIterableFactory.from(new View.Map(filtered, f))

    def flatMap[B : Ordering](f: A => IterableOnce[B]): CC[B] =
      `this`.sortedIterableFactory.from(new View.FlatMap(filtered, f))

    override def withFilter(q: A => Boolean): WithFilter[A, IterableCC, CC] =
      new WithFilter[A, IterableCC, CC](`this`, a => p(a) && q(a))

  }

}

object SortedBag extends SortedIterableFactory.Delegate(immutable.SortedBag)
