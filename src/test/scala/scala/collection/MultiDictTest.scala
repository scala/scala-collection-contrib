package scala.collection

import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import scala.collection.immutable.List
import org.junit.{Assert, Test}

@RunWith(classOf[JUnit4])
class MultiDictTest {

  @Test
  def equality(): Unit = {
    val mm1 = MultiDict("a" -> 1, "b" -> 0, "b" -> 1, "c" -> 3)
    val mm2 = MultiDict("a" -> 1, "b" -> 0, "b" -> 1, "c" -> 3)

    Assert.assertEquals(mm2, mm1)
    Assert.assertEquals(mm1, mm2)
    Assert.assertEquals(mm1.##, mm2.##)
  }

  @Test
  def access(): Unit = {
    val mm = MultiDict("a" -> 1, "b" -> 0, "b" -> 1, "c" -> 3)

    Assert.assertEquals(Set(0, 1), mm.get("b"))
    Assert.assertEquals(Set.empty, mm.get("d"))
    Assert.assertTrue(mm.containsKey("a"))
    Assert.assertFalse(mm.containsKey("d"))
    Assert.assertTrue(mm.containsEntry("a" -> 1))
    Assert.assertFalse(mm.containsEntry("a" -> 2))
    Assert.assertFalse(mm.containsEntry("d" -> 2))
    Assert.assertTrue(mm.containsValue(1))
    Assert.assertTrue(mm.containsValue(3))
    Assert.assertFalse(mm.containsValue(4))
  }

  @Test
  def concat(): Unit = {
    Assert.assertEquals(
      MultiDict(1 -> true, 1 -> false),
      MultiDict(1 -> true).concat(MultiDict(1 -> false))
    )
    Assert.assertEquals(
      MultiDict("a" -> 1, "a" -> 2, "a" -> 3),
      MultiDict("a" -> 1).concatSets(List("a" -> Set(2, 3)))
    )
  }

  @Test
  def map(): Unit = {
    Assert.assertEquals(
      MultiDict("A" -> 1, "B" -> 1, "B" -> 2),
      MultiDict("a" -> 1, "b" -> 1, "b" -> 2).map { case (k, v) => (k.toUpperCase, v) }
    )
    Assert.assertEquals(
      MultiDict(1 -> true, 1 -> true),
      MultiDict("a" -> true, "b" -> true).map { case (_, v) => 1 -> v }
    )
    Assert.assertEquals(
      MultiDict("c" -> 1, "c" -> 2, "c" -> 3, "c" -> 4),
      MultiDict("a" -> 1, "b" -> 2, "b" -> 3).mapSets { _ => "c" -> Set(1, 2, 3, 4) }
    )
  }

  @Test
  def withFilter(): Unit = {
    val filtered =
      for {
        (k, v) <- MultiDict("a" -> 1, "b" -> 2)
        if k == "a" && v % 2 == 0
      } yield (k, v)
    val filteredT: MultiDict[String, Int] = filtered
    Assert.assertEquals(Seq.empty, filtered.toSeq)
  }
  @Test
  def testToString(): Unit = {

    val prefix = "MultiDict("
    val suffix = ")"

    def run(ms: MultiDict[Int, Int]): Unit = {
      val actual = ms.toString
      assert(actual.startsWith(prefix), s"`$actual` does not start with `$prefix`")
      assert(actual.endsWith(suffix), s"`$actual` does not end with `$suffix`")

      // The order of elements in the multiset are not defined, so this test should be robust to order changes

      val expected =
        actual
          .stripPrefix(prefix)
          .stripSuffix(suffix)
          .split(",")
          .iterator
          .flatMap { s =>
            if (s.isEmpty) None
            else {
              val Array(keyString, valueString) = s.split("->")
              Some(keyString.trim.toInt -> valueString.trim.toInt)
            }
          }
          .to(MultiDict)
      Assert.assertEquals(ms, expected)
    }

    def runForFactory(factory: MapFactory[MultiDict]): Unit = {
      Assert.assertEquals(factory().toString, s"$prefix$suffix")
      Assert.assertEquals(factory(1 -> 1).toString, s"${prefix}1 -> 1${suffix}")

      run(factory())
      run(factory(1 -> 1))
      run(factory(1234 -> 2))
      run(factory(1 -> 5,2 -> 6,3 -> 7))
      run(factory(1 -> 1,1 -> 2,1 -> 3,2 -> 1,3 -> 3))
      run(factory(1 -> 1,1 -> 2,1 -> 3,2 -> 4,2 -> 5,2 -> 6,2 -> 7,3 -> 8))
    }

    runForFactory(MultiDict)
    runForFactory(mutable.MultiDict)
    runForFactory(immutable.MultiDict)
  }

}
