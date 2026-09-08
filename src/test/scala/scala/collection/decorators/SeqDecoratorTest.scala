package scala.collection
package decorators

import org.junit.Assert.{assertArrayEquals, assertEquals, assertFalse, assertThrows, assertTrue}
import org.junit.Test
import scala.collection.immutable._

class SeqDecoratorTest {

  @Test def intersperse(): Unit = {
    assertEquals(List(1, 0, 2, 0, 3), List(1, 2, 3).intersperse(0))
    assertEquals(List(-1, 1, 0, 2, 0, 3, -2), List(1, 2, 3).intersperse(-1, 0, -2))
    assertEquals(List.empty[Int], List.empty[Int].intersperse(0))
    assertEquals(List(1, 2), List.empty[Int].intersperse(1, 0, 2))
    assertEquals(List(1), List(1).intersperse(0))
    assertEquals(List(0, 1, 2), List(1).intersperse(0, 5, 2))
  }

  // This test just checks that there is no compilation error
  @Test def genericDecorator(): Unit = {
    val vector = Vector(1, 2, 3)
    val range = Range(0, 10)
    val array = Array(1, 2, 3)
    val string = "foo"
    val list = List(1, 2, 3)
    val result = list.intersperse(0)
    typed[List[Int]](result)
    list.view.intersperse(0)
    val result2 = range.intersperse(0)
    typed[IndexedSeq[Int]](result2)
    vector.intersperse(0)
    vector.view.intersperse(0)
    val result3 = array.intersperse(0)
    typed[Array[Int]](result3)
    array.view.intersperse(0)
    string.intersperse(' ')
    string.view.intersperse(' ')
  }

  def typed[T](t: => T): Unit = ()

  @Test def testReplaced(): Unit = {
    val s = Seq(1, 2, 3, 2, 1)
    assertEquals(s.replaced(2, 4), Seq(1, 4, 3, 4, 1))
    assertEquals(s.replaced(3, 4), Seq(1, 2, 4, 2, 1))
    assertEquals(s.replaced(4, 4), s)
  }

  // --- Ring (circular) operations --------------------------------------------
  //
  // Each ring method is tested across all collection types that the IsSeq
  // decorator supports: List, Vector, Range, Array, String.

  @Test def applyO(): Unit = {
    // List
    assertEquals(0, List(0, 1, 2).applyO(3))
    assertEquals(1, List(0, 1, 2).applyO(4))
    assertEquals(2, List(0, 1, 2).applyO(-1))
    assertEquals(0, List(0, 1, 2).applyO(-3))
    assertThrows(classOf[ArithmeticException], () => List.empty[Int].applyO(0))
    // Vector
    assertEquals(1, Vector(0, 1, 2).applyO(4))
    assertEquals(2, Vector(0, 1, 2).applyO(-1))
    // Range
    assertEquals(0, Range(0, 3).applyO(3))
    assertEquals(2, Range(0, 3).applyO(-1))
    // Array
    assertEquals(1, Array(0, 1, 2).applyO(4))
    assertEquals(0, Array(0, 1, 2).applyO(-3))
    // String
    assertEquals('b', "abc".applyO(4))
    assertEquals('c', "abc".applyO(-1))
  }

  @Test def rotateRight(): Unit = {
    // List
    assertEquals(List(2, 0, 1), List(0, 1, 2).rotateRight(1))
    assertEquals(List(1, 2, 0), List(0, 1, 2).rotateRight(-1))
    assertEquals(List(0, 1, 2), List(0, 1, 2).rotateRight(0))
    assertEquals(List(0, 1, 2), List(0, 1, 2).rotateRight(3))
    assertEquals(List(2, 0, 1), List(0, 1, 2).rotateRight(4))
    assertEquals(List.empty[Int], List.empty[Int].rotateRight(5))
    // Vector
    typed[Vector[Int]](Vector(0, 1, 2).rotateRight(1))
    assertEquals(Vector(2, 0, 1), Vector(0, 1, 2).rotateRight(1))
    assertEquals(Vector(1, 2, 0), Vector(0, 1, 2).rotateRight(-1))
    // Range
    val rangeRot: IndexedSeq[Int] = Range(0, 3).rotateRight(1)
    typed[IndexedSeq[Int]](rangeRot)
    assertEquals(IndexedSeq(2, 0, 1), rangeRot)
    assertEquals(IndexedSeq(4, 0, 1, 2, 3), Range(0, 5).rotateRight(1))
    // Array
    val arrRot: Array[Int] = Array(0, 1, 2).rotateRight(1)
    typed[Array[Int]](arrRot)
    assertArrayEquals(Array(2, 0, 1), arrRot)
    assertArrayEquals(Array(1, 2, 0), Array(0, 1, 2).rotateRight(-1))
    // String
    val strRot: String = "abc".rotateRight(1)
    typed[String](strRot)
    assertEquals("cab", strRot)
    assertEquals("bca", "abc".rotateRight(-1))
    assertEquals("", "".rotateRight(5))
  }

