package scala.collection
package decorators

import org.junit.Assert.{assertEquals, assertSame, assertThrows}
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

  @Test def testApplyO(): Unit = {
    val s = Seq(0, 1, 2)
    assertEquals(s.applyO(3), 0)
    val empty = Vector.empty[Int]
    assertThrows(classOf[java.lang.ArithmeticException], () => empty.applyO(1))
  }

  @Test def testRotatedRight(): Unit = {
    val s = Seq(1, 2, 3, 2, 1)
    val sRotated = Seq(1, 1, 2, 3, 2)
    assertEquals(s.rotateRight(1), sRotated)
    assertEquals(s.rotateRight(6), sRotated)
    assertEquals(s.rotateRight(-4), sRotated)
    val string = "RING"
    assertEquals(string.rotateRight(1), "GRIN")
    val empty = Vector.empty[Int]
    assertEquals(empty.rotateRight(1), empty)
  }

  @Test def testRotatedLeft(): Unit = {
    val s = Seq(1, 2, 3, 2, 1)
    val sRotated = Seq(2, 3, 2, 1, 1)
    assertEquals(s.rotateLeft(1), sRotated)
    assertEquals(s.rotateLeft(6), sRotated)
    assertEquals(s.rotateLeft(-4), sRotated)
  }

}
