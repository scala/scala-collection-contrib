package scala.collection

/**
  * A multidict whose keys are sorted
  * @tparam K the type of keys
  * @tparam V the type of values
  */
trait SortedMultiDict[K, V]
  extends MultiDict[K, V]
    with SortedMultiDictOps[K, V, SortedMultiDict, SortedMultiDict[K, V]] {

  def unsorted: MultiDict[K, V] = this

  def sortedMultiDictFactory: SortedMapFactory[SortedMultiDict] = SortedMultiDict
  override protected def fromSpecific(coll: IterableOnce[(K, V)]): SortedMultiDict[K, V] = sortedMultiDictFactory.from(coll)(ordering)
  override protected def newSpecificBuilder: mutable.Builder[(K, V), SortedMultiDict[K, V]] = sortedMultiDictFactory.newBuilder(ordering)
  override def empty: SortedMultiDict[K, V] = sortedMultiDictFactory.empty(ordering)
  override def withFilter(p: ((K, V)) => Boolean): SortedMultiDictOps.WithFilter[K, V, Iterable, MultiDict, SortedMultiDict] = new SortedMultiDictOps.WithFilter(this, p)

}

trait SortedMultiDictOps[K, V, +CC[X, Y] <: MultiDict[X, Y], +C <: MultiDict[K, V]]
  extends MultiDictOps[K, V, MultiDict, C]
    with SortedOps[K, C] {

  def sortedMultiDictFactory: SortedMapFactory[CC]

  protected def sortedFromIterable[L : Ordering, W](it: Iterable[(L, W)]): CC[L, W] = sortedMultiDictFactory.from(it)
  protected def sortedFromSets[L : Ordering, W](it: Iterable[(L, Set[W])]): CC[L, W] =
    sortedFromIterable(it.view.flatMap { case (l, ws) => ws.map(w => (l, w)) })

  /** `this` sorted multidict upcasted to an unsorted multidict */
  def unsorted: MultiDict[K, V]

  def sets: SortedMap[K, Set[V]]

  def iteratorFrom(start: K): Iterator[(K, V)] =
    sets.iteratorFrom(start).flatMap { case (k, vs) => vs.view.map(v => (k, v)) }

  def firstKey: K = sets.firstKey

  def lastKey: K = sets.lastKey

  def rangeTo(to: K): C = {
    val i = rangeFrom(to).iterator
    if (i.isEmpty) return coll
    val next = i.next()._1
    if (ordering.compare(next, to) == 0)
      if (i.isEmpty) coll
      else rangeUntil(i.next()._1)
    else
      rangeUntil(next)
  }

  /**
    * @return a sorted multidict that contains all the entries of `this` sorted multidict,
    *         transformed by the function `f`
    *
    * @param f transformation function
    * @tparam L new type of keys
    * @tparam W new type of values
    */
  def map[L : Ordering, W](f: ((K, V)) => (L, W)): CC[L, W] = sortedFromIterable(new View.Map(toIterable, f))

  /**
    * Builds a new sorted multidict by applying a function to all groups of elements
    *
    * @param f  the function to apply
    * @tparam L the type of keys of the returned collection
    * @return   a new collection resulting from applying the given function
    *           `f` to each pair of element and its number of occurrences of this
    *           sorted multiset and collecting the results.
    */
  def mapSets[L : Ordering, W](f: ((K, Set[V])) => (L, Set[W])): CC[L, W] = sortedFromSets(new View.Map(sets, f))

  /**
    * @return a sorted multidict that contains all the entries of `this` sorted multidict,
    *         transformed by the function `f` and concatenated
    *
    * @param f transformation function
    * @tparam L new type of keys
    * @tparam W new type of values
    */
  def flatMap[L : Ordering, W](f: ((K, V)) => IterableOnce[(L, W)]): CC[L, W] = sortedFromIterable(new View.FlatMap(toIterable, f))

  /**
    * @return a new sorted multidict resulting from applying the given function `f`
    *         to each group of values of this sorted multidict and concatenating
    *         the results
    * @param f the function to apply
    * @tparam L the new type of keys
    * @tparam W the type of values of the returned sorted multidict
    */
  def flatMapSets[L : Ordering, W](f: ((K, Set[V])) => IterableOnce[(L, Set[W])]): CC[L, W] = sortedFromSets(new View.FlatMap(sets, f))

  /**
    * @return a sorted multidict that contains all the entries of `this` sorted multidict
    *         after they have been successfully transformed by the
    *         given partial function `pf`
    *
    * @param pf transformation to apply
    * @tparam L new type of keys
    * @tparam W new type of values
    */
  def collect[L : Ordering, W](pf: PartialFunction[(K, V), (L, W)]): CC[L, W] = flatMap(kv =>
    if (pf.isDefinedAt(kv)) new View.Single(pf(kv))
    else View.Empty
  )

  /**
    * @return a sorted multidict that contains all the entries of `this` sorted multidict,
    *         after they have been successfully transformed by the given
    *         partial function
    *
    * @param pf the partial function to apply to each set of values
    * @tparam L the new type of keys
    * @tparam W the new type of values
    */
  def collectSets[L : Ordering, W](pf: PartialFunction[(K, Set[V]), (L, Set[W])]): CC[L, W] = flatMapSets(kv =>
    if (pf.isDefinedAt(kv)) new View.Single(pf(kv))
    else View.Empty
  )

}

object SortedMultiDictOps {

  class WithFilter[K, V, +IterableCC[_], +MultiDictCC[X, Y] <: MultiDict[X, Y], +CC[X, Y] <: MultiDict[X, Y]](
    `this`: SortedMultiDictOps[K, V, CC, ?] & MultiDictOps[K, V, MultiDictCC, ?] & IterableOps[(K, V), IterableCC, ?],
    p: ((K, V)) => Boolean
  ) extends MultiDictOps.WithFilter[K, V, IterableCC, MultiDictCC](`this`, p) {

    def map[L : Ordering, W](f: ((K, V)) => (L, W)): CC[L, W] =
      `this`.sortedMultiDictFactory.from(new View.Map(filtered, f))

    def flatMap[L : Ordering, W](f: ((K, V)) => IterableOnce[(L, W)]): CC[L, W] =
      `this`.sortedMultiDictFactory.from(new View.FlatMap(filtered, f))

    override def withFilter(q: ((K, V)) => Boolean): WithFilter[K, V, IterableCC, MultiDictCC, CC] =
      new WithFilter[K, V, IterableCC, MultiDictCC, CC](`this`, kv => p(kv) && q(kv))

  }

}

object SortedMultiDict extends SortedMapFactory.Delegate[SortedMultiDict](immutable.SortedMultiDict)