  @Test def rotateLeft(): Unit = {
    // List
    assertEquals(List(1, 2, 0), List(0, 1, 2).rotateLeft(1))
    assertEquals(List(2, 0, 1), List(0, 1, 2).rotateLeft(-1))
    assertEquals(List.empty[Int], List.empty[Int].rotateLeft(5))
    // Vector
    assertEquals(Vector(1, 2, 0), Vector(0, 1, 2).rotateLeft(1))
    // Range
    assertEquals(IndexedSeq(1, 2, 0), Range(0, 3).rotateLeft(1))
    // Array
    assertArrayEquals(Array(1, 2, 0), Array(0, 1, 2).rotateLeft(1))
    // String
    assertEquals("bca", "abc".rotateLeft(1))
    assertEquals("cab", "abc".rotateLeft(-1))
  }

  @Test def startAt(): Unit = {
    // List
    assertEquals(List(1, 2, 0), List(0, 1, 2).startAt(1))
    assertEquals(List(2, 0, 1), List(0, 1, 2).startAt(2))
    assertEquals(List(0, 1, 2), List(0, 1, 2).startAt(3))
    assertEquals(List(2, 0, 1), List(0, 1, 2).startAt(-1))
    // Vector
    assertEquals(Vector(1, 2, 0), Vector(0, 1, 2).startAt(1))
    // Range
    assertEquals(IndexedSeq(2, 3, 4, 0, 1), Range(0, 5).startAt(2))
    // Array
    assertArrayEquals(Array(1, 2, 0), Array(0, 1, 2).startAt(1))
    // String
    assertEquals("bcda", "abcd".startAt(1))
    assertEquals("dabc", "abcd".startAt(-1))
  }

  @Test def reflectAt(): Unit = {
    // List
    assertEquals(List(0, 2, 1), List(0, 1, 2).reflectAt())
    assertEquals(List(1, 0, 2), List(0, 1, 2).reflectAt(1))
    assertEquals(List(2, 1, 0), List(0, 1, 2).reflectAt(2))
    assertEquals(List.empty[Int], List.empty[Int].reflectAt())
    // Vector
    assertEquals(Vector(0, 2, 1), Vector(0, 1, 2).reflectAt())
    // Range
    assertEquals(IndexedSeq(0, 2, 1), Range(0, 3).reflectAt())
    assertEquals(IndexedSeq(1, 0, 4, 3, 2), Range(0, 5).reflectAt(1))
    // Array
    assertArrayEquals(Array(0, 2, 1), Array(0, 1, 2).reflectAt())
    // String
    assertEquals("acb", "abc".reflectAt())
    assertEquals("dcba", "abcd".reflectAt(3))
  }

  @Test def segmentLengthO(): Unit = {
    // List
    assertEquals(2, List(0, 1, 2).segmentLengthO(_ % 2 == 0, 2))
    assertEquals(1, List(0, 1, 2).segmentLengthO(_ % 2 == 0, 0))
    assertEquals(0, List(0, 1, 2).segmentLengthO(_ < 0))
    assertEquals(0, List.empty[Int].segmentLengthO(_ => true))
    // Vector
    assertEquals(2, Vector(0, 1, 2).segmentLengthO(_ % 2 == 0, 2))
    // Range
    assertEquals(2, Range(0, 3).segmentLengthO(_ % 2 == 0, 2))
    assertEquals(6, Range(0, 6).segmentLengthO(_ < 6))
    // Array
    assertEquals(2, Array(0, 1, 2).segmentLengthO(_ % 2 == 0, 2))
    // String
    assertEquals(3, "aaabbb".segmentLengthO(_ == 'a'))
    assertEquals(3, "aaabbb".segmentLengthO(_ == 'b', 3))
    assertEquals(6, "aaabbb".segmentLengthO(_ => true, 4))
  }

