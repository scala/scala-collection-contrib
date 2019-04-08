package scala
package collection
package mutable

/**
  * A mutable multidict
  * @tparam K the type of keys
  * @tparam V the type of values
  */
class MultiDict[K, V] private (elems: Map[K, Set[V]])
  extends collection.MultiDict[K, V]
    with Iterable[(K, V)]
    with IterableOps[(K, V), Iterable, MultiDict[K, V]]
    with collection.MultiDictOps[K, V, MultiDict, MultiDict[K, V]]
    with Growable[(K, V)]
    with Shrinkable[(K, V)] {

  override def multiDictFactory: MapFactory[MultiDict] = MultiDict
  override protected def fromSpecific(coll: IterableOnce[(K, V)]): MultiDict[K, V] = multiDictFactory.from(coll)
  override protected def newSpecificBuilder: mutable.Builder[(K, V), MultiDict[K, V]] = multiDictFactory.newBuilder[K, V]
  override def empty: MultiDict[K, V] = multiDictFactory.empty
  override def withFilter(p: ((K, V)) => Boolean): MultiDictOps.WithFilter[K, V, Iterable, MultiDict] =
    new MultiDictOps.WithFilter(this, p)
  override def knownSize = -1

  def sets: collection.Map[K, collection.Set[V]] = elems

  def addOne(elem: (K, V)): this.type = {
    val (k, v) = elem
    elems.updateWith(k) {
      case None     => Some(Set(v))
      case Some(vs) => Some(vs += v)
    }
    this
  }

  def subtractOne(elem: (K, V)): this.type = {
    val (k, v) = elem
    elems.updateWith(k) {
      case Some(vs) =>
        vs -= v
        if (vs.nonEmpty) Some(vs) else None
      case None => None
    }
    this
  }

  /**
    * Removes all the entries associated with the given `key`
    * @return the collection itself
    */
  def removeKey(key: K): this.type = {
    elems -= key
    this
  }

  /** Alias for `removeKey` */
  @`inline` final def -*= (key: K): this.type = removeKey(key)

  def clear(): Unit = elems.clear()

}

object MultiDict extends MapFactory[MultiDict] {

  def empty[K, V]: MultiDict[K, V] = new MultiDict(Map.empty)

  def from[K, V](source: IterableOnce[(K, V)]): MultiDict[K, V] = (newBuilder[K, V] ++= source).result()

  def newBuilder[K, V]: Builder[(K, V), MultiDict[K, V]] = new GrowableBuilder[(K, V), MultiDict[K, V]](empty)

}