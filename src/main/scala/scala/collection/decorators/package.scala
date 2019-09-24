package scala.collection

import scala.collection.generic.{IsIterable, IsMap, IsSeq}
import scala.language.implicitConversions

package object decorators {

  implicit def iteratorDecorator[A](it: Iterator[A]): IteratorDecorator[A] =
    new IteratorDecorator[A](it)

  implicit def IterableDecorator[C](coll: C)(implicit it: IsIterable[C]): IterableDecorator[C, it.type] =
    new IterableDecorator(coll)(it)

  implicit def SeqDecorator[C](coll: C)(implicit seq: IsSeq[C]): SeqDecorator[C, seq.type] =
    new SeqDecorator(coll)(seq)

  implicit def MapDecorator[C](coll: C)(implicit map: IsMap[C]): MapDecorator[C, map.type] =
    new MapDecorator(coll)(map)

  implicit def bitSetDecorator[C <: BitSet with BitSetOps[C]](bs: C): BitSetDecorator[C] =
    new BitSetDecorator(bs)

  implicit def mutableBitSetDecorator(bs: mutable.BitSet): MutableBitSetDecorator =
    new MutableBitSetDecorator(bs)

}
