package scala

package object collection {
  @deprecated("Use scala.collection.Bag", "0.3")
  type MultiSet[A] = scala.collection.Bag[A]
  @deprecated("Use scala.collection.Bag", "0.3")
  val MultiSet = scala.collection.Bag
}
