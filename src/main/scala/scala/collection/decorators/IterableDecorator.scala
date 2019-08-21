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


  /**
    * Constructs a collection where consecutive elements are accumulated as
    * long as the output of f for each element doesn't change.
    * <pre>
    * Vector(1,2,2,3,3,3,2,2)
    * .splitBy(identity)
    * </pre>
    * produces
    * <pre>
    * Vector(Vector(1),
    * Vector(2,2),
    * Vector(3,3,3),
    * Vector(2,2))
    * </pre>
    *
    * @param f the function to compute a key for an element
    * @tparam K the type of the computed key
    * @return a collection of collections of the consecutive elements with the
    *         same key in the original collection
    */
  def splitBy[K, CC1, CC2](f: it.A => K)(implicit bf: BuildFrom[C, it.A, CC1], bff: BuildFrom[C, CC1, CC2]): CC2 = {
    bff.fromSpecific(coll)(it(coll).iterator.splitBy(f).map(bf.fromSpecific(coll)))
  }
}
