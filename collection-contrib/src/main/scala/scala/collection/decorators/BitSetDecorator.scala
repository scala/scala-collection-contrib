package scala.collection.decorators

import scala.collection.{BitSet, BitSetOps}

class BitSetDecorator[+C <: BitSet with BitSetOps[C]](protected val bs: C) {

  import BitSetDecorator._
  import BitSetOps._

  /**
    * Bitwise left shift of this BitSet by the given shift distance.
    * The shift distance may be negative, in which case this method performs a right shift.
    * @param shiftBy shift distance, in bits
    * @return a new BitSet whose value is a bitwise shift left of this BitSet by given shift distance (`shiftBy`)
    */
  def <<(shiftBy: Int): C = {

    val shiftedBits = if (bs.nwords == 0 || bs.nwords == 1 && bs.word(0) == 0) Array.emptyLongArray
    else if (shiftBy > 0) shiftLeft(shiftBy)
    else if (shiftBy == 0) bs.toBitMask
    else shiftRight(-shiftBy)

    bs.fromBitMaskNoCopy(shiftedBits)
  }

  /**
    * Bitwise right shift of this BitSet by the given shift distance.
    * The shift distance may be negative, in which case this method performs a left shift.
    * @param shiftBy shift distance, in bits
    * @return a new BitSet whose value is a bitwise shift right of this BitSet by given shift distance (`shiftBy`)
    */
  def >>(shiftBy: Int): C = {

    val shiftedBits = if (bs.nwords == 0 || bs.nwords == 1 && bs.word(0) == 0) Array.emptyLongArray
    else if (shiftBy > 0) shiftRight(shiftBy)
    else if (shiftBy == 0) bs.toBitMask
    else shiftLeft(-shiftBy)

    bs.fromBitMaskNoCopy(shiftedBits)
  }

  private def shiftLeft(shiftBy: Int): Array[Long] = {

    val bitOffset = shiftBy & WordMask
    val wordOffset = shiftBy >>> LogWL

    var significantWordCount = bs.nwords
    while (significantWordCount > 0 && bs.word(significantWordCount - 1) == 0) {
      significantWordCount -= 1
    }

    if (bitOffset == 0) {
      val newSize = significantWordCount + wordOffset
      require(newSize <= MaxSize)
      val newBits = Array.ofDim[Long](newSize)
      var i = wordOffset
      while (i < newSize) {
        newBits(i) = bs.word(i - wordOffset)
        i += 1
      }
      newBits
    } else {
      val revBitOffset = WordLength - bitOffset
      val extraBits = bs.word(significantWordCount - 1) >>> revBitOffset
      val extraWordCount = if (extraBits == 0) 0 else 1
      val newSize = significantWordCount + wordOffset + extraWordCount
      require(newSize <= MaxSize)
      val newBits = Array.ofDim[Long](newSize)
      var previous = 0L
      var i = 0
      while (i < significantWordCount) {
        val current = bs.word(i)
        newBits(i + wordOffset) = (previous >>> revBitOffset) | (current << bitOffset)
        previous = current
        i += 1
      }
      if (extraWordCount != 0) newBits(newSize - 1) = extraBits
      newBits
    }
  }

  private def shiftRight(shiftBy: Int): Array[Long] = {

    val bitOffset = shiftBy & WordMask

    if (bitOffset == 0) {
      val wordOffset = shiftBy >>> LogWL
      val newSize = bs.nwords - wordOffset
      if (newSize > 0) {
        val newBits = Array.ofDim[Long](newSize)
        var i = 0
        while (i < newSize) {
          newBits(i) = bs.word(i + wordOffset)
          i += 1
        }
        newBits
      } else Array.emptyLongArray
    } else {
      val wordOffset = (shiftBy >>> LogWL) + 1
      val extraBits = bs.word(bs.nwords - 1) >>> bitOffset
      val extraWordCount = if (extraBits == 0) 0 else 1
      val newSize = bs.nwords - wordOffset + extraWordCount
      if (newSize > 0) {
        val revBitOffset = WordLength - bitOffset
        val newBits = Array.ofDim[Long](newSize)
        var previous = bs.word(wordOffset - 1)
        var i = wordOffset
        while (i < bs.nwords) {
          val current = bs.word(i)
          newBits(i - wordOffset) = (previous >>> bitOffset) | (current << revBitOffset)
          previous = current
          i += 1
        }
        if (extraWordCount != 0) newBits(newSize - 1) = extraBits
        newBits
      } else Array.emptyLongArray
    }
  }

}

object BitSetDecorator {
  private[collection] final val WordMask = BitSetOps.WordLength - 1
}
