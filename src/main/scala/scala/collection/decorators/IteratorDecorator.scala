package scala.collection
package decorators

import scala.annotation.tailrec

class IteratorDecorator[A](val `this`: Iterator[A]) extends AnyVal {

  def intersperse[B >: A](sep: B): Iterator[B] = new Iterator[B] {
    var intersperseNext = false
    override def hasNext = intersperseNext || `this`.hasNext
    override def next() = {
      val elem = if (intersperseNext) sep else `this`.next()
      intersperseNext = !intersperseNext && `this`.hasNext
      elem
    }
  }

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
    * Constructs an iterator where consecutive elements are accumulated as
    * long as the output of f for each element doesn't change.
    * <pre>
    * Vector(1,2,2,3,3,3,2,2)
    * .iterator
    * .splitBy(identity)
    * .toList
    * </pre>
    * produces
    * <pre>
    * List(Seq(1),
    * Seq(2,2),
    * Seq(3,3,3),
    * Seq(2,2))
    * </pre>
    *
    * @param f the function to compute a key for an element
    * @tparam K the type of the computed key
    * @return an iterator of sequences of the consecutive elements with the
    *         same key in the original iterator
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
