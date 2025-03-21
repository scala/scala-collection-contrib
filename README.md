# scala-collection-contrib

This module provides user-contributed additions to the Scala collections library.

It is published for Scala 2.13 and Scala 3. All platforms are supported (JVM, Scala.js, Scala Native).

## Usage

If you're using sbt, you can add the dependency as follows:

```
libraryDependencies += "org.scala-lang.modules" %% "scala-collection-contrib" % "0.3.0"
```

If you're using Scala.js or Scala Native, substitute `%%%` for `%%`.

Here is the [full Scaladoc](https://static.javadoc.io/org.scala-lang.modules/scala-collection-contrib_2.13/0.3.0/scala/collection/index.html).

### New collection types

These collections are in the `scala.collection` package.

- [`MultiSet`](https://static.javadoc.io/org.scala-lang.modules/scala-collection-contrib_2.13/0.3.0/scala/collection/MultiSet.html) (both [mutable](https://static.javadoc.io/org.scala-lang.modules/scala-collection-contrib_2.13/0.3.0/scala/collection/mutable/MultiSet.html) and [immutable](https://static.javadoc.io/org.scala-lang.modules/scala-collection-contrib_2.13/0.3.0/scala/collection/immutable/MultiSet.html))
- [`SortedMultiSet`](https://static.javadoc.io/org.scala-lang.modules/scala-collection-contrib_2.13/0.3.0/scala/collection/SortedMultiSet.html) (both [mutable](https://static.javadoc.io/org.scala-lang.modules/scala-collection-contrib_2.13/0.3.0/scala/collection/mutable/SortedMultiSet.html) and [immutable](https://static.javadoc.io/org.scala-lang.modules/scala-collection-contrib_2.13/0.3.0/scala/collection/immutable/SortedMultiSet.html))
- [`MultiDict`](https://static.javadoc.io/org.scala-lang.modules/scala-collection-contrib_2.13/0.3.0/scala/collection/MultiDict.html) (both [mutable](https://static.javadoc.io/org.scala-lang.modules/scala-collection-contrib_2.13/0.3.0/scala/collection/mutable/MultiDict.html) and [immutable](https://static.javadoc.io/org.scala-lang.modules/scala-collection-contrib_2.13/0.3.0/scala/collection/immutable/MultiDict.html))
- [`SortedMultiDict`](https://static.javadoc.io/org.scala-lang.modules/scala-collection-contrib_2.13/0.3.0/scala/collection/SortedMultiDict.html) (both [mutable](https://static.javadoc.io/org.scala-lang.modules/scala-collection-contrib_2.13/0.3.0/scala/collection/mutable/SortedMultiDict.html) and [immutable](https://static.javadoc.io/org.scala-lang.modules/scala-collection-contrib_2.13/0.3.0/scala/collection/immutable/SortedMultiDict.html))

### New operations

The new operations are provided via an implicit enrichment. You need to add the following
import to make them available:

```scala
import scala.collection.decorators._
```

The following operations are provided:

- [`BitSet`](https://static.javadoc.io/org.scala-lang.modules/scala-collection-contrib_2.13/latest/scala/collection/decorators/BitSetDecorator.html): <<, >>, ...
- [`mutable.BitSet`](https://static.javadoc.io/org.scala-lang.modules/scala-collection-contrib_2.13/latest/scala/collection/decorators/MutableBitSetDecorator.html): <<=, >>=, ...
- [`Iterable`](https://static.javadoc.io/org.scala-lang.modules/scala-collection-contrib_2.13/latest/scala/collection/decorators/IterableDecorator.html): foldSomeLeft, lazyFoldLeft, lazyFoldRight, splitBy, ...
- [`Iterator`](https://static.javadoc.io/org.scala-lang.modules/scala-collection-contrib_2.13/latest/scala/collection/decorators/IteratorDecorator.html): intersperse, foldSomeLeft, lazyFoldLeft, lazyFoldRight, splitBy, ...
- [`Map`](https://static.javadoc.io/org.scala-lang.modules/scala-collection-contrib_2.13/latest/scala/collection/decorators/MapDecorator.html): fullOuterJoin, leftOuterJoin, mergeByKey, mergeByKeyWith, rightOuterJoin, zipByKey, zipByKeyWith, ...
- [`Seq`](https://static.javadoc.io/org.scala-lang.modules/scala-collection-contrib_2.13/latest/scala/collection/decorators/SeqDecorator.html): intersperse, replaced, splitBy, ...


## Maintenance status

This module is community-maintained.  If you are interested in
participating, please jump right in on issues and pull requests.

## See also: scala-library-next

scala-collection-contrib's merge policy is liberal: we're happy to merge most things here, without too much review, so that people can try them out.

There is now (since 2020) the [scala-library-next repo](https://github.com/scala/scala-library-next), which is where we decide what will actually be added to the next version of the Scala standard library. The standards for merging things there are much more conservative.
