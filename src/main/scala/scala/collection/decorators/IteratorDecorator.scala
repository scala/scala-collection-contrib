package scala.collection
package decorators

import scala.annotation.tailrec

class IteratorDecorator[A](val `this`: Iterator[A]) extends AnyVal {

  /**
    * Inserts a separator value between each element.
    *
    * {{{
    *   Iterator(1, 2, 3).intersperse(0) === Iterator(1, 0, 2, 0, 3)
    *   Iterator('a', 'b', 'c').intersperse(',') === Iterator('a', ',', 'b', ',', 'c')
    *   Iterator('a').intersperse(',') === Iterator('a')
    *   Iterator().intersperse(',') === Iterator()
    * }}}
    *
    * @param sep the separator value.
    * @return    The resulting iterator contains all elements from the source iterator, separated by the `sep` value.
    * @note      Reuse: $consumesIterator
    */
  def intersperse[B >: A](sep: B): Iterator[B] = new Iterator[B] {
    var intersperseNext = false
    override def hasNext = intersperseNext || `this`.hasNext
    override def next() = {
      val elem = if (intersperseNext) sep else `this`.next()
      intersperseNext = !intersperseNext && `this`.hasNext
      elem
    }
  }

  /**
    * Inserts a start value at the start of the iterator, a separator value between each element, and
    * an end value at the end of the iterator.
    *
    * {{{
    *   Iterator(1, 2, 3).intersperse(-1, 0, 99) === Iterator(-1, 1, 0, 2, 0, 3, 99)
    *   Iterator('a', 'b', 'c').intersperse('[', ',', ']') === Iterator('[', 'a', ',', 'b', ',', 'c', ']')
    *   Iterator('a').intersperse('[', ',', ']') === Iterator('[', 'a', ']')
    *   Iterator().intersperse('[', ',', ']') === Iterator('[', ']')
    * }}}
    *
    * @param start the starting value.
    * @param sep   the separator value.
    * @param end   the ending value.
    * @return      The resulting iterator
    *              begins with the `start` value and ends with the `end` value.
    *              Inside, are all elements from the source iterator separated by
    *              the `sep` value.
    * @note        Reuse: $consumesIterator
    */
  def intersperse[B >: A](start: B, sep: B, end: B): Iterator[B] = new Iterator[B] {
    var started = false
    var finished = false
    var intersperseNext = false

    override def hasNext: Boolean = !finished || intersperseNext || `this`.hasNext

    override def next(): B =
      if (!started) {
        started = true
        start
      } else if (intersperseNext) {
        intersperseNext = false
        sep
      } else if (`this`.hasNext) {
        val elem = `this`.next()
        intersperseNext = `this`.hasNext
        elem
      } else if (!finished) {
        finished = true
        end
      } else {
        throw new NoSuchElementException("next on empty iterator")
      }
  }

  /**
    * Folds elements with combination function `op` until
    * all elements have been processed, or `op` returns `None`.
    * $mayNotTerminateInf
    *
    * {{{
    *   def sumOp(acc: Int, e: Int): Option[Int] = if (e == 4) None else Some(acc + e)
    *   Iterator.empty.foldSomeLeft(0)(sumOp) === 0
    *   Iterator(1, 2, 3).foldSomeLeft(0)(sumOp) === 6
    *   Iterator(1, 2, 3, 4, 5).foldSomeLeft(0)(sumOp) === 6
    * }}}
    *
    * @param z the start value
    * @param op the binary operator
    * @tparam B the result type of the binary operator
    * @return the result of evaluating `op` on the previous result of `op` (or `z` for the first time) and
    *         elements of the source iterator, stopping when all the elements have been
    *         iterated or earlier when `op` returns `None`
    * @note Reuse: $consumesIterator
    */
  def foldSomeLeft[B](z: B)(op: (B, A) => Option[B]): B = {
    var result: B = z
    while (`this`.hasNext) {
      op(result, `this`.next()) match {
        case Some(v) => result = v
        case None => return result
      }
    }
    result
  }

  /**
    * $mayNotTerminateInf
    * @note Reuse: $consumesIterator
    */
  def lazyFoldLeft[B](z: B)(op: (B, => A) => B): B = {
    var result = z
    var finished = false
    while (`this`.hasNext && !finished) {
      var nextEvaluated = false
      val elem = `this`.next()
      def getNext = { nextEvaluated = true; elem }
      val acc = op(result, getNext)
      finished = !nextEvaluated && acc == result
      result = acc
    }
    result
  }

  /**
    * $mayNotTerminateInf
    * @note Reuse: $consumesIterator
    */
  def lazyFoldRight[B](z: B)(op: A => Either[B, B => B]): B = {

    def chainEval(x: B, fs: immutable.List[B => B]): B =
      fs.foldLeft(x)((x, f) => f(x))

    @tailrec
    def loop(fs: immutable.List[B => B]): B = {
      if (`this`.hasNext) {
        op(`this`.next()) match {
          case Left(v) => chainEval(v, fs)
          case Right(g) => loop(g :: fs)
        }
      } else {
        chainEval(z, fs)
      }
    }

    loop(immutable.List.empty)
  }

  /**
    * Constructs an iterator in which each element is a the sequence of accumulated elements
    * from the source iterator that have the same key, where the key is calculated by `f`.
    *
    * {{{
    * Iterator(1,2,2,3,3,3,2,2).splitBy(identity) === Iterator(Seq(1), Seq(2,2), Seq(3,3,3), Seq(2,2))
    * Iterator((1,1), (1,2), (2, 3)).splitBy(_._1) === Iterator(Seq((1,1), (1,2)), Seq((2,3)))
    * }}}
    *
    * @param f the function to compute a key for an element
    * @tparam K the type of the computed key
    * @return an iterator of sequences of the consecutive elements with the
    *         same key in the original iterator
    * @note Reuse: $consumesIterator
    */
  def splitBy[K](f: A => K): Iterator[immutable.Seq[A]] =
    new AbstractIterator[immutable.Seq[A]] {
      private var hd: A = _
      private var hdKey: K = _
      private var hdDefined: Boolean = false

      override def hasNext: Boolean = hdDefined || `this`.hasNext

      override def next(): immutable.Seq[A] = {
        if (hasNext) {
          val seq = Vector.newBuilder[A]
          if (hdDefined) {
            seq += hd
          } else {
            val init = `this`.next()
            hd = init
            hdKey = f(init)
            hdDefined = true
            seq += init
          }
          var hadSameKey = true
          while (`this`.hasNext && hadSameKey) {
            val el = `this`.next()
            hdDefined = true
            val key = f(el)
            if (key == hdKey) {
              seq += el
            } else {
              hadSameKey = false
              hdKey = key
              hd = el
            }
          }
          if (hadSameKey) {
            hdDefined = false
          }
          seq.result()
        } else {
          Iterator.empty.next()
        }
      }
    }
}
