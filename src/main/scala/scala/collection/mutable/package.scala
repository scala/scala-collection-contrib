package scala.collection

package object mutable {
  @deprecated("Use scala.collection.mutable.Bag", "0.3")
  type MultiSet[A] = scala.collection.mutable.Bag[A]
  @deprecated("Use scala.collection.mutable.Bag", "0.3")
  val MultiSet = scala.collection.mutable.Bag
}
