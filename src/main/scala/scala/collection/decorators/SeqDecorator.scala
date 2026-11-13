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

  // ---------------------------------------------------------------------------
  // Circular (ring) operations — treat the sequence as a ring where any `Int`
  // is a valid index, wrapping modulo the size. Adapted from scala-tessella/ring-seq.
  // Methods that mirror standard `Seq` operations are suffixed with `O` (ring).
  // ---------------------------------------------------------------------------

  private def ringVector: Vector[seq.A] = seq(coll).iterator.toVector

  private def mod(i: Int, n: Int): Int = java.lang.Math.floorMod(i, n)

  private def rotate[A](v: Vector[A], k: Int): Vector[A] = v.drop(k) ++ v.take(k)

  private def reflect[A](v: Vector[A], i: Int = 0): Vector[A] = {
    val n = v.size
    if (n == 0) v else rotate(v, mod(i + 1, n)).reverse
  }

  private def extendedRing(v: Vector[seq.A], extra: Int): Vector[seq.A] = {
    val n = v.size
    Vector.tabulate(n + math.max(0, extra))(i => v(i % n))
  }

  private def buildFromIter[That](it: IterableOnce[seq.A])(implicit bf: BuildFrom[C, seq.A, That]): That =
    bf.fromSpecific(coll)(it)

  private def emptyColl[That](implicit bf: BuildFrom[C, seq.A, That]): That =
    buildFromIter(Iterator.empty)

  /** Gets the element at a circular index, wrapping modulo the size.
    *
    * @param i the circular index (any `Int`, normalised to `[0, size)`)
    * @throws java.lang.ArithmeticException if the sequence is empty
    * @return the element at circular position `i`
    *
    * @example {{{
    * List(0, 1, 2).applyO(3)  = 0
    * List(0, 1, 2).applyO(-1) = 2
    * }}}
    */
  def applyO(i: Int): seq.A = {
    val v = ringVector
    v(mod(i, v.size))
  }

  /** Rotates the sequence to the right by `step` positions.
    * Negative values rotate to the left. The result is empty iff the source is empty.
    *
    * @param step the circular distance between each new and old position
    * @tparam That the type of the returned collection
    * @return a new collection with all elements rotated to the right by `step` places
    *
    * @example {{{
    * List(0, 1, 2).rotateRight(1)  = List(2, 0, 1)
    * List(0, 1, 2).rotateRight(-1) = List(1, 2, 0)
    * }}}
    */
  def rotateRight[That](step: Int)(implicit bf: BuildFrom[C, seq.A, That]): That = {
    val v = ringVector
    val n = v.size
    if (n == 0) emptyColl
    else buildFromIter(rotate(v, n - mod(step, n)).iterator)
  }

  /** Rotates the sequence to the left by `step` positions.
    * Negative values rotate to the right. The result is empty iff the source is empty.
    *
    * @param step the circular distance between each old and new position
    * @tparam That the type of the returned collection
    * @return a new collection with all elements rotated to the left by `step` places
    *
    * @example {{{
    * List(0, 1, 2).rotateLeft(1)  = List(1, 2, 0)
    * List(0, 1, 2).rotateLeft(-1) = List(2, 0, 1)
    * }}}
    */
  def rotateLeft[That](step: Int)(implicit bf: BuildFrom[C, seq.A, That]): That =
    rotateRight(-step)

  /** Rotates the sequence so that it starts at the given circular index.
    * Equivalent to [[rotateLeft]].
    *
    * @param i the circular index that will be the first element of the result
    * @tparam That the type of the returned collection
    * @return a new collection consisting of all elements rotated to start at circular index `i`
    *
    * @example {{{
    * List(0, 1, 2).startAt(1)  = List(1, 2, 0)
    * List(0, 1, 2).startAt(-1) = List(2, 0, 1)
    * }}}
    */
  def startAt[That](i: Int)(implicit bf: BuildFrom[C, seq.A, That]): That =
    rotateLeft(i)

  /** Reflects the sequence with axis through the given circular index.
    * Equivalent to `startAt(i + 1).reverse`.
    *
    * @param i the circular index of the reflection axis; defaults to `0`
    * @tparam That the type of the returned collection
    * @return a new collection consisting of all elements reversed around circular index `i`
    *
    * @example {{{
    * List(0, 1, 2).reflectAt()  = List(0, 2, 1)
    * List(0, 1, 2).reflectAt(1) = List(1, 0, 2)
    * }}}
    */
  def reflectAt[That](i: Int = 0)(implicit bf: BuildFrom[C, seq.A, That]): That = {
    val v = ringVector
    if (v.isEmpty) emptyColl else buildFromIter(reflect(v, i).iterator)
  }

  /** Computes the length of the longest segment starting at a circular index
    * whose elements all satisfy a predicate.
    *
    * @param p    the predicate used to test elements
    * @param from the circular index to start at; defaults to `0`
    * @return the length of the longest segment of this sequence starting from
    *         circular index `from` such that every element of the segment
    *         satisfies `p`
    *
    * @example {{{
    * List(0, 1, 2).segmentLengthO(_ % 2 == 0, 2) = 2
    * }}}
    */
  def segmentLengthO(p: seq.A => Boolean, from: Int = 0): Int = {
    val v = ringVector
    val n = v.size
    if (n == 0) 0
    else rotate(v, mod(from, n)).iterator.takeWhile(p).size
  }

  /** Selects the longest prefix of elements starting at a circular index
    * that satisfy a predicate.
    *
    * @param p    the predicate used to test elements
    * @param from the circular index to start at; defaults to `0`
    * @tparam That the type of the returned collection
    * @return the longest prefix of this circular sequence starting at `from`
    *         whose elements all satisfy `p`
    *
    * @example {{{
    * List(0, 1, 2, 3, 4).takeWhileO(_ < 3, 1) = List(1, 2)
    * }}}
    */
  def takeWhileO[That](p: seq.A => Boolean, from: Int = 0)(implicit bf: BuildFrom[C, seq.A, That]): That = {
    val v = ringVector
    val n = v.size
    if (n == 0) emptyColl
    else buildFromIter(rotate(v, mod(from, n)).iterator.takeWhile(p))
  }

  /** Drops the longest prefix of elements starting at a circular index
    * that satisfy a predicate.
    *
    * @param p    the predicate used to test elements
    * @param from the circular index to start at; defaults to `0`
    * @tparam That the type of the returned collection
    * @return the remainder of this circular sequence after dropping the
    *         longest prefix starting at `from` whose elements satisfy `p`
    *
    * @example {{{
    * List(0, 1, 2, 3, 4).dropWhileO(_ < 3, 1) = List(3, 4, 0)
    * }}}
    */
  def dropWhileO[That](p: seq.A => Boolean, from: Int = 0)(implicit bf: BuildFrom[C, seq.A, That]): That = {
    val v = ringVector
    val n = v.size
    if (n == 0) emptyColl
    else buildFromIter(rotate(v, mod(from, n)).iterator.dropWhile(p))
  }

  /** Splits this circular sequence into a prefix/suffix pair at the first
    * element, starting from a circular index, that does not satisfy the predicate.
    *
    * @param p    the predicate used to test elements
    * @param from the circular index to start at; defaults to `0`
    * @tparam That the type of the two returned collections
    * @return a pair `(takeWhileO(p, from), dropWhileO(p, from))`
    *
    * @example {{{
    * List(0, 1, 2, 3, 4).spanO(_ < 3, 1) = (List(1, 2), List(3, 4, 0))
    * }}}
    */
  def spanO[That](p: seq.A => Boolean, from: Int = 0)(implicit bf: BuildFrom[C, seq.A, That]): (That, That) = {
    val v = ringVector
    val n = v.size
    if (n == 0) (emptyColl, emptyColl)
    else {
      val (taken, dropped) = rotate(v, mod(from, n)).span(p)
      (buildFromIter(taken.iterator), buildFromIter(dropped.iterator))
    }
  }

  /** Selects a circular slice. Unlike `slice`, `from` and `until` can be any
    * `Int` and the result can be longer than the source.
    *
    * @param from  the circular start index (inclusive)
    * @param until the circular end index (exclusive)
    * @tparam That the type of the returned collection
    * @return a new collection containing the elements from circular index
    *         `from` up to (but not including) circular index `until`; empty
    *         if the source is empty or if `from >= until`
    *
    * @example {{{
    * List(0, 1, 2).sliceO(-1, 4) = List(2, 0, 1, 2, 0)
    * }}}
    */
  def sliceO[That](from: Int, until: Int)(implicit bf: BuildFrom[C, seq.A, That]): That = {
    val v = ringVector
    val n = v.size
    if (n == 0 || from >= until) emptyColl
    else {
      val rotated = rotate(v, mod(from, n))
      buildFromIter(Iterator.continually(rotated).flatten.take(until - from))
    }
  }

  /** Tests whether this circular sequence contains a given sequence as a slice.
    *
    * @param that the sequence to test
    * @tparam B a supertype of the element type
    * @return `true` if some circular slice of this sequence equals `that`,
    *         otherwise `false`
    *
    * @example {{{
    * List(0, 1, 2).containsSliceO(Seq(2, 0, 1, 2, 0)) = true
    * }}}
    */
  def containsSliceO[B >: seq.A](that: scala.collection.Seq[B]): Boolean = {
    val v = ringVector
    if (v.isEmpty) that.isEmpty
    else extendedRing(v, that.size - 1).containsSlice(that)
  }

  /** Finds the first circular index at or after `from` where this sequence
    * contains `that` as a slice.
    *
    * @param that the sequence to test
    * @param from the circular index to start searching from; defaults to `0`
    * @tparam B a supertype of the element type
    * @return the first index `>= from` at which the ring starts with `that`,
    *         or `-1` if no such position exists
    *
    * @example {{{
    * List(0, 1, 2).indexOfSliceO(Seq(2, 0, 1, 2, 0)) = 2
    * }}}
    */
  def indexOfSliceO[B >: seq.A](that: scala.collection.Seq[B], from: Int = 0): Int = {
    val v = ringVector
    val n = v.size
    if (n == 0) if (that.isEmpty) 0 else -1
    else extendedRing(v, that.size - 1).indexOfSlice(that, mod(from, n))
  }

  /** Finds the last circular index at or before `end` where this sequence
    * contains `that` as a slice.
    *
    * @param that the sequence to test
    * @param end  the circular index to stop searching at; defaults to `-1`
    *             (the last index of the ring)
    * @tparam B a supertype of the element type
    * @return the last index `<= end` at which the ring starts with `that`,
    *         or `-1` if no such position exists
    *
    * @example {{{
    * List(0, 1, 2, 0, 1, 2).lastIndexOfSliceO(Seq(2, 0)) = 5
    * }}}
    */
  def lastIndexOfSliceO[B >: seq.A](that: scala.collection.Seq[B], end: Int = -1): Int = {
    val v = ringVector
    val n = v.size
    if (n == 0) if (that.isEmpty) 0 else -1
    else extendedRing(v, that.size - 1).lastIndexOfSlice(that, mod(end, n))
  }

  /** Groups elements in fixed-size blocks by passing a sliding window over
    * the circular sequence.
    *
    * @param size the number of elements per group
    * @param step the distance between the first elements of successive groups;
    *             defaults to `1`
    * @tparam That the type of each window
    * @return an iterator producing circular windows of size `size`; empty when
    *         the ring is empty
    *
    * @example {{{
    * List(0, 1, 2).slidingO(2) =
    *   Iterator(List(0, 1), List(1, 2), List(2, 0))
    * }}}
    */
  def slidingO[That](size: Int, step: Int = 1)(implicit bf: BuildFrom[C, seq.A, That]): Iterator[That] = {
    val v = ringVector
    val n = v.size
    if (n == 0) Iterator.empty
    else extendedRing(v, step * (n - 1) + size - n).sliding(size, step).map(buildFromIter(_))
  }

  /** Partitions this circular sequence into non-overlapping fixed-size blocks.
    * Unlike standard `grouped`, the last block wraps across the seam so every
    * block has exactly `size` elements.
    *
    * @param size the number of elements per block; must be positive
    * @tparam That the type of each block
    * @return an iterator producing `ceil(n / size)` blocks of size `size`;
    *         empty when the ring is empty
    *
    * @example {{{
    * List(0, 1, 2, 3, 4).groupedO(2) =
    *   Iterator(List(0, 1), List(2, 3), List(4, 0))
    * }}}
    */
  def groupedO[That](size: Int)(implicit bf: BuildFrom[C, seq.A, That]): Iterator[That] = {
    val v = ringVector
    val n = v.size
    if (n == 0) Iterator.empty
    else {
      val count = (n + size - 1) / size
      extendedRing(v, count * size - n).grouped(size).map(buildFromIter(_))
    }
  }

  /** Iterates over elements paired with their original circular index,
    * starting at a circular index.
    *
    * @param from the circular index to start at; defaults to `0`
    * @return an iterator of `(element, index)` pairs of length `n`, where
    *         each index is in `[0, n)`; empty when the ring is empty
    *
    * @example {{{
    * List('a', 'b', 'c').zipWithIndexO(1).toList =
    *   List(('b', 1), ('c', 2), ('a', 0))
    * }}}
    */
  def zipWithIndexO(from: Int = 0): Iterator[(seq.A, Int)] = {
    val v = ringVector
    val n = v.size
    if (n == 0) Iterator.empty
    else {
      val start = mod(from, n)
      rotate(v, start).iterator.zipWithIndex.map { case (a, i) => (a, (start + i) % n) }
    }
  }

  /** Computes all rotations of this circular sequence, starting from itself
    * and moving one step to the right.
    *
    * @tparam That the type of each rotation
    * @return an iterator of the `n` rotations; for an empty ring yields the
    *         single empty rotation
    *
    * @example {{{
    * List(0, 1, 2).rotations =
    *   Iterator(List(0, 1, 2), List(1, 2, 0), List(2, 0, 1))
    * }}}
    */
  def rotations[That](implicit bf: BuildFrom[C, seq.A, That]): Iterator[That] = {
    val v = ringVector
    val n = v.size
    if (n == 0) Iterator.single(emptyColl)
    else (0 until n).iterator.map(k => buildFromIter(rotate(v, k).iterator))
  }

  /** Computes the two reflections of this circular sequence: the original and
    * its reflection through the axis at circular index `0`.
    *
    * @tparam That the type of each reflection
    * @return an iterator of length 2; for an empty ring yields the single
    *         empty reflection
    *
    * @example {{{
    * List(0, 1, 2).reflections =
    *   Iterator(List(0, 1, 2), List(0, 2, 1))
    * }}}
    */
  def reflections[That](implicit bf: BuildFrom[C, seq.A, That]): Iterator[That] = {
    val v = ringVector
    if (v.isEmpty) Iterator.single(emptyColl)
    else Iterator(buildFromIter(v.iterator), buildFromIter(reflect(v).iterator))
  }

  /** Computes the two reversions of this circular sequence: the original and
    * its reverse.
    *
    * @tparam That the type of each reversion
    * @return an iterator of length 2; for an empty ring yields the single
    *         empty reversion
    *
    * @example {{{
    * List(0, 1, 2).reversions =
    *   Iterator(List(0, 1, 2), List(2, 1, 0))
    * }}}
    */
  def reversions[That](implicit bf: BuildFrom[C, seq.A, That]): Iterator[That] = {
    val v = ringVector
    if (v.isEmpty) Iterator.single(emptyColl)
    else Iterator(buildFromIter(v.iterator), buildFromIter(v.reverseIterator))
  }

  /** Computes all `2n` rotations and reflections of this circular sequence:
    * first all rotations of the original, then all rotations of its reflection.
    *
    * @tparam That the type of each variant
    * @return an iterator of length `2 * n`; for an empty ring yields the single
    *         empty variant
    *
    * @example {{{
    * List(0, 1, 2).rotationsAndReflections.toList =
    *   List(List(0, 1, 2), List(1, 2, 0), List(2, 0, 1),
    *        List(0, 2, 1), List(2, 1, 0), List(1, 0, 2))
    * }}}
    */
  def rotationsAndReflections[That](implicit bf: BuildFrom[C, seq.A, That]): Iterator[That] = {
    val v = ringVector
    val n = v.size
    if (n == 0) Iterator.single(emptyColl)
    else {
      val reflected = reflect(v)
      def rotationsOf(source: Vector[seq.A]): Iterator[That] =
        (0 until n).iterator.map(j => buildFromIter(rotate(source, j).iterator))
      rotationsOf(v) ++ rotationsOf(reflected)
    }
  }

  /** Tests whether this circular sequence is a rotation of a given sequence.
    *
    * @param that the sequence to test
    * @tparam B a supertype of the element type
    * @return `true` if some rotation of this sequence equals `that`,
    *         otherwise `false`
    *
    * @example {{{
    * List(0, 1, 2).isRotationOf(Seq(1, 2, 0)) = true
    * }}}
    */
  def isRotationOf[B >: seq.A](that: scala.collection.Seq[B]): Boolean = {
    val v = ringVector
    v.size == that.size && (v.isEmpty || (v ++ v.init).containsSlice(that))
  }

  /** Tests whether this circular sequence is a reflection of a given sequence.
    *
    * @param that the sequence to test
    * @tparam B a supertype of the element type
    * @return `true` if this sequence or its reflection equals `that`,
    *         otherwise `false`
    *
    * @example {{{
    * List(0, 1, 2).isReflectionOf(Seq(0, 2, 1)) = true
    * }}}
    */
  def isReflectionOf[B >: seq.A](that: scala.collection.Seq[B]): Boolean = {
    val v = ringVector
    v.size == that.size && (that.sameElements(v) || that.sameElements(reflect(v)))
  }

  /** Tests whether this circular sequence is a reversion of a given sequence.
    *
    * @param that the sequence to test
    * @tparam B a supertype of the element type
    * @return `true` if this sequence or its reverse equals `that`,
    *         otherwise `false`
    *
    * @example {{{
    * List(0, 1, 2).isReversionOf(Seq(2, 1, 0)) = true
    * }}}
    */
  def isReversionOf[B >: seq.A](that: scala.collection.Seq[B]): Boolean = {
    val v = ringVector
    v.size == that.size && (that.sameElements(v) || that.sameElements(v.reverseIterator))
  }

  /** Tests whether this circular sequence is a rotation or a reflection of
    * a given sequence.
    *
    * @param that the sequence to test
    * @tparam B a supertype of the element type
    * @return `true` if some rotation of this sequence or of its reflection
    *         equals `that`, otherwise `false`
    *
    * @example {{{
    * List(0, 1, 2).isRotationOrReflectionOf(Seq(2, 0, 1)) = true
    * }}}
    */
  def isRotationOrReflectionOf[B >: seq.A](that: scala.collection.Seq[B]): Boolean = {
    val v = ringVector
    if (v.size != that.size) false
    else if (v.isEmpty) true
    else {
      def containsAsRotation(source: Vector[seq.A]): Boolean =
        (source ++ source.init).containsSlice(that)
      containsAsRotation(v) || containsAsRotation(reflect(v))
    }
  }

  /** Finds the rotation offset that aligns this circular sequence with a
    * given sequence.
    *
    * @param that the sequence to align to
    * @tparam B a supertype of the element type
    * @return `Some(k)` such that rotating this left by `k` equals `that`,
    *         or `None` if sizes differ or no rotation matches
    *
    * @example {{{
    * List(0, 1, 2).alignTo(Seq(2, 0, 1)) = Some(2)
    * }}}
    */
  def alignTo[B >: seq.A](that: scala.collection.Seq[B]): Option[Int] = {
    val v = ringVector
    if (v.size != that.size) None
    else if (v.isEmpty) Some(0)
    else {
      val idx = (v ++ v.init).indexOfSlice(that)
      Option.when(idx >= 0)(idx)
    }
  }

  /** The number of positions at which the corresponding elements of this
    * sequence and `that` differ (Hamming distance).
    *
    * @param that the sequence to compare against; must have the same size
    * @tparam B a supertype of the element type
    * @throws java.lang.IllegalArgumentException if `that` has a different size
    * @return the number of positions where the two sequences differ
    *
    * @example {{{
    * List(1, 0, 1, 1).hammingDistance(Seq(1, 1, 0, 1)) = 2
    * }}}
    */
  def hammingDistance[B >: seq.A](that: scala.collection.Seq[B]): Int = {
    val v = ringVector
    require(v.size == that.size, "sequences must have the same size")
    v.iterator.zip(that).count { case (a, b) => a != b }
  }

  /** The minimum Hamming distance over all rotations of this circular sequence.
    * Equals `0` iff `that` is a rotation of this sequence.
    *
    * @param that the sequence to compare against; must have the same size
    * @tparam B a supertype of the element type
    * @throws java.lang.IllegalArgumentException if `that` has a different size
    * @return the smallest number of positional mismatches across all rotations
    *
    * @example {{{
    * List(1, 0, 1, 1).minRotationalHammingDistance(Seq(1, 1, 0, 1)) = 0
    * }}}
    */
  def minRotationalHammingDistance[B >: seq.A](that: scala.collection.Seq[B]): Int = {
    val v = ringVector
    val n = v.size
    require(n == that.size, "sequences must have the same size")
    if (n == 0) 0
    else {
      val b = that.toVector
      // Tight while loop with early exit on best-so-far; avoids materialising
      // each of the n rotations just to compare.
      var best = Int.MaxValue
      var k = 0
      while (k < n && best != 0) {
        var count = 0
        var i = 0
        var ai = k
        while (i < n && count < best) {
          if (v(ai) != b(i)) count += 1
          ai += 1
          if (ai == n) ai = 0
          i += 1
        }
        if (count < best) best = count
        k += 1
      }
      best
    }
  }

  /** The starting index of the lexicographically smallest rotation of this
    * circular sequence (Booth's algorithm, O(n)).
    *
    * @tparam B a supertype of the element type for which an `Ordering` is available
    * @return the index in `[0, n)` such that `startAt(canonicalIndex)` is the
    *         lex-smallest of all rotations; `0` for empty or singleton sequences
    *
    * @example {{{
    * List(2, 0, 1).canonicalIndex = 1
    * }}}
    */
  def canonicalIndex[B >: seq.A](implicit ord: Ordering[B]): Int = {
    val v = ringVector
    if (v.size <= 1) 0 else SeqDecorator.leastRotationBooth[B](v)
  }

  /** The lexicographically smallest rotation of this circular sequence
    * (necklace canonical form). Two circular sequences are rotations of each
    * other iff their canonical forms are equal.
    *
    * @tparam That the type of the returned collection
    * @return the rotation that is lexicographically smallest
    *
    * @example {{{
    * List(2, 0, 1).canonical = List(0, 1, 2)
    * }}}
    */
  def canonical[That](implicit ord: Ordering[seq.A], bf: BuildFrom[C, seq.A, That]): That =
    startAt(canonicalIndex[seq.A])

  /** The lexicographically smallest representative of this circular sequence
    * under both rotation and reflection (bracelet canonical form). Two circular
    * sequences belong to the same bracelet equivalence class iff their bracelet
    * forms are equal.
    *
    * @tparam That the type of the returned collection
    * @return the smaller of `canonical` and `reflectAt().canonical`, by
    *         lexicographic ordering
    *
    * @example {{{
    * List(2, 0, 1).bracelet = List(0, 1, 2)
    * List(1, 0, 2).bracelet = List(0, 1, 2)  // the reflection wins
    * }}}
    */
  def bracelet[That](implicit ord: Ordering[seq.A], bf: BuildFrom[C, seq.A, That]): That = {
    val v = ringVector
    if (v.size <= 1) buildFromIter(v.iterator)
    else {
      val a = SeqDecorator.canonicalRotation[seq.A](v)
      val b = SeqDecorator.canonicalRotation[seq.A](reflect(v))
      implicit val vectorOrd: Ordering[Vector[seq.A]] = Ordering.Implicits.seqOrdering
      buildFromIter((if (vectorOrd.lteq(a, b)) a else b).iterator)
    }
  }

  /** The order of rotational symmetry of this circular sequence: how many
    * times it matches itself over a full rotation.
    *
    * @return an integer between `1` and the size of the sequence; `1` means
    *         no rotational symmetry, `n` means all elements are equal; for an
    *         empty or singleton sequence, `1`
    *
    * @example {{{
    * List(0, 1, 2, 0, 1, 2).rotationalSymmetry = 2
    * }}}
    */
  def rotationalSymmetry: Int = {
    val v = ringVector
    val n = v.size
    if (n < 2) 1
    else {
      val smallestPeriod =
        (1 to n).find(shift => n % shift == 0 && (0 until n - shift).forall(i => v(i) == v(i + shift)))
      n / smallestPeriod.getOrElse(n)
    }
  }

  /** The indices of each element of this circular sequence that lies on an
    * axis of reflectional symmetry.
    *
    * @return the indices where an axis of reflectional symmetry passes
    *         (each such axis splits the ring into two identical halves);
    *         `Nil` for the empty sequence
    *
    * @example {{{
    * List(2, 1, 2, 2, 1, 2, 2, 1, 2, 2, 1, 2).symmetryIndices = List(0, 3, 6, 9)
    * }}}
    */
  def symmetryIndices: List[Int] = {
    val v = ringVector
    val n = v.size
    if (n == 0) Nil
    else {
      val reversed = v.reverse
      (0 until n).filter(shift => (0 until n).forall(i => v(i) == reversed((i + shift) % n))).toList
    }
  }

  /** The order of reflectional (mirror) symmetry of this circular sequence:
    * the number of axes along which it is mirror-symmetric.
    *
    * @return the number `>= 0` of reflections that leave this circular
    *         sequence unchanged
    *
    * @example {{{
    * List(2, 1, 2, 2, 1, 2, 2, 1, 2, 2, 1, 2).symmetry = 4
    * }}}
    */
  def symmetry: Int = symmetryIndices.size

}

private object SeqDecorator {

  /** Booth's O(n) least-rotation algorithm. Returns the start index of the
    * lexicographically smallest rotation of `s`.
    */
  def leastRotationBooth[A](s: Vector[A])(implicit ord: Ordering[A]): Int = {
    val n = s.size
    val len = 2 * n
    val f = Array.fill(len)(-1)
    var k = 0
    var j = 1
    while (j < len) {
      val sj = s(j % n)
      var i = f(j - k - 1)
      while (i != -1 && !ord.equiv(sj, s((k + i + 1) % n))) {
        if (ord.lt(sj, s((k + i + 1) % n))) k = j - i - 1
        i = f(i)
      }
      if (i == -1 && !ord.equiv(sj, s((k + i + 1) % n))) {
        if (ord.lt(sj, s((k + i + 1) % n))) k = j
        f(j - k) = -1
      } else {
        f(j - k) = i + 1
      }
      j += 1
    }
    k
  }

  def canonicalRotation[A](s: Vector[A])(implicit ord: Ordering[A]): Vector[A] = {
    val k = leastRotationBooth[A](s)
    if (k == 0) s else s.drop(k) ++ s.take(k)
  }

}
