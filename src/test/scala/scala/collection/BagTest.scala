package scala.collection

import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import scala.collection.immutable.List
import org.junit.{Assert, Test}

@RunWith(classOf[JUnit4])
class BagTest {

  @Test
  def equality(): Unit = {
    val ms1 = Bag("a", "b", "b", "c")
    val ms2 = Bag("a", "b", "b", "c")

    Assert.assertEquals(ms2, ms1)
    Assert.assertEquals(ms1, ms2)
    Assert.assertEquals(ms1.##, ms2.##)
  }

  @Test
  def concat(): Unit = {
    Assert.assertEquals(
      Bag(1, 1),
      Bag(1).concat(Bag(1))
    )
    Assert.assertEquals(
      Bag("a", "a", "a"),
      Bag("a").concatOccurrences(List(("a", 2)))
    )
  }

  @Test
  def map(): Unit = {
    Assert.assertEquals(
      Bag("A", "B", "B"),
      Bag("a", "b", "b").map(_.toUpperCase)
    )
    Assert.assertEquals(
      Bag(1, 1),
      Bag("a", "b").map(_ => 1)
    )
    Assert.assertEquals(
      Bag("c", "c", "c", "c"),
      Bag("a", "b", "b").mapOccurrences { _ => ("c", 2) }
    )
  }

  @Test
  def testToString(): Unit = {

    def run(ms: Bag[Int]): Unit = {
      val actual = ms.toString
      assert(actual.startsWith("Bag("), s"`$actual` does not start with `Bag(`")
      assert(actual.endsWith(")"), s"`$actual` does not end with `)`")

      // The order of elements in the bag are not defined, so this test should be robust to order changes
      Assert.assertEquals(ms,
        actual
          .stripPrefix("Bag(")
          .stripSuffix(")")
          .split(",")
          .iterator
          .flatMap (_.trim match {
            case "" => None
            case s => Some(s.toInt)
          })
          .to(Bag))
    }

    def runForFactory(factory: IterableFactory[Bag]): Unit = {
      Assert.assertEquals(factory().toString, "Bag()")
      Assert.assertEquals(factory(1).toString, "Bag(1)")

      run(factory())
      run(factory(1))
      run(factory(1234))
      run(factory(1,2,3))
      run(factory(1,1,1,2,3))
      run(factory(1,1,1,2,2,2,2,3))
    }

    runForFactory(Bag)
    runForFactory(mutable.Bag)
    runForFactory(immutable.Bag)
  }

}
