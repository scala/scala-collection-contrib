package scala.collection.decorators

import org.junit.{Assert, Test}

import scala.collection.BitSet

class BitSetDecoratorTest {

  import Assert.{assertEquals, assertSame}
  import BitSet.empty

  @Test
  def shiftEmptyLeft(): Unit = {
    for (shiftBy <- 0 to 128) {
      assertSame(empty, empty << shiftBy)
    }
  }

  @Test
  def shiftLowestBitLeft(): Unit = {
    for (shiftBy <- 0 to 128) {
      assertEquals(BitSet(shiftBy), BitSet(0) << shiftBy)
    }
  }

  @Test
  def shiftNegativeLeft(): Unit = {
    assertEquals(BitSet(0), BitSet(1) << -1)
  }

  @Test
  def largeShiftLeft(): Unit = {
    val bs = BitSet(0 to 300 by 5: _*)
    for (shiftBy <- 0 to 128) {
      assertEquals(bs.map(_ + shiftBy), bs << shiftBy)
    }
  }

  @Test
  def skipZeroWordsOnShiftLeft(): Unit = {
    val result = BitSet(5 * 64 - 1) << 64
    assertEquals(BitSet(6 * 64 - 1), result)
    assertEquals(6, result.nwords)
  }

  @Test
  def shiftEmptyRight(): Unit = {
    for (shiftBy <- 0 to 128) {
      assertSame(empty, empty >> shiftBy)
    }
  }

  @Test
  def shiftLowestBitRight(): Unit = {
    assertEquals(BitSet(0), BitSet(0) >> 0)
    for (shiftBy <- 1 to 128) {
      assertSame(empty, BitSet(0) >> shiftBy)
    }
  }

  @Test
  def shiftToLowestBitRight(): Unit = {
    for (shiftBy <- 0 to 128) {
      assertEquals(BitSet(0), BitSet(shiftBy) >> shiftBy)
    }
  }

  @Test
  def shiftNegativeRight(): Unit = {
    assertEquals(BitSet(1), BitSet(0) >> -1)
  }

  @Test
  def largeShiftRight(): Unit = {
    val bs = BitSet(0 to 300 by 5: _*)
    for (shiftBy <- 0 to 128) {
      assertEquals(bs.collect {
        case b if b >= shiftBy => b - shiftBy
      }, bs >> shiftBy)
    }
  }

}
