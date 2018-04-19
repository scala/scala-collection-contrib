# Scala Collection Contrib

This module provides extra features to the Scala standard collections.

## New Collection Types

- `MultiSet` (both mutable and immutable)
- `SortedMultiSet` (both mutable and immutable)
- `MultiDict` (both mutable and immutable)
- `SortedMultiDict` (both mutable and immutable)

## New Operations

The new operations are provided via an implicit enrichment. You need to add the following
import to make them available:

~~~ scala
import scala.collection.decorators._
~~~

The following operations are provided:

- `Seq`
    - `intersperse`
- `Map`
    - `zipByKey` / `join` / `zipByKeyWith`
    - `mergeByKey` / `fullOuterJoin` / `mergeByKeyWith` / `leftOuterJoin` / `rightOuterJoin`

