# scala-mania
sparing no effort &amp; noodling towards a functional brain




http://blog.originate.com/blog/2014/06/15/idiomatic-scala-your-options-do-not-match/

The most idiomatic way to use an scala.Option instance is to treat it as a collection or monad and use map, flatMap, filter, or foreach […] A less-idiomatic way to use scala.Option values is via pattern matching

```scala
opt match {
  case Some(a) => foo(a)
  case None => bar
}

opt map foo getOrElse bar
opt.fold(bar)(foo)

```
