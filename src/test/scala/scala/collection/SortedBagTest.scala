package scala.collection


import org.junit.{Assert, Test}
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(classOf[JUnit4])
class SortedBagTest {

  def sortedBag(sms: SortedBag[Int]): Unit = {
    Assert.assertEquals(1, sms.get(1))
    Assert.assertEquals(2, sms.get(2))
    Assert.assertEquals(1, sms.firstKey)
    Assert.assertEquals(3, sms.lastKey)
    Assert.assertEquals(SortedBag(3, 2, 2), sms.rangeFrom(2))
  }

  @Test def run(): Unit = {
    sortedBag(immutable.SortedBag(2, 1, 3, 2))
    sortedBag(mutable.SortedBag(2, 1, 3, 2))
  }

}
