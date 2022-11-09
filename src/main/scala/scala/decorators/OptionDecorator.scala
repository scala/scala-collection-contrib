package scala.decorators

class OptionDecorator[A](private val opt: Option[A]) extends AnyVal {
  /** Evaluates the argument if the option is empty and returns the option unmodified
    *
    * This is useful to perform an operation for its side-effect, while still allowing chaining,
    * much like [[Option#tapEach]] does, but for when the option empty rather than defined
    * @example {{{
    *   def maybeAdd(firstOption: Option[Int], secondOption: Option[Int]) = 
    *     for {
    *       first <- firstOption.onEmpty(println("first was empty"))
    *       second <- secondOption.onEmpty(println("second was empty"))
    *     }   yield first + second
    * }}}
    * @tparam U a dummy type, that allows you to pass in any block, regardless of its type
    * @param f the code to evaluate when the option is empty
    * @return the source option unmodified
    */
  def onEmpty[U](f: => U): Option[A] = {
    if (opt.isEmpty) f
    opt
  }
}