  @Test def takeWhileO(): Unit = {
    // List
    assertEquals(List(1, 2), List(0, 1, 2, 3, 4).takeWhileO(_ < 3, 1))
    assertEquals(List(0, 1, 2), List(0, 1, 2, 3, 4).takeWhileO(_ < 3, 0))
    assertEquals(List(4, 0, 1, 2), List(0, 1, 2, 3, 4).takeWhileO(_ != 3, -1))
    assertEquals(List.empty[Int], List.empty[Int].takeWhileO(_ => true))
    // Vector
    assertEquals(Vector(1, 2), Vector(0, 1, 2, 3, 4).takeWhileO(_ < 3, 1))
    // Range
    assertEquals(IndexedSeq(1, 2), Range(0, 5).takeWhileO(_ < 3, 1))
    // Array
    assertArrayEquals(Array(1, 2), Array(0, 1, 2, 3, 4).takeWhileO(_ < 3, 1))
    // String
    assertEquals("bc", "abcde".takeWhileO(_ < 'd', 1))
    assertEquals("eab", "abcde".takeWhileO(_ != 'c', 4))
  }

  @Test def dropWhileO(): Unit = {
    // List
    assertEquals(List(3, 4, 0), List(0, 1, 2, 3, 4).dropWhileO(_ < 3, 1))
    assertEquals(List(3, 4), List(0, 1, 2, 3, 4).dropWhileO(_ < 3, 0))
    assertEquals(List.empty[Int], List.empty[Int].dropWhileO(_ => false))
    // Vector
    assertEquals(Vector(3, 4, 0), Vector(0, 1, 2, 3, 4).dropWhileO(_ < 3, 1))
    // Range
    assertEquals(IndexedSeq(3, 4, 0), Range(0, 5).dropWhileO(_ < 3, 1))
    // Array
    assertArrayEquals(Array(3, 4, 0), Array(0, 1, 2, 3, 4).dropWhileO(_ < 3, 1))
    // String
    assertEquals("de", "abcde".dropWhileO(_ < 'd', 0))
    assertEquals("cd", "abcde".dropWhileO(_ != 'c', 4))
  }

  @Test def spanO(): Unit = {
    // List
    assertEquals((List(1, 2), List(3, 4, 0)), List(0, 1, 2, 3, 4).spanO(_ < 3, 1))
    assertEquals((List(0, 1, 2), List(3, 4)), List(0, 1, 2, 3, 4).spanO(_ < 3, 0))
    assertEquals((List.empty[Int], List.empty[Int]), List.empty[Int].spanO(_ => true))
    // Vector
    assertEquals((Vector(1, 2), Vector(3, 4, 0)), Vector(0, 1, 2, 3, 4).spanO(_ < 3, 1))
    // Range
    assertEquals((IndexedSeq(1, 2), IndexedSeq(3, 4, 0)), Range(0, 5).spanO(_ < 3, 1))
    // Array
    val (aLeft, aRight) = Array(0, 1, 2, 3, 4).spanO(_ < 3, 1)
    assertArrayEquals(Array(1, 2), aLeft)
    assertArrayEquals(Array(3, 4, 0), aRight)
    // String
    assertEquals(("bc", "dea"), "abcde".spanO(_ < 'd', 1))
  }

  @Test def sliceO(): Unit = {
    // List
    assertEquals(List(2, 0, 1, 2, 0), List(0, 1, 2).sliceO(-1, 4))
    assertEquals(List(1, 2), List(0, 1, 2).sliceO(1, 3))
    assertEquals(List.empty[Int], List(0, 1, 2).sliceO(2, 2))
    assertEquals(List.empty[Int], List(0, 1, 2).sliceO(2, 1))
    assertEquals(List.empty[Int], List.empty[Int].sliceO(0, 10))
    // Vector
    assertEquals(Vector(2, 0, 1, 2, 0), Vector(0, 1, 2).sliceO(-1, 4))
    // Range
    assertEquals(IndexedSeq(2, 0, 1, 2, 0), Range(0, 3).sliceO(-1, 4))
    assertEquals(IndexedSeq(4, 0, 1, 2, 3), Range(0, 5).sliceO(-1, 4))
    // Array
    assertArrayEquals(Array(2, 0, 1, 2, 0), Array(0, 1, 2).sliceO(-1, 4))
    // String
    assertEquals("cabca", "abc".sliceO(-1, 4))
    assertEquals("eabcde", "abcde".sliceO(-1, 5))
  }

