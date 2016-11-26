
put all your code to scala if you only need to support 2.11. If you need your code to compile on both 2.10 and 2.11, put incompatible things to their respective directories (scala-2.11 and scala-2.10) and compatible things (i.e. those which can be shared between versions) to scala



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