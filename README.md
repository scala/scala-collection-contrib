# scala-collection-contrib

This module provides various additions to the Scala 2.13 standard collections.

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
