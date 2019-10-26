package scala.collection
package decorators

import org.junit.{Assert, Test}

import scala.util.Try

class IteratorDecoratorTest {
  @Test
  def intersperseShouldIntersperseASeparator(): Unit = {
    Assert.assertEquals(Seq(1, 0, 2, 0, 3), Iterator(1, 2, 3).intersperse(0).toSeq)
    Assert.assertEquals(Seq('a', ',', 'b', ',', 'c'), Iterator('a', 'b', 'c').intersperse(',').toSeq)
    Assert.assertEquals(Seq('a'), Iterator('a').intersperse(',').toSeq)
    Assert.assertEquals(Seq.empty, Iterator().intersperse(',').toSeq)
    // Works with infinite iterators:
    Assert.assertEquals(Seq(1, 0, 2, 0, 3), Iterator.from(1).intersperse(0).take(5).toSeq)
  }

  @Test
  def intersperseShouldIntersperseASeparatorAndInsertStartAndEnd(): Unit = {
    Assert.assertEquals(Seq(-1, 1, 0, 2, 0, 3, 99), Iterator(1, 2, 3).intersperse(-1, 0, 99).toSeq)
    Assert.assertEquals(Seq('[', 'a', ',', 'b', ',', 'c', ']'),
      Iterator('a', 'b', 'c').intersperse('[', ',', ']').toSeq)
    Assert.assertEquals(Seq('[', 'a', ']'), Iterator('a').intersperse('[', ',', ']').toSeq)
    Assert.assertEquals(Seq('[', ']'), Iterator().intersperse('[', ',', ']').toSeq)
    // Works with infinite iterators:
    Assert.assertEquals(Seq(-1, 1, 0, 2, 0, 3), Iterator.from(1).intersperse(-1, 0, 99).take(6).toSeq)
  }

  @Test
  def foldSomeLeftShouldFold(): Unit = {
    def sumOp(acc: Int, e: Int): Option[Int] = if (e == 4) None else Some(acc + e)
    Assert.assertEquals(0, Iterator().foldSomeLeft(0)(sumOp))
    Assert.assertEquals(6, Iterator(1, 2, 3).foldSomeLeft(0)(sumOp))
    Assert.assertEquals(6, Iterator(1, 2, 3, 4, 5).foldSomeLeft(0)(sumOp))
    Assert.assertEquals(0, Iterator(4, 5).foldSomeLeft(0)(sumOp))
    // Works with infinite iterators:
    def sumMax4(acc: Int, e: Int): Option[Int] = if (acc == 4) None else Some(acc + e)
    Assert.assertEquals(4, Iterator.continually(1).foldSomeLeft(0)(sumMax4))
  }

  @Test
  def lazyFoldLeftShouldFold(): Unit = {
    // Notice how sumOp doesn't evaluate `e` under some conditions.
    def sumOp(acc: Int, e: => Int): Int = if (acc >= 5) acc else acc + e
    Assert.assertEquals(0, Iterator().lazyFoldLeft(0)(sumOp))
    Assert.assertEquals(3, Iterator(1, 1, 1).lazyFoldLeft(0)(sumOp))
    Assert.assertEquals(6, Iterator(1, 2, 3, 4, 5).lazyFoldLeft(0)(sumOp))
    Assert.assertEquals(5, Iterator(1, 1, 1, 1, 1, 1, 1, 1).lazyFoldLeft(0)(sumOp))
    Assert.assertEquals(9, Iterator(4, 5).lazyFoldLeft(0)(sumOp))
    Assert.assertEquals(9, Iterator(4, 5, 1).lazyFoldLeft(0)(sumOp))
    Assert.assertEquals(10, Iterator(10, 20, 30).lazyFoldLeft(0)(sumOp))
    // Works with infinite iterators:
    Assert.assertEquals(5, Iterator.continually(1).lazyFoldLeft(0)(sumOp))
  }

