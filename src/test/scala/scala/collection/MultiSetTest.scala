package scala.collection

import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import scala.collection.immutable.List
import org.junit.{Assert, Test}

@RunWith(classOf[JUnit4])
class MultiSetTest {

  @Test
  def equality(): Unit = {
    val ms1 = MultiSet("a", "b", "b", "c")
    val ms2 = MultiSet("a", "b", "b", "c")

    Assert.assertEquals(ms2, ms1)
    Assert.assertEquals(ms1, ms2)
    Assert.assertEquals(ms1.##, ms2.##)
  }

  @Test
  def concat(): Unit = {
    Assert.assertEquals(
      MultiSet(1, 1),
      MultiSet(1).concat(MultiSet(1))
    )
    Assert.assertEquals(
      MultiSet("a", "a", "a"),
      MultiSet("a").concatOccurrences(List(("a", 2)))
    )
  }

  @Test
  def map(): Unit = {
    Assert.assertEquals(
      MultiSet("A", "B", "B"),
      MultiSet("a", "b", "b").map(_.toUpperCase)
    )
    Assert.assertEquals(
      MultiSet(1, 1),
      MultiSet("a", "b").map(_ => 1)
    )
    Assert.assertEquals(
      MultiSet("c", "c", "c", "c"),
      MultiSet("a", "b", "b").mapOccurrences { _ => ("c", 2) }
    )
  }

  @Test
  def testToString(): Unit = {

    def run(ms: MultiSet[Int]): Unit = {
      val actual = ms.toString
      assert(actual.startsWith("MultiSet("), s"`$actual` does not start with `MultiSet(`")
      assert(actual.endsWith(")"), s"`$actual` does not end with `)`")

      // The order of elements in the multiset are not defined, so this test should be robust to order changes
      Assert.assertEquals(ms,
        actual
          .stripPrefix("MultiSet(")
          .stripSuffix(")")
          .split(",")
          .iterator
          .flatMap (_.trim match {
            case "" => None
            case s => Some(s.toInt)
          })
          .to(MultiSet))
    }

    def runForFactory(factory: IterableFactory[MultiSet]): Unit = {
      Assert.assertEquals(factory().toString, "MultiSet()")
      Assert.assertEquals(factory(1).toString, "MultiSet(1)")

      run(factory())
      run(factory(1))
      run(factory(1234))
      run(factory(1,2,3))
      run(factory(1,1,1,2,3))
      run(factory(1,1,1,2,2,2,2,3))
    }

    runForFactory(MultiSet)
    runForFactory(mutable.MultiSet)
    runForFactory(immutable.MultiSet)
  }

}