  @Test def containsSliceO(): Unit = {
    // List
    assertTrue(List(0, 1, 2).containsSliceO(Seq(2, 0, 1, 2, 0)))
    assertTrue(List(0, 1, 2).containsSliceO(Seq(2, 0)))
    assertFalse(List(0, 1, 2).containsSliceO(Seq(0, 2)))
    assertTrue(List.empty[Int].containsSliceO(Seq.empty[Int]))
    assertFalse(List.empty[Int].containsSliceO(Seq(1)))
    // Vector
    assertTrue(Vector(0, 1, 2).containsSliceO(Seq(2, 0)))
    // Range
    assertTrue(Range(0, 3).containsSliceO(Seq(2, 0, 1)))
    assertFalse(Range(0, 3).containsSliceO(Seq(0, 2)))
    // Array
    assertTrue(Array(0, 1, 2).containsSliceO(Seq(2, 0)))
    // String
    assertTrue("abc".containsSliceO("cab"))
    assertTrue("abc".containsSliceO("ca"))
    assertFalse("abc".containsSliceO("ac"))
  }

  @Test def indexOfSliceO(): Unit = {
    // List
    assertEquals(2, List(0, 1, 2).indexOfSliceO(Seq(2, 0, 1, 2, 0)))
    assertEquals(1, List(0, 1, 2, 3).indexOfSliceO(Seq(1, 2)))
    assertEquals(-1, List(0, 1, 2).indexOfSliceO(Seq(1, 0)))
    // Vector
    assertEquals(1, Vector(0, 1, 2, 3).indexOfSliceO(Seq(1, 2)))
    // Range
    assertEquals(2, Range(0, 3).indexOfSliceO(Seq(2, 0)))
    // Array
    assertEquals(1, Array(0, 1, 2, 3).indexOfSliceO(Seq(1, 2)))
    // String
    assertEquals(2, "abc".indexOfSliceO("ca"))
    assertEquals(1, "abcd".indexOfSliceO("bc"))
    assertEquals(-1, "abc".indexOfSliceO("ac"))
  }

  @Test def lastIndexOfSliceO(): Unit = {
    // List
    assertEquals(5, List(0, 1, 2, 0, 1, 2).lastIndexOfSliceO(Seq(2, 0)))
    assertEquals(-1, List(0, 1, 2).lastIndexOfSliceO(Seq(1, 0, 2)))
    // Vector
    assertEquals(5, Vector(0, 1, 2, 0, 1, 2).lastIndexOfSliceO(Seq(2, 0)))
    // Range
    assertEquals(2, Range(0, 3).lastIndexOfSliceO(Seq(2, 0)))
    // Array
    assertEquals(5, Array(0, 1, 2, 0, 1, 2).lastIndexOfSliceO(Seq(2, 0)))
    // String
    assertEquals(5, "abcabc".lastIndexOfSliceO("ca"))
  }

  @Test def slidingO(): Unit = {
    // List
    assertEquals(
      List(List(0, 1), List(1, 2), List(2, 0)),
      List(0, 1, 2).slidingO(2).toList
    )
    assertEquals(
      List(List(0, 1), List(2, 0), List(1, 2)),
      List(0, 1, 2).slidingO(2, 2).toList
    )
    assertTrue(List.empty[Int].slidingO(2).isEmpty)
    // Vector
    assertEquals(
      List(Vector(0, 1), Vector(1, 2), Vector(2, 0)),
      Vector(0, 1, 2).slidingO(2).toList
    )
    // Range
    assertEquals(
      List(IndexedSeq(0, 1), IndexedSeq(1, 2), IndexedSeq(2, 0)),
      Range(0, 3).slidingO(2).toList
    )
    // Array
    assertEquals(
      List(List(0, 1), List(1, 2), List(2, 0)),
      Array(0, 1, 2).slidingO(2).toList.map(_.toList)
    )
    // String
    assertEquals(
      List("ab", "bc", "ca"),
      "abc".slidingO(2).toList
    )
  }

