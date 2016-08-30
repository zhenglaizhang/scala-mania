/*
Functor Laws

All functors are expected to exhibit certain kinds of functor-like properties and behaviors. … The first functor law states that if we map the id function over a functor, the functor that we get back should be the same as the original functor.

The second law says that composing two functions and then mapping the resulting function over a functor should be the same as first mapping one function over the functor and then mapping the other one.
 */
import scalaz._
import Scalaz._

val seq = List(1, 2, 3)

seq map (identity) assert_=== seq

(seq map {{(_: Int) * 3} map {(_: Int) + 1}}) assert_=== (seq map {(_: Int) * 3} map {(_:Int) + 1})


/*
These are laws the implementer of the functors must abide, and not something the compiler can check for you. Scalaz 7+ ships with FunctorLaw traits that describes this in code:


trait FunctorLaw {
  /** The identity function, lifted, is a no-op. */
  def identity[A](fa: F[A])(implicit FA: Equal[F[A]]): Boolean = FA.equal(map(fa)(x => x), fa)

  /**
   * A series of maps may be freely rewritten as a single map on a
   * composed function.
   */
  def associative[A, B, C](fa: F[A], f1: A => B, f2: B => C)(implicit FC: Equal[F[C]]): Boolean = FC.equal(map(map(fa)(f1))(f2), map(fa)(f2 compose f1))
}




> sbt test:console
# Here’s how you test if List meets the functor laws:
scala> functor.laws[List].check()
+ functor.invariantFunctor.identity: OK, passed 100 tests.
+ functor.invariantFunctor.composite: OK, passed 100 tests.
+ functor.identity: OK, passed 100 tests.
+ functor.composite: OK, passed 100 tests.
 */