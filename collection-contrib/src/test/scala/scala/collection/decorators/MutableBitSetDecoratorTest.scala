package scala.collection.decorators

import org.junit.{Assert, Test}

import scala.collection.mutable.BitSet

class MutableBitSetDecoratorTest {

  import Assert.{assertEquals, assertSame}
  import BitSet.empty

  @Test
  def shiftEmptyLeftInPlace(): Unit = {
    for (shiftBy <- 0 to 128) {
      val bs = empty
      bs <<= shiftBy
      assertEquals(empty, bs)
      assertEquals(empty.nwords, bs.nwords)
    }
  }

  @Test
  def shiftLowestBitLeftInPlace(): Unit = {
    for (shiftBy <- 0 to 128) {
      val bs = BitSet(0)
      bs <<= shiftBy
      assertEquals(BitSet(shiftBy), bs)
    }
  }

  @Test
  def shiftNegativeLeftInPlace(): Unit = {
    val bs = BitSet(1)
    bs <<= -1
    assertEquals(BitSet(0), bs)
  }

  @Test
  def largeShiftLeftInPlace(): Unit = {
    for (shiftBy <- 0 to 128) {
      val bs = BitSet(0 to 300 by 5: _*)
      val expected = bs.map(_ + shiftBy)
      bs <<= shiftBy
      assertEquals(expected, bs)
    }
  }

  @Test
  def skipZeroWordsOnShiftLeftInPlace(): Unit = {
    val bs = BitSet(5 * 64 - 1)
    bs <<= 64
    assertEquals(BitSet(6 * 64 - 1), bs)
    assertEquals(8, bs.nwords)
  }

  @Test
  def shiftEmptyRightInPlace(): Unit = {
    for (shiftBy <- 0 to 128) {
      val bs = empty
      bs >>= shiftBy
      assertEquals(empty, bs)
      assertEquals(empty.nwords, bs.nwords)
    }
  }

  @Test
  def shiftLowestBitRightInPlace(): Unit = {
    val bs = BitSet(0)
    bs >>= 0
    assertEquals(BitSet(0), bs)

    for (shiftBy <- 1 to 128) {
      val bs = BitSet(0)
      bs >>= shiftBy
      assertEquals(empty, bs)
    }
  }

  @Test
  def shiftToLowestBitRightInPlace(): Unit = {
    for (shiftBy <- 0 to 128) {
      val bs = BitSet(shiftBy)
      bs >>= shiftBy
      assertEquals(BitSet(0), bs)
    }
  }

  @Test
  def shiftNegativeRightInPlace(): Unit = {
    val bs = BitSet(0)
    bs >>= -1
    assertEquals(BitSet(1), bs)
  }

  @Test
  def largeShiftRightInPlace(): Unit = {
    for (shiftBy <- 0 to 128) {
      val bs = BitSet(0 to 300 by 5: _*)
      val expected = bs.collect {
        case b if b >= shiftBy => b - shiftBy
      }
      bs >>= shiftBy
      assertEquals(expected, bs)
    }
  }

}