  @Test def groupedO(): Unit = {
    // List
    assertEquals(
      List(List(0, 1), List(2, 3), List(4, 0)),
      List(0, 1, 2, 3, 4).groupedO(2).toList
    )
    assertEquals(List(List(0, 1, 2)), List(0, 1, 2).groupedO(3).toList)
    assertEquals(List(List(0, 1, 2, 0)), List(0, 1, 2).groupedO(4).toList)
    assertTrue(List.empty[Int].groupedO(2).isEmpty)
    // Vector
    assertEquals(
      List(Vector(0, 1), Vector(2, 3), Vector(4, 0)),
      Vector(0, 1, 2, 3, 4).groupedO(2).toList
    )
    // Range
    assertEquals(
      List(IndexedSeq(0, 1), IndexedSeq(2, 3), IndexedSeq(4, 0)),
      Range(0, 5).groupedO(2).toList
    )
    // Array
    assertEquals(
      List(List(0, 1), List(2, 3), List(4, 0)),
      Array(0, 1, 2, 3, 4).groupedO(2).toList.map(_.toList)
    )
    // String
    assertEquals(
      List("ab", "cd", "ea"),
      "abcde".groupedO(2).toList
    )
  }

  @Test def zipWithIndexO(): Unit = {
    // List
    assertEquals(
      List(('b', 1), ('c', 2), ('a', 0)),
      List('a', 'b', 'c').zipWithIndexO(1).toList
    )
    assertEquals(
      List(('a', 0), ('b', 1), ('c', 2)),
      List('a', 'b', 'c').zipWithIndexO().toList
    )
    assertTrue(List.empty[Char].zipWithIndexO().isEmpty)
    // Vector
    assertEquals(
      List(('b', 1), ('c', 2), ('a', 0)),
      Vector('a', 'b', 'c').zipWithIndexO(1).toList
    )
    // Range
    assertEquals(
      List((1, 1), (2, 2), (0, 0)),
      Range(0, 3).zipWithIndexO(1).toList
    )
    // Array
    assertEquals(
      List((1, 1), (2, 2), (0, 0)),
      Array(0, 1, 2).zipWithIndexO(1).toList
    )
    // String
    assertEquals(
      List(('b', 1), ('c', 2), ('a', 0)),
      "abc".zipWithIndexO(1).toList
    )
  }

  @Test def rotations(): Unit = {
    // List
    assertEquals(
      List(List(0, 1, 2), List(1, 2, 0), List(2, 0, 1)),
      List(0, 1, 2).rotations.toList
    )
    assertEquals(List(List.empty[Int]), List.empty[Int].rotations.toList)
    // Vector
    assertEquals(
      List(Vector(0, 1, 2), Vector(1, 2, 0), Vector(2, 0, 1)),
      Vector(0, 1, 2).rotations.toList
    )
    // Range
    assertEquals(
      List(IndexedSeq(0, 1, 2), IndexedSeq(1, 2, 0), IndexedSeq(2, 0, 1)),
      Range(0, 3).rotations.toList
    )
    // Array
    assertEquals(
      List(List(0, 1, 2), List(1, 2, 0), List(2, 0, 1)),
      Array(0, 1, 2).rotations.toList.map(_.toList)
    )
    // String
    assertEquals(List("abc", "bca", "cab"), "abc".rotations.toList)
    assertEquals(List(""), "".rotations.toList)
  }

  @Test def reflections(): Unit = {
    // List
    assertEquals(
      List(List(0, 1, 2), List(0, 2, 1)),
      List(0, 1, 2).reflections.toList
    )
    assertEquals(List(List.empty[Int]), List.empty[Int].reflections.toList)
    // Vector
    assertEquals(
      List(Vector(0, 1, 2), Vector(0, 2, 1)),
      Vector(0, 1, 2).reflections.toList
    )
    // Range
    assertEquals(
      List(IndexedSeq(0, 1, 2), IndexedSeq(0, 2, 1)),
      Range(0, 3).reflections.toList
    )
    // Array
    assertEquals(
      List(List(0, 1, 2), List(0, 2, 1)),
      Array(0, 1, 2).reflections.toList.map(_.toList)
    )
    // String
    assertEquals(List("abc", "acb"), "abc".reflections.toList)
  }

  @Test def reversions(): Unit = {
    // List
    assertEquals(
      List(List(0, 1, 2), List(2, 1, 0)),
      List(0, 1, 2).reversions.toList
    )
    assertEquals(List(List.empty[Int]), List.empty[Int].reversions.toList)
    // Vector
    assertEquals(
      List(Vector(0, 1, 2), Vector(2, 1, 0)),
      Vector(0, 1, 2).reversions.toList
    )
    // Range
    assertEquals(
      List(IndexedSeq(0, 1, 2), IndexedSeq(2, 1, 0)),
      Range(0, 3).reversions.toList
    )
    // Array
    assertEquals(
      List(List(0, 1, 2), List(2, 1, 0)),
      Array(0, 1, 2).reversions.toList.map(_.toList)
    )
    // String
    assertEquals(List("abc", "cba"), "abc".reversions.toList)
  }

