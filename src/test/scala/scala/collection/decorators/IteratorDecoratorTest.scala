package scala.collection
package decorators

import org.junit.{Assert, Test}

import scala.util.Try

class IteratorDecoratorTest {
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
