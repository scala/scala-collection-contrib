package scala.collection
package decorators

/**
  * @param coll the decorated collection
  * @param seq evidence that type `C` is a sequence
  * @tparam C type of the decorated collection (e.g. `List[Int]`, `String`, etc.)
  */
class SeqDecorator[C, S <: HasSeqOps[C]](coll: C)(implicit val seq: S) {

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
    bf.fromSpecificIterable(coll)(new View.Intersperse(seq(coll), sep))

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
    bf.fromSpecificIterable(coll)(new View.IntersperseSurround(seq(coll), start, sep, end))

  /** Splits this collection into groups according to the given predicate.
    *
    * @param p the predicate used to discriminate elements
    * @return A nested collection with groups of elements,
    *         opening new groups whenever the predicate
    *         changes the return type
    *
    * @example {{{
    * // Example 1: Split a list of integers into groups that are even / odd
    * List(1, 2, 4, 6, 7).splitWith(i => i % 2 == 0) => List(List(1), List(2, 4, 6), List(7))
    *
    * // Example 2: Split a list of chars into groups that are upper case or lower case
    * List('a', 'b', 'C', 'D', 'e', 'f').splitWith(_.isUpper) => List(List('a', 'b'), List('C', 'D'), List('e', 'f'))
    * }}}
    */
  def splitWith[Group, That](p: seq.A => Boolean)(implicit bfGroup: BuildFrom[C, seq.A, Group], bfThat: BuildFrom[C, Group, That]): That = {
    def newGroupBuilder() = bfGroup.newBuilder(coll)

    val groups: mutable.Builder[Group, That] = bfThat.newBuilder(coll)
    val it: Iterator[seq.A] = seq(coll).iterator

    var currentGroup = newGroupBuilder()
    var lastTestResult = Option.empty[Boolean]

    while (it.hasNext) {
      val elem = it.next()
      val currentTest = p(elem)

      lastTestResult match {
        case None =>
          currentGroup.addOne(elem)
        case Some(lastTest) if currentTest == lastTest =>
          currentGroup.addOne(elem)
        case Some(_) =>
          groups.addOne(currentGroup.result())
          currentGroup = newGroupBuilder().addOne(elem)
      }

      lastTestResult = Some(currentTest)
    }

    groups.addOne(currentGroup.result()).result()
  }

}
