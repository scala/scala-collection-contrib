package scala.collection

import scala.util.hashing.MurmurHash3

/**
  * A multidict is a map that can associate a set of values to a given key.
  *
  * @tparam K the type of keys
  * @tparam V the type of values
  */
trait MultiDict[K, V]
  extends Iterable[(K, V)]
    with MultiDictOps[K, V, MultiDict, MultiDict[K, V]]
    with Equals {

  override protected[this] def className: String = "MultiDict"

  def multiDictFactory: MapFactory[MultiDict] = MultiDict
  override protected def fromSpecific(coll: IterableOnce[(K, V)]): MultiDict[K, V] = multiDictFactory.from(coll)
  override protected def newSpecificBuilder: mutable.Builder[(K, V), MultiDict[K, V]] = multiDictFactory.newBuilder
  override def empty: MultiDict[K, V] = multiDictFactory.empty
  override def withFilter(p: ((K, V)) => Boolean): MultiDictOps.WithFilter[K, V, Iterable, MultiDict] = new MultiDictOps.WithFilter(this, p)

  def canEqual(that: Any): Boolean = true

  override def equals(o: Any): Boolean = o match {
    case that: MultiDict[K, V] =>
      (this eq that) ||
        (that canEqual this) &&
          (this.size == that.size) && {
          try {
            sets forall { case (k, vs) => that.sets.get(k).contains(vs) }
          } catch {
            case _: ClassCastException => false
          }
        }
    case _ => false
  }

  override def hashCode(): Int = MurmurHash3.unorderedHash(sets, "MultiMap".##)

}


trait MultiDictOps[K, V, +CC[X, Y] <: MultiDict[X, Y], +C <: MultiDict[K, V]]
  extends IterableOps[(K, V), Iterable, C] {

  def multiDictFactory: MapFactory[CC]

  protected def multiDictFromIterable[L, W](it: Iterable[(L, W)]): CC[L, W] =
    multiDictFactory.from(it)

  protected def fromSpecificSets(it: Iterable[(K, Set[V])]): C =
    fromSpecific(it.view.flatMap { case (k, vs) => vs.view.map(v => (k, v)) })

  protected def fromSets[L, W](it: Iterable[(L, Set[W])]): CC[L, W] =
    multiDictFromIterable(it.view.flatMap { case (k, vs) => vs.view.map(v => (k, v)) })

  /**
    * @return All the elements contained in this multidict, grouped by key
    */
  def sets: Map[K, Set[V]]

  def iterator: Iterator[(K, V)] =
    sets.iterator.flatMap { case (k, vs) => vs.view.map(v => (k, v)) }

  /**
    * @return The set of values associated with the given `key`, or the empty
    *         set if there is no such association
    * @param key key to look up
    */
  def get(key: K): Set[V] = sets.get(key).getOrElse(Set.empty)

  /**
    * @return Whether `key` has at least one occurrence in this multidict or not
    * @param key the key to test
    */
  def containsKey(key: K): Boolean = sets.contains(key)

  /**
    * @return Whether the binding `kv` is contained in this multidict or not
    * @param kv the binding to test
    */
  def containsEntry(kv: (K, V)): Boolean = sets.get(kv._1).exists(_.contains(kv._2))

  /**
    * @return Whether at least one key is associated to the given `value`
    * @param value the value to test
    */
  def containsValue(value: V): Boolean = sets.exists { case (_, vs) => vs.contains(value) }

  /** @return the set of keys */
  def keySet: Set[K] = sets.keySet

  /** @return all the values contained in this multidict */
  def values: Iterable[V] = sets.values.flatten

  /**
    * @return a multidict that contains all the entries of `this` multidict,
    *         transformed by the function `f`
    *
    * @param f transformation function
    * @tparam L new type of keys
    * @tparam W new type of values
    */
  def map[L, W](f: ((K, V)) => (L, W)): CC[L, W] =
    multiDictFromIterable(new View.Map(toIterable, f))

  /**
    * @return a multidict that contains all the entries of `this` multidict,
    *         transformed by the function `f` and concatenated
    *
    * @param f transformation function
    * @tparam L new type of keys
    * @tparam W new type of values
    */
  def flatMap[L, W](f: ((K, V)) => IterableOnce[(L, W)]): CC[L, W] =
    multiDictFromIterable(new View.FlatMap(toIterable, f))

  /**
    * @return a multidict that contains all the entries of `this` multidict
    *         after they have been successfully transformed by the
    *         given partial function `pf`
    *
    * @param pf transformation to apply
    * @tparam L new type of keys
    * @tparam W new type of values
    */
  def collect[L, W](pf: PartialFunction[(K, V), (L, W)]): CC[L, W] =
    flatMap(kv =>
      if (pf.isDefinedAt(kv)) new View.Single(pf(kv))
      else View.Empty
    )

  /** Concatenate the entries given in `that` iterable to `this` multidict */
  def concat(that: IterableOnce[(K, V)]): C = fromSpecific(that match {
    case that: collection.Iterable[(K, V)] => new View.Concat(toIterable, that)
    case _ => iterator ++ that.iterator
  })

  /**
    * @return Whether there exists a value associated with the given `key`
    *         that satisfies the given predicate `p`
    */
  def entryExists(key: K, p: V => Boolean): Boolean =
    sets.get(key).exists(_.exists(p))

  /**
    * @return a new multidict resulting from applying the given function `f`
    *         to each group of values of this multidict and collecting
    *         the results
    * @param f the function to apply
    * @tparam L the new type of keys
    * @tparam W the type of values of the returned multidict
    */
  def mapSets[L, W](f: ((K, Set[V])) => (L, Set[W])): CC[L, W] =
    fromSets(new View.Map(sets, f))

  /**
    * @return a multidict that contains all the entries of `this` multidict,
    *         after they have been successfully transformed by the given
    *         partial function
    *
    * @param pf the partial function to apply to each set of values
    * @tparam L the new type of keys
    * @tparam W the new type of values
    */
  def collectSets[L, W](pf: PartialFunction[(K, Set[V]), (L, Set[W])]): CC[L, W] =
    flatMapSets(kvs =>
      if (pf.isDefinedAt(kvs)) new View.Single(pf(kvs))
      else View.Empty
    )

  /**
    * @return a new multidict resulting from applying the given function `f`
    *         to each group of values of this multidict and concatenating
    *         the results
    * @param f the function to apply
    * @tparam L the new type of keys
    * @tparam W the type of values of the returned multidict
    */
  def flatMapSets[L, W](f: ((K, Set[V])) => IterableOnce[(L, Set[W])]): CC[L, W] =
    fromSets(new View.FlatMap(sets, f))

  /**
    * @return a new multidict concatenating the values of this multidict
    *         and `that` collection of values
    *
    * @param that the collection of values to add to this multidict
    */
  def concatSets(that: Iterable[(K, Set[V])]): C =
    fromSpecificSets(new View.Concat(sets, that))

  /**
    * @return a multidict that contains all the entries of this multidict
    *         that satisfy the predicate `p`
    */
  def filterSets(p: ((K, Set[V])) => Boolean): C =
    fromSpecificSets(new View.Filter(sets, p, isFlipped = false))

  override def addString(sb: StringBuilder, start: String, sep: String, end: String): StringBuilder =
    iterator.map { case (k, v) => s"$k -> $v" }.addString(sb, start, sep, end)
}

object MultiDictOps {

  class WithFilter[K, V, +IterableCC[_], +CC[X, Y] <: MultiDict[X, Y]](
    `this`: MultiDictOps[K, V, CC, _] with IterableOps[(K, V), IterableCC, _],
    p: ((K, V)) => Boolean
  ) extends IterableOps.WithFilter[(K, V), IterableCC](`this`, p) {

    def map[L, W](f: ((K, V)) => (L, W)): CC[L, W] =
      `this`.multiDictFactory.from(new View.Map(filtered, f))

    def flatMap[L, W](f: ((K, V)) => IterableOnce[(L, W)]): CC[L, W] =
      `this`.multiDictFactory.from(new View.FlatMap(filtered, f))

    override def withFilter(q: ((K, V)) => Boolean): WithFilter[K, V, IterableCC, CC] =
      new WithFilter[K, V, IterableCC, CC](`this`, (kv: (K, V)) => p(kv) && q(kv))
  }

}

object MultiDict extends MapFactory.Delegate[MultiDict](immutable.MultiDict)
