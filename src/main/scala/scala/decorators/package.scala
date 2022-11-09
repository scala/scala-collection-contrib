package scala

import scala.language.implicitConversions

package object decorators {
  implicit def optionDecorator[A](opt: Option[A]): OptionDecorator[A] = new OptionDecorator(opt) 
  
}
