package scala
package decorators

import org.junit.Assert.assertEquals
import org.junit.Test
import scala.collection.immutable._

class OptionDecoratorTest {

  @Test def onEmptyNone(): Unit = {
    var isSet = false
    def set: Unit = isSet = true
    None.onEmpty(set)
    assertEquals(true, isSet)
  }

  @Test def onEmptySome(): Unit = {
    var isSet = false
    def set: Unit = isSet = true
    Some("potato").onEmpty(set)
    assertEquals(false, isSet)
  }
}
