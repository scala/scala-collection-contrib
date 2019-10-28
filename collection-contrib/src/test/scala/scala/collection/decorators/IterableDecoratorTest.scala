package scala.collection
package decorators

import org.junit.{Assert, Test}

import scala.collection.immutable.{LazyList, List, Map, Range}

class IterableDecoratorTest {

  @Test
  def foldSomeLeft(): Unit = {
      val r = Range(0, 100)
      Assert.assertEquals(0, r.foldSomeLeft(0)((x, y) => None))
      Assert.assertEquals(10, r.foldSomeLeft(0)((x, y) => if (y > 10) None else Some(y)))
      Assert.assertEquals(55, r.foldSomeLeft(0)((x, y) => if (y > 10) None else Some(x + y)))
      Assert.assertEquals(4950, r.foldSomeLeft(0)((x, y) => Some(x + y)))

      Assert.assertEquals(10, List[Int]().foldSomeLeft(10)((x, y) => Some(x + y)))
    }

  @Test
  def lazyFoldLeftIsStackSafe(): Unit = {
    val bigList = List.range(1, 50000)
    def sum(as: Iterable[Int]): Int =
      as.lazyFoldLeft(0)(_ + _)

    Assert.assertEquals(sum(bigList), 1249975000)
  }

  @Test
  def lazyFoldLeftIsLazy(): Unit = {
    val nats = LazyList.from(0)
    def exists[A](as: Iterable[A])(f: A => Boolean): Boolean =
      as.lazyFoldLeft(false)(_ || f(_))
    
    Assert.assertTrue(exists(nats)(_ > 100000))
  }

  @Test def lazyFoldRightIsLazy(): Unit = {
    val xs = LazyList.from(0)
    def chooseOne(x: Int): Either[Int, Int => Int]= if (x < (1 << 16)) Right(identity) else Left(x)

    Assert.assertEquals(1 << 16, xs.lazyFoldRight(0)(chooseOne))
  }

  @Test
  def hasIterableOpsWorksWithStringAndMap(): Unit = {
    val result = "foo".foldSomeLeft(0) { case (_, 'o') => None case (n, _) => Some(n + 1) }
    Assert.assertEquals(1, result)

    val result2 =
      Map(1 -> "foo", 2 -> "bar").foldSomeLeft(0) {
        case (n, (k, _)) => if (k == -1) None else Some(n + 1)
      }
    Assert.assertEquals(2, result2)
  }

  @Test
  def splitByShouldHonorEmptyIterator(): Unit = {
    val split = Vector.empty[Int].splitBy(identity)
    Assert.assertEquals(Vector.empty, split)
  }

  @Test
  def splitByShouldReturnSingleSeqWhenSingleElement(): Unit = {
    val value = Vector("1")
    val split = value.splitBy(identity)
    Assert.assertEquals(Vector(value), split)
  }

  @Test
  def splitByShouldReturnSingleSeqWhenAllElHaveTheSameKey(): Unit = {
    val value = Vector("1", "1", "1")
    val split = value.splitBy(identity)
    Assert.assertEquals(Vector(value), split)
  }

  @Test
  def splitByShouldReturnVectorOfVectorOrConsecutiveElementsWithTheSameKey(): Unit = {
    val value = Vector("1", "2", "2", "3", "3", "3", "2", "2")
    val split: Vector[Vector[String]] = value.splitBy(identity)
    Assert.assertEquals(Vector(Vector("1"), Vector("2", "2"), Vector("3", "3", "3"), Vector("2", "2")), split)
  }

  @Test
  def splitByShouldReturnListOfListOfConsecutiveElementsWithTheSameKey(): Unit = {
    val value = List("1", "2", "2", "3", "3", "3", "2", "2")
    val split: List[List[String]] = value.splitBy(identity)
    Assert.assertEquals(List(List("1"), List("2", "2"), List("3", "3", "3"), List("2", "2")), split)
  }

  @Test
  def splitByShouldReturnSetOfSetOfConsecutiveElementsWithTheSameKey(): Unit = {
    val value = Set("1", "2", "2", "3", "3", "3", "2", "2")
    val split: Set[Set[String]] = value.splitBy(identity)
    Assert.assertEquals(Set(Set("1"), Set("2"), Set("3")), split)
  }
}
