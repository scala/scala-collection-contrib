package scala.collection
package decorators

import org.junit.{Assert, Test}

import scala.util.Try

class IteratorDecoratorTest {
  @Test
  def intersperseShouldIntersperseASeparator(): Unit = {
    Assert.assertEquals(Iterator(1, 2, 3).intersperse(0).toSeq, Seq(1, 0, 2, 0, 3))
    Assert.assertEquals(Iterator('a', 'b', 'c').intersperse(',').toSeq, Seq('a', ',', 'b', ',', 'c'))
    Assert.assertEquals(Iterator('a').intersperse(',').toSeq, Seq('a'))
    Assert.assertEquals(Iterator().intersperse(',').toSeq, Seq.empty)
  }

  @Test
  def intersperseShouldIntersperseASeparatorAndInsertStartAndEnd(): Unit = {
    Assert.assertEquals(Iterator(1, 2, 3).intersperse(-1, 0, 99).toSeq, Seq(-1, 1, 0, 2, 0, 3, 99))
    Assert.assertEquals(Iterator('a', 'b', 'c').intersperse('[', ',', ']').toSeq,
      Seq('[', 'a', ',', 'b', ',', 'c', ']'))
    Assert.assertEquals(Iterator('a').intersperse('[', ',', ']').toSeq, Seq('[', 'a', ']'))
    Assert.assertEquals(Iterator().intersperse('[', ',', ']').toSeq, Seq('[', ']'))
  }

  @Test
  def splitByShouldHonorEmptyIterator(): Unit = {
    val groupedIterator = Iterator.empty.splitBy(identity)
    Assert.assertFalse(groupedIterator.hasNext)
    Assert.assertEquals(Try(groupedIterator.next).toString, Try(Iterator.empty.next()).toString)
  }

  @Test
  def splitByShouldReturnIteratorOfSingleSeqWhenAllElHaveTheSameKey(): Unit = {
    val value = Vector("1", "1", "1")
    val groupedIterator = value.iterator.splitBy(identity)
    Assert.assertTrue(groupedIterator.hasNext)
    Assert.assertEquals(groupedIterator.next.toVector, value)
    Assert.assertFalse(groupedIterator.hasNext)
    Assert.assertEquals(Try(groupedIterator.next).toString, Try(Iterator.empty.next()).toString)
  }

  @Test
  def splitByShouldReturnIteratorOfSeqOfConsecutiveElementsWithTheSameKey(): Unit = {
    val value = Vector("1", "2", "2", "3", "3", "3", "2", "2")
    val groupedIterator = value.iterator.splitBy(identity)
    Assert.assertTrue(groupedIterator.hasNext)
    Assert.assertEquals(groupedIterator.next.toVector, Vector("1"))
    Assert.assertTrue(groupedIterator.hasNext)
    Assert.assertEquals(groupedIterator.next.toVector, Vector("2", "2"))
    Assert.assertTrue(groupedIterator.hasNext)
    Assert.assertEquals(groupedIterator.next.toVector, Vector("3", "3", "3"))
    Assert.assertTrue(groupedIterator.hasNext)
    Assert.assertEquals(groupedIterator.next.toVector, Vector("2", "2"))
    Assert.assertFalse(groupedIterator.hasNext)
    Assert.assertEquals(Try(groupedIterator.next).toString, Try(Iterator.empty.next()).toString)
  }
}
