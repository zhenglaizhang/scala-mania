
/*
Scala's monad related syntactic sugar: "for comprehensions."


The rule here is simple

for (x <- expr) yield resultExpr
Expands to1

expr map {x => resultExpr}
And as a reminder, that's equivalent to

expr flatMap {x => unit(resultExpr)}




That's because the rule is recursive

for(x1 <- expr1;...x <- expr)
   yield resultExpr
expands to

expr1 flatMap {x1 =>
   for(...;x <- expr) yield resultExpr
}

This rule gets applied repeatedly until only one expression remains at which point the map form of expansion is used.








An Imperative "For"
"For" also has an imperative version for the cases where you're only calling a function for its side effects. In it you just drop the yield statement.

The expansion rule is much like the yield based version but foreach is used instead of flatMap or map.

Now, you don't have to implement foreach if you don't want to use the imperative form of "for", but foreach is trivial to implement since we already have map.



So far our monads have built on a few key concepts. These three methods - map, flatMap, and forEach - allow almost all of what "for" can do.


Scala's "for" statement has one more feature: "if" guards. As an example
"if" guards get mapped to a method called filter. Filter takes a predicate function (a function that takes on argument and returns true or false) and creates a new monad without the elements that don't match the predicate.


Not all monads can be filtered. Using the container analogy, filtering might remove all elements and some containers can't be empty. For such monads you don't need to create a filter method. Scala won't complain as long as you don't use an "if" guard in a "for" expression.




"for" is more general than that. It expands into map, flatMap, foreach, and filter. Of those, map and flatMap should be defined for any monad. The foreach method can be defined if you want the monad to be used imperatively and it's trivial to build. Filter can be defined for some monads but not for others.


"m map f" can be implemented as "m flatMap {x => unit(x)}. "m foreach f" can be implemented in terms of map, or in terms of flatMap "m flatMap {x => unit(f(x));()}. Even "m filter p" can be implemented using flatMap (I'll show how next time). flatMap really is the heart of the beast.


The Scala spec actually specifies that "for" expands using pattern matching. Basically, the real spec expands the rules I present here to allow patterns on the left side of the <-.
 */


val ns = List(1, 2)
val os = List(4, 5)
val ps = List(3, 4)
val qs =
  for (n <- ns; o <- os; p <- ps)
    yield n * o * p



val qs2 = ns flatMap { n =>
  os flatMap { o => {
    ps map { p => n * o * p }
  }
  }
}




for (n <- ns; o <- os) println(n * o)
ns foreach { n => os foreach { o => println(n * o) } }

class M[A](v: A) {
  def flatMap[B](f: A => M[B]): M[B] = ???


  /*
  foreach can just call map and throw away the results. That might not be the most runtime efficient way of doing things, though, so Scala allows you to define foreach your own way.
   */
  def foreach[B](f: A => B): Unit = {
    map(f)
    ()
  }

  def map[B](f: A => B): M[B] = ???
}
