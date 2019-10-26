package scala.collection

package object immutable {
  @deprecated("Use scala.collection.immutable.Bag", "0.3")
  type MultiSet[A] = scala.collection.immutable.Bag[A]
  @deprecated("Use scala.collection.immutable.Bag", "0.3")
  val MultiSet = scala.collection.immutable.Bag
}
