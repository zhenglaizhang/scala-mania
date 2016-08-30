import scalaz._
import Scalaz._

// Compile with -optimize to eliminate boxing/unboxing in specialized
// tag methods.

class User
class Checkin


/*
The really clever thing here is that this whole setup is just at the type-level: the values are just Int in the underlying byte code.

That is to say if we had something like:

def foobar(i: Int): Int @@ Multiplication = ...
When compiled it would actually end up being:

def foobar(i: Int): Int = …
Which is pretty awesome. This sort of strategy is obviously quite generic and there are a range of different tags within Scalaz7, including selecting first and last operands of a monoid, zipping with applicatives, conjunction and disjunctions etc. All very neat stuff!
 */

object h11 {
  type Tagged[U] = { type Tag = U }
  type @@[T, U] = T with Tagged[U] // Thanks to @retronym for suggesting this type alias

  class Tagger[U] {
    def apply[T](t : T) : T @@ U = t.asInstanceOf[T @@ U]
  }
  def tag[U] = new Tagger[U]

  // Manual specialization needed here ... specializing apply above doesn't help
  def tag[U](i : Int) : Int @@ U = i.asInstanceOf[Int @@ U]
  def tag[U](l : Long) : Long @@ U = l.asInstanceOf[Long @@ U]
  def tag[U](d : Double) : Double @@ U = d.asInstanceOf[Double @@ U]

  def fetch[A](id: Int @@ A): A = null.asInstanceOf[A]

  //def main(args: Array[String]): Unit = {
  val id = tag[Checkin](10)

  fetch[Checkin](id) // Compiles
  //  fetch[User](id)    // Does not compile

  val ids = tag[User](1) :: tag[User](2) :: tag[User](3) :: Nil

  val users : List[(Int @@ User)] = ids       // Compiles
  //  val checkins : List[Int @@ Checkin] = ids   // Does not compile
  //}
}










/*
case class KiloGram(value: Double)

Although it does adds type safety, it’s not fun to use because we have to call x.value every time we need to extract the value out of it. Tagged type to the rescue.
 */


/*
scala> sealed trait KiloGram
defined trait KiloGram

scala> def KiloGram[A](a: A): A @@ KiloGram = Tag[A, KiloGram](a)
KiloGram: [A](a: A)scalaz.@@[A,KiloGram]

scala> val mass = KiloGram(20.0)
mass: scalaz.@@[Double,KiloGram] = 20.0

scala> 2 * Tag.unwrap(mass) // this doesn't work on REPL
res2: Double = 40.0

scala> 2 * Tag.unwrap(mass)
<console>:17: error: wrong number of type parameters for method unwrap$mDc$sp: [T](a: Object{type Tag = T; type Self = Double})Double
              2 * Tag.unwrap(mass)
                      ^

scala> 2 * scalaz.Tag.unsubst[Double, Id, KiloGram](mass)
res2: Double = 40.0
 */



/*
As of scalaz 7.1 we need to explicitly unwrap tags. Previously we could just do 2 * mass. Due to a problem on REPL SI-8871, Tag.unwrap doesn’t work, so I had to use Tag.unsubst. Just to be clear, A @@ KiloGram is an infix notation of scalaz.@@[A, KiloGram]. We can now define a function that calculates relativistic energy.



scala> sealed trait JoulePerKiloGram
defined trait JoulePerKiloGram

scala> def JoulePerKiloGram[A](a: A): A @@ JoulePerKiloGram = Tag[A, JoulePerKiloGram](a)
JoulePerKiloGram: [A](a: A)scalaz.@@[A,JoulePerKiloGram]

scala> def energyR(m: Double @@ KiloGram): Double @@ JoulePerKiloGram =
         JoulePerKiloGram(299792458.0 * 299792458.0 * Tag.unsubst[Double, Id, KiloGram](m))
energyR: (m: scalaz.@@[Double,KiloGram])scalaz.@@[Double,JoulePerKiloGram]

scala> energyR(mass)
res4: scalaz.@@[Double,JoulePerKiloGram] = 1.79751035747363533E18

scala> energyR(10.0)
<console>:18: error: type mismatch;
 found   : Double(10.0)
 required: scalaz.@@[Double,KiloGram]
    (which expands to)  AnyRef{type Tag = KiloGram; type Self = Double}
              energyR(10.0)
                      ^
As you can see, passing in plain Double to energyR fails at compile-time. This sounds exactly like newtype except it’s even better because we can define Int @@ KiloGram if we want.
 */



/*
One of the really neat things that Scalaz7 makes use of is a very clever trick with types in order to disambiguate typeclass instances for a given type T.
 */

Monoid[Int].append(1, {2})
1 |+| 2

/*
This is simple use of the Monoid type, and this example uses the default type class instance for Int, which simply sums two numbers together. But, consider another case for Int: what if you needed to multiply those same numbers instead?

If there were two typeclass instances for Monoid[Int], unless they were in separate objects, packages or jars there would be an implicit ambiguity which would not compile: the type system would not implciitly be able to determine which typeclass it should apply when considering an operation on monoids of Int.


This issue has been overcome in Scalaz7 by using the type tagging trick mentioned in the introduction. Specifically, the default behaviour for Monoid[Int] is addition, and then a second typeclass exists for Monoid[Int @@ Multiplication]
 */

import Tags._

Multiplication(2) |+| Multiplication(10)
//  resulting type signature of @@[Int,Multiplication]

/*
Multiplication just acts as a small bit of plumbing to produce a type of A, tagged with Multiplication; and this gives you the ability to define a type which is distinct from another - even if they are "the same" (so to speak). The definition of Multiplication

sealed trait Multiplication
def Multiplication[A](a: A): A @@ Multiplication = Tag[A, Multiplication](a)

object Tag {
  @inline def apply[A, T](a: A): A @@ T = a.asInstanceOf[A @@ T]
  ...
}


The Tag object simply tags types of A with a specified marker of T; or, in this case, Multiplication, where the result is A @@ T
*/
