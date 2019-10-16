package scala
package collection
package immutable

import scala.collection.mutable.{Builder, ImmutableBuilder}

/**
  * An immutable bag
  * @tparam A the element type of the collection
  */
trait Bag[A]
  extends collection.Bag[A]
    with Iterable[A]
    with BagOps[A, Bag, Bag[A]] {

  override def iterableFactory: IterableFactory[Bag] = Bag
  override protected def fromSpecific(coll: IterableOnce[A]): Bag[A] = iterableFactory.from(coll)
  override protected def newSpecificBuilder: mutable.Builder[A, Bag[A]] = iterableFactory.newBuilder
  override def empty: Bag[A] = iterableFactory.empty

}

trait BagOps[A, +CC[X] <: Bag[X], +C <: Bag[A]] extends collection.BagOps[A, CC, C] {
  /**
    * @return an immutable bag containing all the elements of this bag
    *         and one more occurrence of `elem`
    * @param elem the element to add
    */
  def incl(elem: A): C

  /** Alias for `incl` */
  @`inline` final def + (elem: A): C = incl(elem)

  /**
    * @return an immutable bag containing all the elements of this bag
    *         and one occurrence less of `elem`
    *
    * @param elem the element to remove
    */
  def excl(elem: A): C

  /** Alias for `excl` */
  @`inline` final def - (elem: A): C = excl(elem)
}

class BagImpl[A] private[immutable](elems: Map[A, Int]) extends Bag[A] {

  def occurrences: Map[A, Int] = elems

  override def iterableFactory: IterableFactory[Bag] = Bag

  /**
    * @return an immutable bag containing all the elements of this bag
    *         and one more occurrence of `elem`
    * @param elem the element to add
    */
  def incl(elem: A): Bag[A] =
    new BagImpl(elems.updatedWith(elem) {
      case None    => Some(1)
      case Some(n) => Some(n + 1)
    })

  /**
    * @return an immutable bag containing all the elements of this bag
    *         and one occurrence less of `elem`
    *
    * @param elem the element to remove
    */
  def excl(elem: A): Bag[A] =
    new BagImpl(elems.updatedWith(elem) {
      case Some(n) => if (n > 1) Some(n - 1) else None
      case None => None
    })

}

object Bag extends IterableFactory[Bag] {

  def from[A](source: IterableOnce[A]): Bag[A] =
    source match {
      case ms: Bag[A] => ms
      case _ => (newBuilder[A] ++= source).result()
    }

  def empty[A] = new BagImpl[A](Map.empty)

  def newBuilder[A]: Builder[A, Bag[A]] =
    new ImmutableBuilder[A, Bag[A]](empty[A]) {
      def addOne(elem: A): this.type = { elems = elems + elem; this }
    }

}