  @Test
  def lazyFoldLeftShouldFoldWeirdEdgeCases(): Unit = {
    // `delayedSumOp` doesn't return `acc`, causing a delayed stop of the iteration.
    def delayedSumOp(acc: Int, e: => Int): Int = if (acc >= 5) 5 else acc + e
    Assert.assertEquals(0, Iterator().lazyFoldLeft(0)(delayedSumOp))
    Assert.assertEquals(3, Iterator(1, 1, 1).lazyFoldLeft(0)(delayedSumOp))
    Assert.assertEquals(9, Iterator(4, 5).lazyFoldLeft(0)(delayedSumOp))
    Assert.assertEquals(5, Iterator(4, 5, 1).lazyFoldLeft(0)(delayedSumOp))
    Assert.assertEquals(5, Iterator(6, 1).lazyFoldLeft(0)(delayedSumOp))
    // Works with infinite iterators:
    Assert.assertEquals(5, Iterator.continually(1).lazyFoldLeft(0)(delayedSumOp))

    // `alwaysGrowingSumOp` returns a new value every time, causing no stop in the iteration.
    def alwaysGrowingSumOp(acc: Int, e: => Int): Int = if (acc >= 5) acc + 1 else acc + e
    Assert.assertEquals(0, Iterator().lazyFoldLeft(0)(alwaysGrowingSumOp))
    Assert.assertEquals(3, Iterator(1, 1, 1).lazyFoldLeft(0)(alwaysGrowingSumOp))
    Assert.assertEquals(9, Iterator(5, 10, 10, 10, 10).lazyFoldLeft(0)(alwaysGrowingSumOp))
    Assert.assertEquals(9, Iterator(4, 5).lazyFoldLeft(0)(alwaysGrowingSumOp))
    Assert.assertEquals(10, Iterator(4, 5, 20).lazyFoldLeft(0)(alwaysGrowingSumOp))
  }

  @Test
  def lazyFoldRightShouldFold(): Unit = {
    def sumOp(acc: Int): Either[Int, Int => Int] = if (acc >= 5) Left(acc) else Right(acc + _)
    Assert.assertEquals(0, Iterator().lazyFoldRight(0)(sumOp))
    Assert.assertEquals(3, Iterator(1, 1, 1).lazyFoldRight(0)(sumOp))
    Assert.assertEquals(15, Iterator(1, 2, 3, 4, 5).lazyFoldRight(0)(sumOp))
    Assert.assertEquals(8, Iterator(1, 1, 1, 1, 1, 1, 1, 1).lazyFoldRight(0)(sumOp))
    Assert.assertEquals(5, Iterator(5, 4).lazyFoldRight(0)(sumOp))
    Assert.assertEquals(6, Iterator(1, 5, 4).lazyFoldRight(0)(sumOp))
    Assert.assertEquals(32, Iterator(32, 21, 10).lazyFoldRight(0)(sumOp))
  }

  @Test
  def splitByShouldHonorEmptyIterator(): Unit = {
    val groupedIterator = Iterator.empty.splitBy(identity)
    Assert.assertFalse(groupedIterator.hasNext)
    Assert.assertEquals(Try(Iterator.empty.next()).toString, Try(groupedIterator.next).toString)
  }

  @Test
  def splitByShouldReturnIteratorOfSingleSeqWhenAllElHaveTheSameKey(): Unit = {
    val value = Vector("1", "1", "1")
    val groupedIterator = value.iterator.splitBy(identity)
    Assert.assertTrue(groupedIterator.hasNext)
    Assert.assertEquals(value, groupedIterator.next.toVector)
    Assert.assertFalse(groupedIterator.hasNext)
    Assert.assertEquals(Try(Iterator.empty.next()).toString, Try(groupedIterator.next).toString)
  }

  @Test
  def splitByShouldReturnIteratorOfSeqOfConsecutiveElementsWithTheSameKey(): Unit = {
    val value = Vector("1", "2", "2", "3", "3", "3", "2", "2")
    val groupedIterator = value.iterator.splitBy(identity)
    Assert.assertTrue(groupedIterator.hasNext)
    Assert.assertEquals(Vector("1"), groupedIterator.next.toVector)
    Assert.assertTrue(groupedIterator.hasNext)
    Assert.assertEquals(Vector("2", "2"), groupedIterator.next.toVector)
    Assert.assertTrue(groupedIterator.hasNext)
    Assert.assertEquals(Vector("3", "3", "3"), groupedIterator.next.toVector)
    Assert.assertTrue(groupedIterator.hasNext)
    Assert.assertEquals(Vector("2", "2"), groupedIterator.next.toVector)
    Assert.assertFalse(groupedIterator.hasNext)
    Assert.assertEquals(Try(Iterator.empty.next()).toString, Try(groupedIterator.next).toString)
  }

  @Test
  def splitByShouldSplitByFunction(): Unit = {
    Assert.assertEquals(Seq(Seq((1,1), (1,2)), Seq((2,3))), Iterator((1,1), (1,2), (2,3)).splitBy(_._1).toSeq)
    Assert.assertEquals(
      Seq(Seq((1,1), (1,2)), Seq((2,3)), Seq((1,4))),
      Iterator((1,1), (1,2), (2,3), (1,4)).splitBy(_._1).toSeq
    )
  }

  @Test
  def splitByShouldSplitInfiniteIterators(): Unit = {
    Assert.assertEquals(
      Seq(Seq(0, 0, 0), Seq(1, 1, 1), Seq(2, 2, 2)),
      Iterator.from(0).map(_ / 3).splitBy(identity).take(3).toSeq
    )
  }
}
