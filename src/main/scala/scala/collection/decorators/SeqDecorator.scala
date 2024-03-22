package scala.collection
package decorators

import scala.collection.generic.IsSeq

/**
  * @param coll the decorated collection
  * @param seq evidence that type `C` is a sequence
  * @tparam C type of the decorated collection (e.g. `List[Int]`, `String`, etc.)
  */
class SeqDecorator[C, S <: IsSeq[C]](coll: C)(implicit val seq: S) {

  /** Adds the element `sep` between each element of the sequence.
    * If the sequence has less than two elements, the collection is unchanged.
    *
    * @param sep the element to intersperse
    * @tparam B the element type of the returned collection
    * @return a new collection consisting of all elements of this collection
    *         interspersed with the element `sep`
    *
    * @example {{{
    * List(1, 2, 3, 4).intersperse(0) = List(1, 0, 2, 0, 3, 0, 4)
    * }}}
    */
  def intersperse[B >: seq.A, That](sep: B)(implicit bf: BuildFrom[C, B, That]): That =
    bf.fromSpecific(coll)(new View.Intersperse(seq(coll), sep))

  /** Adds the element `sep` between each element of the sequence,
    * prepending `start` and appending `end`.
    * If the sequence has less than two elements, returns `start +: this :+ end`.
    *
    * @param start the element to prepend
    * @param sep the element to intersperse
    * @param end the element to append
    * @tparam B the element type of the returned collection
    * @return a new collection consisting of all elements of this collection
    *         interspersed with the element `sep`, beginning with `start` and ending with `end`
    *
    * @example {{{
    *      List(1, 2, 3, 4).intersperse(-1, 0, 5) => List(-1, 1, 0, 2, 0, 3, 0, 4, 5)
    * }}}
    */
  def intersperse[B >: seq.A, That](start: B, sep: B, end: B)(implicit bf: BuildFrom[C, B, That]): That =
    bf.fromSpecific(coll)(new View.IntersperseSurround(seq(coll), start, sep, end))

  /** Produces a new sequence where all occurrences of some element are replaced by
    * a different element.
    *
    * @param elem        the element to replace
    * @param replacement the replacement element
    * @tparam B          the element type of the returned collection.
    * @return            a new sequence consisting of all elements of this sequence
    *                    except that all occurrences of `elem` are replaced by
    *                    `replacement`
    */
  def replaced[B >: seq.A, That](elem: B, replacement: B)(implicit bf: BuildFrom[C, B, That]): That =
    bf.fromSpecific(coll)(new collection.View.Map(seq(coll), (a: seq.A) => if (a == elem) replacement else a))

  /**
   * for improved readability, the integer index of an element in a Seq
   */
  type Index = Int

  /**
   * the integer index of an element in a circular Seq, any value is valid
   */
  type IndexO = Int

  private def index(i: IndexO): Index =
    java.lang.Math.floorMod(i, seq(coll).size)

  /** Considers the sequence circular and rotates it right by `step` places.
   *
   * @param step the number of places to be rotated to the right
   * @tparam B the element type of the returned collection
   * @return a new collection consisting of all elements of this collection
   *         circularly rotated by `step` places to the right.
   * @example {{{
   *      List(1, 2, 3, 4, 5).rotateRight(1) => List(5, 1, 2, 3, 4)
   * }}}
   */
  def rotateRight[B >: seq.A, That](step: Int)(implicit bf: BuildFrom[C, B, That]): That =
    if (seq(coll).isEmpty)
      bf.fromSpecific(coll)(collection.View.Empty)
    else {
      val j: Index = seq(coll).size - index(step)
      bf.fromSpecific(coll)(new collection.View.Drop(seq(coll), j) ++ new collection.View.Take(seq(coll), j))
    }

  /** Considers the sequence circular and rotates it left by `step` places.
   *
   * @param step the number of places to be rotated to the left
   * @tparam B the element type of the returned collection
   * @return a new collection consisting of all elements of this collection
   *         circularly rotated by `step` places to the left.
   * @example {{{
   *      List(1, 2, 3, 4, 5).rotateLeft(1) => List(2, 3, 4, 5, 1)
   * }}}
   */
  def rotateLeft[B >: seq.A, That](step: Int)(implicit bf: BuildFrom[C, B, That]): That =
    rotateRight(-step)

  /** Considers the sequence circular and rotates it to start with the element at `i` circular index.
   *
   * @param i the circular index of the element to be rotated at the start of the new collection
   * @tparam B the element type of the returned collection
   * @return a new collection consisting of all elements of this collection
   *         circularly rotated so to start with the element at circular index `i`.
   * @example {{{
   *      List(1, 2, 3, 4, 5).startAt(2) => List(3, 4, 5, 1, 2)
   * }}}
   */
  def startAt[B >: seq.A, That](i: IndexO)(implicit bf: BuildFrom[C, B, That]): That =
    rotateLeft(i)

}
