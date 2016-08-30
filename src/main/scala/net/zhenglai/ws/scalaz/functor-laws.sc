/*
Functor Laws

All functors are expected to exhibit certain kinds of functor-like properties and behaviors. … The first functor law states that if we map the id function over a functor, the functor that we get back should be the same as the original functor.

The second law says that composing two functions and then mapping the resulting function over a functor should be the same as first mapping one function over the functor and then mapping the other one.
 */
import net.zhenglai.lib.{COption, CSome}

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


(CSome(0, "ho"): COption[String]) map {(_: String) + "ha"}
(CSome(0, "ho"): COption[String]) map { identity }



/*
Here are the laws for Applicative:

  trait ApplicativeLaw extends FunctorLaw {
    def identityAp[A](fa: F[A])(implicit FA: Equal[F[A]]): Boolean =
      FA.equal(ap(fa)(point((a: A) => a)), fa)

    def composition[A, B, C](fbc: F[B => C], fab: F[A => B], fa: F[A])(implicit FC: Equal[F[C]]) =
      FC.equal(ap(ap(fa)(fab))(fbc), ap(fa)(ap(fab)(ap(fbc)(point((bc: B => C) => (ab: A => B) => bc compose ab)))))

    def homomorphism[A, B](ab: A => B, a: A)(implicit FB: Equal[F[B]]): Boolean =
      FB.equal(ap(point(a))(point(ab)), point(ab(a)))

    def interchange[A, B](f: F[A => B], a: A)(implicit FB: Equal[F[B]]): Boolean =
      FB.equal(ap(point(a))(f), ap(f)(point((f: A => B) => f(a))))
  }
 */





/*
Semigroup Laws

Here are the Semigroup Laws:

  /**
   * A semigroup in type F must satisfy two laws:
    *
    *  - '''closure''': `∀ a, b in F, append(a, b)` is also in `F`. This is enforced by the type system.
    *  - '''associativity''': `∀ a, b, c` in `F`, the equation `append(append(a, b), c) = append(a, append(b , c))` holds.
   */
  trait SemigroupLaw {
    def associative(f1: F, f2: F, f3: F)(implicit F: Equal[F]): Boolean =
      F.equal(append(f1, append(f2, f3)), append(append(f1, f2), f3))
  }
Remember, 1 * (2 * 3) and (1 * 2) * 3 must hold, which is called associative.
 */

// scala> semigroup.laws[Int @@ Tags.Multiplication].check()





/*
Monoid Laws

Here are the Monoid Laws:

  /**
   * Monoid instances must satisfy [[scalaz.Semigroup.SemigroupLaw]] and 2 additional laws:
   *
   *  - '''left identity''': `forall a. append(zero, a) == a`
   *  - '''right identity''' : `forall a. append(a, zero) == a`
   */
  trait MonoidLaw extends SemigroupLaw {
    def leftIdentity(a: F)(implicit F: Equal[F]) = F.equal(a, append(zero, a))
    def rightIdentity(a: F)(implicit F: Equal[F]) = F.equal(a, append(a, zero))
  }


This law is simple. I can |+| (mappend) identity value to either left hand side or right hand side. For multiplication:

scala> 1 * 2 assert_=== 2

scala> 2 * 1 assert_=== 2
 */

1 * 2 assert_=== 2
2 * 1 assert_=== 2

/*
import Tags._
scala> (Monoid[Int @@ Tags.Multiplication].zero |+| Tags.Multiplication(2): Int) assert_=== 2

scala> (Tags.Multiplication(2) |+| Monoid[Int @@ Tags.Multiplication].zero: Int) assert_=== 2



scala> monoid.laws[Int].check()
+ monoid.semigroup.associative: OK, passed 100 tests.
+ monoid.left identity: OK, passed 100 tests.
+ monoid.right identity: OK, passed 100 tests.


scala> monoid.laws[Int @@ Tags.Multiplication].check()
+ monoid.semigroup.associative: OK, passed 100 tests.
+ monoid.left identity: OK, passed 100 tests.
+ monoid.right identity: OK, passed 100 tests.
*/