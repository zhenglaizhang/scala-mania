/*
If F has a functor instance available, it's possible to lift any function A => B to F[A] => F[B].

If F has an applicative functor instance available, it's possible to lift any function A => B => C => .. => Z to F[A] => F[B] => F[C] => .. => F[Z]. Essentially, applicative functor is a generalization of functor for arbitrary arity.


// PartialFunction

Remember a PartialFunction[A, B] is a function defined for some subset of the domain A (as specified by the isDefinedAt method). You can "lift" a PartialFunction[A, B] into a Function[A, Option[B]]. That is, a function defined over the whole of A but whose values are of type Option[B]

any collection that extends PartialFunction[Int, A] (as pointed out by oxbow_lakes) may be lifted;
 */

val pf: PartialFunction[Int, Boolean] = {
  case k if k < 0 => true
}

val lifted = pf.lift
lifted(1)
lifted(-1)

val wow = Seq(1, 2, 3).lift
// which turns a partial function into a total function where values not defined in the collection are mapped onto None,
wow(0) // index!!
wow(30)

Seq(1,2,3).lift(22).getOrElse(-1)
// This shows a neat approach to avoid index out of bounds exceptions.


/*
//Methods

You can "lift" a method invocation into a function. This is called eta-expansion (thanks to Ben James for this).

We lift a method into a function by applying the underscore
*/
def times2(x: Int) = x * 2

// f is an instance (i.e. it is a value) of the (function) type (Int => Int)
val f = times2 _
f(4)

/*
//Functors

A functor (as defined by scalaz) is some "container" (I use the term extremely loosely), F such that, if we have an F[A] and a function A => B, then we can get our hands on an F[B] (think, for example, F = List and the map method)
 */

// TODO http://stackoverflow.com/questions/17965059/what-is-lifting-in-scala

