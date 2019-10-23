# scala-collection-contrib

[<img src="https://travis-ci.org/scala/scala-collection-contrib.svg?branch=master"/>](https://travis-ci.org/scala/scala-collection-contrib)
[<img src="https://img.shields.io/maven-central/v/org.scala-lang.modules/scala-collection-contrib_2.13.svg?label=scala+2.13"/>](http://search.maven.org/#search%7Cga%7C1%7Cg%3Aorg.scala-lang.modules%20a%3Ascala-collection-contrib_2.13)

This module provides various additions to the Scala 2.13 standard collections.

## Usage

If you're using sbt, you can add the dependency as follows:

```
libraryDependencies += "org.scala-lang.modules" %% "scala-collection-contrib" % "0.2.0"
```

Here is the [full Scaladoc](https://static.javadoc.io/org.scala-lang.modules/scala-collection-contrib_2.13/0.2.0/scala/collection/index.html).

### New collection types

These collections are in the `scala.collection` package.

- [`MultiSet`](https://static.javadoc.io/org.scala-lang.modules/scala-collection-contrib_2.13/0.2.0/scala/collection/MultiSet.html) (both [mutable](https://static.javadoc.io/org.scala-lang.modules/scala-collection-contrib_2.13/0.2.0/scala/collection/mutable/MultiSet.html) and [immutable](https://static.javadoc.io/org.scala-lang.modules/scala-collection-contrib_2.13/0.2.0/scala/collection/immutable/MultiSet.html))
- [`SortedMultiSet`](https://static.javadoc.io/org.scala-lang.modules/scala-collection-contrib_2.13/0.2.0/scala/collection/SortedMultiSet.html) (both [mutable](https://static.javadoc.io/org.scala-lang.modules/scala-collection-contrib_2.13/0.2.0/scala/collection/mutable/SortedMultiSet.html) and [immutable](https://static.javadoc.io/org.scala-lang.modules/scala-collection-contrib_2.13/0.2.0/scala/collection/immutable/SortedMultiSet.html))
- [`MultiDict`](https://static.javadoc.io/org.scala-lang.modules/scala-collection-contrib_2.13/0.2.0/scala/collection/MultiDict.html) (both [mutable](https://static.javadoc.io/org.scala-lang.modules/scala-collection-contrib_2.13/0.2.0/scala/collection/mutable/MultiDict.html) and [immutable](https://static.javadoc.io/org.scala-lang.modules/scala-collection-contrib_2.13/0.2.0/scala/collection/immutable/MultiDict.html))
- [`SortedMultiDict`](https://static.javadoc.io/org.scala-lang.modules/scala-collection-contrib_2.13/0.2.0/scala/collection/SortedMultiDict.html) (both [mutable](https://static.javadoc.io/org.scala-lang.modules/scala-collection-contrib_2.13/0.2.0/scala/collection/mutable/SortedMultiDict.html) and [immutable](https://static.javadoc.io/org.scala-lang.modules/scala-collection-contrib_2.13/0.2.0/scala/collection/immutable/SortedMultiDict.html))

### New operations

The new operations are provided via an implicit enrichment. You need to add the following
import to make them available:

```scala
import scala.collection.decorators._
```

The following operations are provided:

- [`BitSet`](https://static.javadoc.io/org.scala-lang.modules/scala-collection-contrib_2.13/0.2.0/scala/collection/decorators/BitSetDecorator.html): <<, >>, foldSomeLeft, fullOuterJoin, intersperse, intersperse, join, lazyFoldLeft, lazyFoldRight, leftOuterJoin, mergeByKey, mergeByKeyWith, replaced, rightOuterJoin, splitBy, zipByKey, zipByKeyWith
- [`Iterable`](https://static.javadoc.io/org.scala-lang.modules/scala-collection-contrib_2.13/0.2.0/scala/collection/decorators/IterableDecorator.html): foldSomeLeft, fullOuterJoin, intersperse, intersperse, join, lazyFoldLeft, lazyFoldRight, leftOuterJoin, mergeByKey, mergeByKeyWith, replaced, rightOuterJoin, splitBy, zipByKey, zipByKeyWith
- [`Iterator`](https://static.javadoc.io/org.scala-lang.modules/scala-collection-contrib_2.13/0.2.0/scala/collection/decorators/IteratorDecorator.html): foldSomeLeft, fullOuterJoin, intersperse, intersperse, join, lazyFoldLeft, lazyFoldRight, leftOuterJoin, mergeByKey, mergeByKeyWith, replaced, rightOuterJoin, splitBy, zipByKey, zipByKeyWith, foldSomeLeft, intersperse, intersperse, lazyFoldLeft, lazyFoldRight, splitBy
- [`Map`](https://static.javadoc.io/org.scala-lang.modules/scala-collection-contrib_2.13/0.2.0/scala/collection/decorators/MapDecorator.html): foldSomeLeft, fullOuterJoin, intersperse, intersperse, join, lazyFoldLeft, lazyFoldRight, leftOuterJoin, mergeByKey, mergeByKeyWith, replaced, rightOuterJoin, splitBy, zipByKeyWith
- [`Seq`](https://static.javadoc.io/org.scala-lang.modules/scala-collection-contrib_2.13/0.2.0/scala/collection/decorators/SeqDecorator.html): foldSomeLeft, intersperse, join, lazyFoldLeft, lazyFoldRight, leftOuterJoin, mergeByKey, mergeByKeyWith, replaced, rightOuterJoin, splitBy, zipByKey, zipByKeyWith


## Maintenance status

This module is community-maintained.  If you are interested in
participating, please jump right in on issues and pull requests.

## Releasing

As with other Scala standard modules, build and release infrastructure
is provided by the
[sbt-scala-module](https://github.com/scala/sbt-scala-module/) sbt
plugin.
