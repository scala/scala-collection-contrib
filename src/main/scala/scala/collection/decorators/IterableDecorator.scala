package scala
package collection
package decorators

import scala.collection.generic.IsIterable

class IterableDecorator[C, I <: IsIterable[C]](coll: C)(implicit val it: I) {

  /**
    * Left to right fold that stops if the combination function `op`
    * returns `None`
    * @param z the start value
    * @param op the binary operator
    * @tparam B the result type of the binary operator
    * @return the result of inserting `op` between consecutive elements of the collection,
    *         going left to right with the start value `z` on the left, and stopping when
    *         all the elements have been traversed or earlier if the operator returns `None`
    */
  def foldSomeLeft[B](z: B)(op: (B, it.A) => Option[B]): B =
    it(coll).iterator.foldSomeLeft(z)(op)

  /** Lazy left to right fold. Like `foldLeft` but the combination function `op` is
    * non-strict in its second parameter. If `op(b, a)` chooses not to evaluate `a` and
    * returns `b`, this terminates the traversal early.
    *
    * @param z  the start value
    * @param op the binary operator
    * @tparam B the result type of the binary operator
    * @return   the result of inserting `op` between consecutive elements of the
    *           collection, going left to right with the start value `z` on the left,
    *           and stopping when all the elements have been traversed or earlier if
    *           `op(b, a)` choose not to evaluate `a` and returns `b`
    */
  def lazyFoldLeft[B](z: B)(op: (B, => it.A) => B): B =
    it(coll).iterator.lazyFoldLeft(z)(op)

  /**
    * Right to left fold that can be interrupted before traversing the whole collection.
    * @param z the start value
    * @param op the operator
    * @tparam B the result type
    * @return the result of applying the operator between consecutive elements of the collection,
    *         going right to left, with the start value `z` on the right. The result of the application
    *         of the function `op` to each element drives the process: if it returns `Left(result)`,
    *         then `result` is returned without iterating further; if it returns `Right(f)`, the function
    *         `f` is applied to the previous result to produce the new result and the fold continues.
    */
  def lazyFoldRight[B](z: B)(op: it.A => Either[B, B => B]): B =
    it(coll).iterator.lazyFoldRight(z)(op)

}
