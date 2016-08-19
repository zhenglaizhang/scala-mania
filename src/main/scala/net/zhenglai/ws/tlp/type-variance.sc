/*
Variance, in general, can be explained as "type compatible-ness" between types, forming an extends relation. The most popular cases where you’ll have to deal with this is when working with containers or functions (so… surprisingly often!).

A major difference from Java in Scala is, that container types are not-variant by default!

Variance in Scala is defined by using + and - signs in front of type parameters.


Name	        Description	                      Scala Syntax
Invariant     C[T'] and C[T] are not related    C[T]
Covariant     C[T'] is a subclass of C[T]       C[+T]
Contravariant C[T] is a subclass of C[T']       C[-T]


Most immutable collections are covariant, and most mutable collections are invariant.
 */

// scala.collection.immutable.List[+A], which is immutable as well as covariant,
// having immutable collections co-variant is safe
class Fruit
case class Apple() extends Fruit
case class Orange() extends Fruit

val l1: List[Apple] = Apple() :: Nil
val l2: List[Fruit] = Orange() :: l1

// and also, it's safe to prepend with "anything",
// as we're building a new list - not modifying the previous instance

val l3: List[AnyRef] = "" :: l2


// won't compile
// val a: Array[Any] = Array[Int](1, 2, 3)
// Array’s invariance


// Set[A] is invariant