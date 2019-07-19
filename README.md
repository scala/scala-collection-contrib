# scala-collection-contrib

[<img src="https://travis-ci.org/scala/scala-collection-contrib.svg?branch=master"/>](https://travis-ci.org/scala/scala-collection-contrib)
[<img src="https://img.shields.io/maven-central/v/org.scala-lang.modules/scala-collection-contrib_2.13.svg?label=scala+2.13"/>](http://search.maven.org/#search%7Cga%7C1%7Cg%3Aorg.scala-lang.modules%20a%3Ascala-collection-contrib_2.13)

This module provides various additions to the Scala 2.13 standard collections.

## Usage

If you're using sbt, you can add the dependency as follows:

```
libraryDependencies += "org.scala-lang.modules" %% "scala-collection-contrib" % "0.1.0"
```

## New collection types

- `MultiSet` (both mutable and immutable)
- `SortedMultiSet` (both mutable and immutable)
- `MultiDict` (both mutable and immutable)
- `SortedMultiDict` (both mutable and immutable)

## New operations

The new operations are provided via an implicit enrichment. You need to add the following
import to make them available:

~~~ scala
import scala.collection.decorators._
~~~

The following operations are provided:

- `Seq`
    - `intersperse`
    - `replaced`
- `Map`
    - `zipByKey` / `join` / `zipByKeyWith`
    - `mergeByKey` / `fullOuterJoin` / `mergeByKeyWith` / `leftOuterJoin` / `rightOuterJoin`

## Maintenance status

This module is community-maintained.  If you are interested in
participating, please jump right in on issues and pull requests.

## Releasing

As with other Scala standard modules, build and release infrastructure
is provided by the
[sbt-scala-module](https://github.com/scala/sbt-scala-module/) sbt
plugin.
