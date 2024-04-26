package scala.collection
package decorators

import org.junit.{Assert, Test}

class MapDecoratorTest {

  @Test
  def zipByKeyWith(): Unit = {
    val map1 = Map(1 -> "a", 2 -> "b")
    val map2 = Map(2 -> "c")
    val zipped = map1.zipByKeyWith(map2)(_ ++ _)
    val expected = Map(2 -> "bc")
    Assert.assertEquals(expected, zipped)

    val sortedMap1 = SortedMap(2 -> "a", 1 -> "b")
    val sortedMap2 = SortedMap(1 -> "d", 2 -> "c")
    val sortedZipped = sortedMap1.zipByKeyWith(sortedMap2)(_ ++ _)
    val sortedZippedT: SortedMap[Int, String] = sortedZipped
    val sortedExpected = SortedMap(1 -> "bd", 2 -> "ac")
    Assert.assertEquals(sortedExpected, sortedZipped)
  }

  @Test
  def joins(): Unit = {
    val map1 = Map(1 -> "a", 2 -> "b")
    val map2 = Map(2 -> "c", 3 -> "d")
    locally {
      val expected = Map(
        1 -> (Some("a"), None),
        2 -> (Some("b"), Some("c")),
        3 -> (None,      Some("d"))
      )
      Assert.assertEquals(expected, map1.fullOuterJoin(map2))
    }
    locally {
      val expected = Map(
        1 -> ("a", None),
        2 -> ("b", Some("c"))
      )
      Assert.assertEquals(expected, map1.leftOuterJoin(map2))
    }
    locally {
      val expected = Map(
        2 -> (Some("b"), "c"),
        3 -> (None,      "d")
      )
      Assert.assertEquals(expected, map1.rightOuterJoin(map2))
    }

    val sortedMap1 = SortedMap(2 -> "a", 1 -> "b")
    val sortedMap2 = SortedMap(2 -> "c", 3 -> "d")
    locally {
      val expected = SortedMap(
        1 -> (Some("b"), None),
        2 -> (Some("a"), Some("c")),
        3 -> (None,      Some("d"))
      )
      val expectedT: SortedMap[Int, (Option[String], Option[String])] = expected
      Assert.assertEquals(expected, sortedMap1.fullOuterJoin(sortedMap2))
    }
  }

  @Test
  def mapDecoratorWorksWithViews/*AndMutableMaps*/(): Unit = {
    val map1 = Map(1 -> "a", 2 -> "b")
    val map2 = Map(2 -> "c")
    val zipped = map1.view.zipByKeyWith(map2)(_ ++ _).to(Map)
    val expected = Map(2 -> "bc")
    Assert.assertEquals(expected, zipped)

//    val mutableMap1 = mutable.Map(1 -> "a", 2 -> "b")
//    val zipped2 = mutableMap1.zipByKeyWith(map2)(_ ++ _).to(Map)
//    Assert.assertEquals(expected, zipped2)
  }

  @Test
  def mergingByKeyPerformsFullOuterJoin(): Unit = {
    val arthur = "arthur.txt"

    val tyson = "tyson.txt"

    val sandra = "sandra.txt"

    val allKeys = Set(arthur, tyson, sandra)

    val sharedValue = 1

    val ourChanges = Map(
      (
        arthur,
        sharedValue
      ),
      (
        tyson,
        2
      )
    )

    {
      // In this test case, none of the associated values collide across keys...

      val theirChanges = Map(
        (
          arthur,
          sharedValue
        ),
        (
          sandra,
          3
        )
      )

      Assert.assertEquals("Expect the same keys to appear in the join taken either way around.", ourChanges.mergeByKey(theirChanges).keySet, theirChanges
        .mergeByKey(ourChanges)
        .keys)

      Assert.assertTrue("Expect the same associated values to appear in the join taken either way around, albeit swapped around and not necessarily in the same key order.",
        ourChanges
          .mergeByKey(theirChanges)
          .values
          .map(_.swap)
          .toList
          .sorted
          .sameElements(theirChanges.mergeByKey(ourChanges).values.toList.sorted))

      Assert.assertEquals("Expect all the keys to appear in an outer join.", ourChanges.mergeByKey(theirChanges).keys, allKeys)

      Assert.assertEquals("Expect all the keys to appear in an outer join.", theirChanges.mergeByKey(ourChanges).keys, allKeys)
    }

    {
      // In this test case, associated values collide across keys...

      val theirChangesRedux = Map(
        (
          arthur,
          sharedValue
        ),
        (
          sandra,
          sharedValue
        )
      )

      Assert.assertEquals("Expect the same keys to appear in the join taken either way around.", ourChanges.mergeByKey(theirChangesRedux).keySet, theirChangesRedux
        .mergeByKey(ourChanges)
        .keys)

      Assert.assertTrue("Expect the same associated values to appear in the join taken either way around, albeit swapped around and not necessarily in the same key order.",
        ourChanges
          .mergeByKey(theirChangesRedux)
          .values
          .map(_.swap)
          .toList
          .sorted
          .sameElements(theirChangesRedux.mergeByKey(ourChanges).values.toList.sorted))

      Assert.assertEquals("Expect all the keys to appear in an outer join.", ourChanges.mergeByKey(theirChangesRedux).keys, allKeys)

      Assert.assertEquals("Expect all the keys to appear in an outer join.", theirChangesRedux.mergeByKey(ourChanges).keys, allKeys)
    }
  }

}
