package scala
package collection
package mutable

/**
  * A mutable multidict whose keys are sorted
  * @tparam K the type of keys
  * @tparam V the type of values
  */
class SortedMultiDict[K, V] private (elems: SortedMap[K, Set[V]])(implicit val ordering: Ordering[K])
  extends Iterable[(K, V)]
    with collection.SortedMultiDict[K, V]
    with IterableOps[(K, V), Iterable, SortedMultiDict[K, V]]
    with collection.SortedMultiDictOps[K, V, SortedMultiDict, SortedMultiDict[K, V]]
    with Growable[(K, V)]
    with Shrinkable[(K, V)] {

  override def knownSize = -1

  def sets: collection.SortedMap[K, collection.Set[V]] = elems

  override def sortedMultiDictFactory: SortedMapFactory[SortedMultiDict] = SortedMultiDict
  override protected def fromSpecific(coll: IterableOnce[(K, V)]): SortedMultiDict[K, V] = sortedMultiDictFactory.from(coll)
  override protected def newSpecificBuilder: mutable.Builder[(K, V), SortedMultiDict[K, V]] = sortedMultiDictFactory.newBuilder[K, V]
  override def empty: SortedMultiDict[K, V] = sortedMultiDictFactory.empty
  override def withFilter(p: ((K, V)) => Boolean): SortedMultiDictOps.WithFilter[K, V, Iterable, collection.MultiDict, SortedMultiDict] =
    new SortedMultiDictOps.WithFilter[K, V, Iterable, collection.MultiDict, SortedMultiDict](this, p)

  def rangeImpl(from: Option[K], until: Option[K]): SortedMultiDict[K, V] =
    new SortedMultiDict(elems.rangeImpl(from, until))

  def addOne(elem: (K, V)): this.type = {
    val (k, v) = elem
    val vs = elems.getOrElseUpdate(k, Set.empty[V])
    vs.addOne(v)
    this
  }

  def subtractOne(elem: (K, V)): this.type = {
    val (k, v) = elem
    elems.get(k) match {
      case Some(vs) =>
        vs.subtractOne(v)
        if (vs.isEmpty) elems.subtractOne(k)
      case _ =>
    }
    this
  }

  /**
    * Removes all the entries associated with the given `key`
    * @return the collection itself
    */
  def removeKey(key: K): this.type = {
    elems.subtractOne(key)
    this
  }

  /** Alias for `removeKey` */
  @`inline` final def -*= (key: K): this.type = removeKey(key)

  def clear(): Unit = elems.clear()

}

object SortedMultiDict extends SortedMapFactory[SortedMultiDict] {

  def empty[K: Ordering, V]: SortedMultiDict[K, V] =
    new SortedMultiDict(SortedMap.empty)

  def from[K: Ordering, V](it: IterableOnce[(K, V)]): SortedMultiDict[K, V] =
    (newBuilder[K, V] ++= it).result()

  def newBuilder[K: Ordering, V]: Builder[(K, V), SortedMultiDict[K, V]] =
    new GrowableBuilder[(K, V), SortedMultiDict[K, V]](empty)

}