  @Test def rotationsAndReflections(): Unit = {
    val expected = List(
      List(0, 1, 2), List(1, 2, 0), List(2, 0, 1),
      List(0, 2, 1), List(2, 1, 0), List(1, 0, 2)
    )
    // List
    assertEquals(expected, List(0, 1, 2).rotationsAndReflections.toList)
    // Vector
    assertEquals(expected.map(_.toVector), Vector(0, 1, 2).rotationsAndReflections.toList)
    // Range
    assertEquals(expected.map(_.toIndexedSeq), Range(0, 3).rotationsAndReflections.toList)
    // Array
    assertEquals(expected, Array(0, 1, 2).rotationsAndReflections.toList.map(_.toList))
    // String
    assertEquals(
      List("abc", "bca", "cab", "acb", "cba", "bac"),
      "abc".rotationsAndReflections.toList
    )
  }

  @Test def isRotationOf(): Unit = {
    // List
    assertTrue(List(0, 1, 2).isRotationOf(Seq(1, 2, 0)))
    assertTrue(List(0, 1, 2).isRotationOf(Seq(0, 1, 2)))
    assertFalse(List(0, 1, 2).isRotationOf(Seq(0, 2, 1)))
    assertFalse(List(0, 1, 2).isRotationOf(Seq(0, 1)))
    assertTrue(List.empty[Int].isRotationOf(Seq.empty[Int]))
    // Vector
    assertTrue(Vector(0, 1, 2).isRotationOf(Seq(2, 0, 1)))
    // Range
    assertTrue(Range(0, 3).isRotationOf(Seq(1, 2, 0)))
    assertFalse(Range(0, 3).isRotationOf(Seq(2, 1, 0)))
    // Array
    assertTrue(Array(0, 1, 2).isRotationOf(Seq(2, 0, 1)))
    assertFalse(Array(0, 1, 2).isRotationOf(Seq(0, 2, 1)))
    // String
    assertTrue("abc".isRotationOf("cab"))
    assertFalse("abc".isRotationOf("acb"))
  }

  @Test def isReflectionOf(): Unit = {
    // List
    assertTrue(List(0, 1, 2).isReflectionOf(Seq(0, 2, 1)))
    assertTrue(List(0, 1, 2).isReflectionOf(Seq(0, 1, 2)))
    assertFalse(List(0, 1, 2).isReflectionOf(Seq(1, 2, 0)))
    // Vector
    assertTrue(Vector(0, 1, 2).isReflectionOf(Seq(0, 2, 1)))
    // Range
    assertTrue(Range(0, 3).isReflectionOf(Seq(0, 2, 1)))
    // Array
    assertTrue(Array(0, 1, 2).isReflectionOf(Seq(0, 2, 1)))
    // String
    assertTrue("abc".isReflectionOf("acb"))
    assertFalse("abc".isReflectionOf("bca"))
  }

  @Test def isReversionOf(): Unit = {
    // List
    assertTrue(List(0, 1, 2).isReversionOf(Seq(2, 1, 0)))
    assertTrue(List(0, 1, 2).isReversionOf(Seq(0, 1, 2)))
    assertFalse(List(0, 1, 2).isReversionOf(Seq(1, 2, 0)))
    // Vector
    assertTrue(Vector(0, 1, 2).isReversionOf(Seq(2, 1, 0)))
    // Range
    assertTrue(Range(0, 3).isReversionOf(Seq(2, 1, 0)))
    // Array
    assertTrue(Array(0, 1, 2).isReversionOf(Seq(2, 1, 0)))
    // String
    assertTrue("abc".isReversionOf("cba"))
    assertFalse("abc".isReversionOf("bca"))
  }

