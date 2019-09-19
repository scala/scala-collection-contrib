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

- `Seq`
    - [`intersperse`](https://static.javadoc.io/org.scala-lang.modules/scala-collection-contrib_2.13/0.2.0/scala/collection/decorators/SeqDecorator.html#intersperse[B>:SeqDecorator.this.seq.A,That]\(start:B,sep:B,end:B\)\(implicitbf:scala.collection.BuildFrom[C,B,That]\):That)
    - [`replaced`](https://static.javadoc.io/org.scala-lang.modules/scala-collection-contrib_2.13/0.2.0/scala/collection/decorators/SeqDecorator.html#replaced[B>:SeqDecorator.this.seq.A,That]\(elem:B,replacement:B\)\(implicitbf:scala.collection.BuildFrom[C,B,That]\):That)
- `Map`
    - [`zipByKey`](https://static.javadoc.io/org.scala-lang.modules/scala-collection-contrib_2.13/0.2.0/scala/collection/decorators/MapDecorator.html#zipByKey[W,That]\(other:scala.collection.Map[MapDecorator.this.map.K,W]\)\(implicitbf:scala.collection.BuildFrom[C,\(MapDecorator.this.map.K,\(MapDecorator.this.map.V,W\)\),That]\):That) / [`join`](https://static.javadoc.io/org.scala-lang.modules/scala-collection-contrib_2.13/0.2.0/scala/collection/decorators/MapDecorator.html#join[W,That]\(other:scala.collection.Map[MapDecorator.this.map.K,W]\)\(implicitbf:scala.collection.BuildFrom[C,\(MapDecorator.this.map.K,\(MapDecorator.this.map.V,W\)\),That]\):That) / [`zipByKeyWith`](https://static.javadoc.io/org.scala-lang.modules/scala-collection-contrib_2.13/0.2.0/scala/collection/decorators/MapDecorator.html#zipByKeyWith[W,X,That]\(other:scala.collection.Map[MapDecorator.this.map.K,W]\)\(f:\(MapDecorator.this.map.V,W\)=>X\)\(implicitbf:scala.collection.BuildFrom[C,\(MapDecorator.this.map.K,X\),That]\):That)
    - [`mergeByKey`](https://static.javadoc.io/org.scala-lang.modules/scala-collection-contrib_2.13/0.2.0/scala/collection/decorators/MapDecorator.html#mergeByKey[W,That]\(other:scala.collection.Map[MapDecorator.this.map.K,W]\)\(implicitbf:scala.collection.BuildFrom[C,\(MapDecorator.this.map.K,\(Option[MapDecorator.this.map.V],Option[W]\)\),That]\):That) / [`fullOuterJoin`](https://static.javadoc.io/org.scala-lang.modules/scala-collection-contrib_2.13/0.2.0/scala/collection/decorators/MapDecorator.html#fullOuterJoin[W,That]\(other:scala.collection.Map[MapDecorator.this.map.K,W]\)\(implicitbf:scala.collection.BuildFrom[C,\(MapDecorator.this.map.K,\(Option[MapDecorator.this.map.V],Option[W]\)\),That]\):That) / [`mergeByKeyWith`](https://static.javadoc.io/org.scala-lang.modules/scala-collection-contrib_2.13/0.2.0/scala/collection/decorators/MapDecorator.html#mergeByKeyWith[W,X,That]\(other:scala.collection.Map[MapDecorator.this.map.K,W]\)\(f:PartialFunction[\(Option[MapDecorator.this.map.V],Option[W]\),X]\)\(implicitbf:scala.collection.BuildFrom[C,\(MapDecorator.this.map.K,X\),That]\):That) / [`leftOuterJoin`](https://static.javadoc.io/org.scala-lang.modules/scala-collection-contrib_2.13/0.2.0/scala/collection/decorators/MapDecorator.html#leftOuterJoin[W,That]\(other:scala.collection.Map[MapDecorator.this.map.K,W]\)\(implicitbf:scala.collection.BuildFrom[C,\(MapDecorator.this.map.K,\(MapDecorator.this.map.V,Option[W]\)\),That]\):That) / [`rightOuterJoin`](https://static.javadoc.io/org.scala-lang.modules/scala-collection-contrib_2.13/0.2.0/scala/collection/decorators/MapDecorator.html#rightOuterJoin[W,That]\(other:scala.collection.Map[MapDecorator.this.map.K,W]\)\(implicitbf:scala.collection.BuildFrom[C,\(MapDecorator.this.map.K,\(Option[MapDecorator.this.map.V],W\)\),That]\):That)
- `BitSet`
    - [`<<`](https://static.javadoc.io/org.scala-lang.modules/scala-collection-contrib_2.13/0.2.0/scala/collection/decorators/BitSetDecorator.html)
    - [`>>`](https://static.javadoc.io/org.scala-lang.modules/scala-collection-contrib_2.13/0.2.0/scala/collection/decorators/BitSetDecorator.html)

## Maintenance status

This module is community-maintained.  If you are interested in
participating, please jump right in on issues and pull requests.

## Releasing

As with other Scala standard modules, build and release infrastructure
is provided by the
[sbt-scala-module](https://github.com/scala/sbt-scala-module/) sbt
plugin.
