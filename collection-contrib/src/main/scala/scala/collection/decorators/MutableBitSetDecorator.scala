package scala.collection.decorators

import scala.collection.{BitSetOps, mutable}

class MutableBitSetDecorator(protected val bs: mutable.BitSet) {

  import BitSetDecorator._
  import BitSetOps._

  /**
    * Updates this BitSet to the left shift of itself by the given shift distance.
    * The shift distance may be negative, in which case this method performs a right shift.
    * @param shiftBy shift distance, in bits
    * @return the BitSet itself
    */
  def <<=(shiftBy: Int): mutable.BitSet = {

    if (bs.nwords == 0 || bs.nwords == 1 && bs.word(0) == 0) ()
    else if (shiftBy > 0) shiftLeftInPlace(shiftBy)
    else if (shiftBy < 0) shiftRightInPlace(-shiftBy)

    bs
  }

  /**
    * Updates this BitSet to the right shift of itself by the given shift distance.
    * The shift distance may be negative, in which case this method performs a left shift.
    * @param shiftBy shift distance, in bits
    * @return the BitSet itself
    */
  def >>=(shiftBy: Int): mutable.BitSet = {

    if (bs.nwords == 0 || bs.nwords == 1 && bs.word(0) == 0) ()
    else if (shiftBy > 0) shiftRightInPlace(shiftBy)
    else if (shiftBy < 0) shiftLeftInPlace(-shiftBy)

    bs
  }

  private def shiftLeftInPlace(shiftBy: Int): Unit = {

    val bitOffset = shiftBy & WordMask
    val wordOffset = shiftBy >>> LogWL

    var significantWordCount = bs.nwords
    while (significantWordCount > 0 && bs.word(significantWordCount - 1) == 0) {
      significantWordCount -= 1
    }

    if (bitOffset == 0) {
      val newSize = significantWordCount + wordOffset
      require(newSize <= MaxSize)
      ensureCapacity(newSize)
      System.arraycopy(bs.elems, 0, bs.elems, wordOffset, significantWordCount)
    } else {
      val revBitOffset = WordLength - bitOffset
      val extraBits = bs.elems(significantWordCount - 1) >>> revBitOffset
      val extraWordCount = if (extraBits == 0) 0 else 1
      val newSize = significantWordCount + wordOffset + extraWordCount
      require(newSize <= MaxSize)
      ensureCapacity(newSize)
      var i = significantWordCount - 1
      var previous = bs.elems(i)
      while (i > 0) {
        val current = bs.elems(i - 1)
        bs.elems(i + wordOffset) = (current >>> revBitOffset) | (previous << bitOffset)
        previous = current
        i -= 1
      }
      bs.elems(wordOffset) = previous << bitOffset
      if (extraWordCount != 0) bs.elems(newSize - 1) = extraBits
    }
    java.util.Arrays.fill(bs.elems, 0, wordOffset, 0)
  }

  private def shiftRightInPlace(shiftBy: Int): Unit = {

    val bitOffset = shiftBy & WordMask

    if (bitOffset == 0) {
      val wordOffset = shiftBy >>> LogWL
      val newSize = bs.nwords - wordOffset
      if (newSize > 0) {
        System.arraycopy(bs.elems, wordOffset, bs.elems, 0, newSize)
        java.util.Arrays.fill(bs.elems, newSize, bs.nwords, 0)
      } else bs.clear()
    } else {
      val wordOffset = (shiftBy >>> LogWL) + 1
      val extraBits = bs.elems(bs.nwords - 1) >>> bitOffset
      val extraWordCount = if (extraBits == 0) 0 else 1
      val newSize = bs.nwords - wordOffset + extraWordCount
      if (newSize > 0) {
        val revBitOffset = WordLength - bitOffset
        var previous = bs.elems(wordOffset - 1)
        var i = wordOffset
        while (i < bs.nwords) {
          val current = bs.elems(i)
          bs.elems(i - wordOffset) = (previous >>> bitOffset) | (current << revBitOffset)
          previous = current
          i += 1
        }
        if (extraWordCount != 0) bs.elems(newSize - 1) = extraBits
        java.util.Arrays.fill(bs.elems, newSize, bs.nwords, 0)
      } else bs.clear()
    }
  }

  protected final def ensureCapacity(idx: Int): Unit = {
    // Copied from mutable.BitSet.ensureCapacity (which is inaccessible from here).
    require(idx < MaxSize)
    if (idx >= bs.nwords) {
      var newlen = bs.nwords
      while (idx >= newlen) newlen = math.min(newlen * 2, MaxSize)
      val elems1 = new Array[Long](newlen)
      Array.copy(bs.elems, 0, elems1, 0, bs.nwords)
      bs.elems = elems1
    }
  }

}