  @Test def isRotationOrReflectionOf(): Unit = {
    // List
    assertTrue(List(0, 1, 2).isRotationOrReflectionOf(Seq(2, 0, 1)))
    assertTrue(List(0, 1, 2).isRotationOrReflectionOf(Seq(0, 2, 1)))
    assertFalse(List(0, 1, 2).isRotationOrReflectionOf(Seq(1, 0, 3)))
    // Vector
    assertTrue(Vector(0, 1, 2).isRotationOrReflectionOf(Seq(1, 0, 2)))
    // Range
    assertTrue(Range(0, 3).isRotationOrReflectionOf(Seq(1, 0, 2)))
    // Array
    assertTrue(Array(0, 1, 2).isRotationOrReflectionOf(Seq(1, 0, 2)))
    // String
    assertTrue("abc".isRotationOrReflectionOf("bac"))
    assertFalse("abc".isRotationOrReflectionOf("abd"))
  }

  @Test def alignTo(): Unit = {
    // List
    assertEquals(Some(2), List(0, 1, 2).alignTo(Seq(2, 0, 1)))
    assertEquals(Some(0), List(0, 1, 2).alignTo(Seq(0, 1, 2)))
    assertEquals(None, List(0, 1, 2).alignTo(Seq(0, 2, 1)))
    assertEquals(None, List(0, 1, 2).alignTo(Seq(0, 1)))
    assertEquals(Some(0), List.empty[Int].alignTo(Seq.empty[Int]))
    // Vector
    assertEquals(Some(1), Vector(0, 1, 2).alignTo(Seq(1, 2, 0)))
    // Range
    assertEquals(Some(2), Range(0, 3).alignTo(Seq(2, 0, 1)))
    // Array
    assertEquals(Some(2), Array(0, 1, 2).alignTo(Seq(2, 0, 1)))
    // String
    assertEquals(Some(2), "abc".alignTo("cab"))
    assertEquals(None, "abc".alignTo("acb"))
  }

  @Test def hammingDistance(): Unit = {
    // List
    assertEquals(2, List(1, 0, 1, 1).hammingDistance(Seq(1, 1, 0, 1)))
    assertEquals(0, List(1, 0, 1, 1).hammingDistance(Seq(1, 0, 1, 1)))
    assertEquals(0, List.empty[Int].hammingDistance(Seq.empty[Int]))
    assertThrows(classOf[IllegalArgumentException], () =>
      List(1, 0).hammingDistance(Seq(1, 0, 0))
    )
    // Vector
    assertEquals(2, Vector(1, 0, 1, 1).hammingDistance(Seq(1, 1, 0, 1)))
    // Range
    assertEquals(3, Range(0, 3).hammingDistance(Seq(2, 0, 1)))
    // Array
    assertEquals(2, Array(1, 0, 1, 1).hammingDistance(Seq(1, 1, 0, 1)))
    // String
    assertEquals(3, "karolin".hammingDistance("kathrin"))
    assertEquals(0, "abc".hammingDistance("abc"))
  }

  @Test def minRotationalHammingDistance(): Unit = {
    // List
    assertEquals(0, List(1, 0, 1, 1).minRotationalHammingDistance(Seq(1, 1, 0, 1)))
    assertEquals(2, List(1, 0, 0, 0).minRotationalHammingDistance(Seq(1, 1, 1, 0)))
    assertEquals(0, List.empty[Int].minRotationalHammingDistance(Seq.empty[Int]))
    // Vector
    assertEquals(0, Vector(1, 0, 1, 1).minRotationalHammingDistance(Seq(1, 1, 0, 1)))
    // Range
    assertEquals(0, Range(0, 3).minRotationalHammingDistance(Seq(2, 0, 1)))
    // Array
    assertEquals(2, Array(1, 0, 0, 0).minRotationalHammingDistance(Seq(1, 1, 1, 0)))
    // String
    assertEquals(0, "abc".minRotationalHammingDistance("cab"))
    assertEquals(2, "abcd".minRotationalHammingDistance("abdc"))
  }

  @Test def canonicalIndex(): Unit = {
    // List
    assertEquals(1, List(2, 0, 1).canonicalIndex)
    assertEquals(0, List(0, 1, 2).canonicalIndex)
    assertEquals(0, List.empty[Int].canonicalIndex)
    assertEquals(0, List(5).canonicalIndex)
    // Vector
    assertEquals(1, Vector(2, 0, 1).canonicalIndex)
    // Range
    assertEquals(0, Range(0, 3).canonicalIndex)
    // Array
    assertEquals(1, Array(2, 0, 1).canonicalIndex)
    // String
    assertEquals(1, "cab".canonicalIndex) // startAt(1) = "abc"
    assertEquals(0, "abc".canonicalIndex)
  }

  @Test def canonical(): Unit = {
    // List
    assertEquals(List(0, 1, 2), List(2, 0, 1).canonical)
    assertEquals(List(0, 1, 2), List(0, 1, 2).canonical)
    assertEquals(List(0, 0, 1, 1), List(1, 1, 0, 0).canonical)
    assertEquals(List.empty[Int], List.empty[Int].canonical)
    // Vector
    assertEquals(Vector(0, 1, 2), Vector(2, 0, 1).canonical)
    // Range
    assertEquals(IndexedSeq(0, 1, 2), Range(0, 3).canonical)
    // Array
    assertArrayEquals(Array(0, 1, 2), Array(2, 0, 1).canonical)
    // String
    assertEquals("abc", "cab".canonical)
    assertEquals("abc", "bca".canonical)
  }

  @Test def bracelet(): Unit = {
    // List
    assertEquals(List(0, 1, 2), List(2, 0, 1).bracelet)
    assertEquals(List(0, 1, 2), List(1, 0, 2).bracelet) // reflection of (0, 1, 2)
    assertEquals(List.empty[Int], List.empty[Int].bracelet)
    // Vector
    assertEquals(Vector(0, 1, 2), Vector(1, 0, 2).bracelet)
    // Range
    assertEquals(IndexedSeq(0, 1, 2), Range(0, 3).bracelet)
    // Array
    assertArrayEquals(Array(0, 1, 2), Array(1, 0, 2).bracelet)
    // String
    assertEquals("abc", "cab".bracelet)
    assertEquals("abc", "bac".bracelet)
  }

  @Test def rotationalSymmetry(): Unit = {
    // List
    assertEquals(2, List(0, 1, 2, 0, 1, 2).rotationalSymmetry)
    assertEquals(1, List(0, 1, 2).rotationalSymmetry)
    assertEquals(4, List(0, 0, 0, 0).rotationalSymmetry)
    assertEquals(1, List.empty[Int].rotationalSymmetry)
    assertEquals(1, List(5).rotationalSymmetry)
    // Vector
    assertEquals(2, Vector(0, 1, 2, 0, 1, 2).rotationalSymmetry)
    // Range
    assertEquals(1, Range(0, 3).rotationalSymmetry)
    // Array
    assertEquals(2, Array(0, 1, 2, 0, 1, 2).rotationalSymmetry)
    // String
    assertEquals(3, "ababab".rotationalSymmetry)
    assertEquals(1, "abc".rotationalSymmetry)
  }

  @Test def symmetryIndices(): Unit = {
    // List
    assertEquals(
      List(0, 3, 6, 9),
      List(2, 1, 2, 2, 1, 2, 2, 1, 2, 2, 1, 2).symmetryIndices
    )
    assertEquals(Nil, List.empty[Int].symmetryIndices)
    // Vector
    assertEquals(
      List(0, 3, 6, 9),
      Vector(2, 1, 2, 2, 1, 2, 2, 1, 2, 2, 1, 2).symmetryIndices
    )
    // Range
    assertEquals(Nil, Range(0, 3).symmetryIndices)
    // Array
    assertEquals(
      List(0, 3, 6, 9),
      Array(2, 1, 2, 2, 1, 2, 2, 1, 2, 2, 1, 2).symmetryIndices
    )
    // String
    // char version of the List(2, 1, 2, 2, 1, 2, ...) example
    assertEquals(List(0, 3, 6, 9), "bcbbcbbcbbcb".symmetryIndices)
    assertEquals(Nil, "abcd".symmetryIndices)
  }

  @Test def symmetry(): Unit = {
    // List
    assertEquals(4, List(2, 1, 2, 2, 1, 2, 2, 1, 2, 2, 1, 2).symmetry)
    assertEquals(0, List(0, 1, 2).symmetry)
    assertEquals(0, List.empty[Int].symmetry)
    // Vector
    assertEquals(4, Vector(2, 1, 2, 2, 1, 2, 2, 1, 2, 2, 1, 2).symmetry)
    // Range
    assertEquals(0, Range(0, 3).symmetry)
    // Array
    assertEquals(4, Array(2, 1, 2, 2, 1, 2, 2, 1, 2, 2, 1, 2).symmetry)
    // String
    assertEquals(0, "abc".symmetry)
    assertEquals(1, "aba".symmetry)
  }

}
