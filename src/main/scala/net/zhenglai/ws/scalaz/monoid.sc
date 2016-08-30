/*
It seems that both * together with 1 and ++ along with [] share some common properties: - The function takes two parameters. - The parameters and the returned value have the same type. - There exists such a value that doesn’t change other values when used with the binary function.
 */

import scalaz._
import Scalaz._

4 * 1
1 * 9

List(1, 2, 3, 4) ++ Nil
Nil ++ List(1, 2, 3)


/*
It doesn’t matter if we do (3 * 4) * 5 or 3 * (4 * 5). Either way, the result is 60. The same goes for ++. … We call this property associativity. * is associative, and so is ++, but -, for example, is not.
 */

(3 * 2) * (8 * 5) assert_=== 3 * (2 * (8 * 5))




/*
A monoid is when you have an associative binary function and a value which acts as an identity with respect to that function.



trait Monoid[A] extends Semigroup[A] { self =>
  ////
  /** The identity element for `append`. */
  def zero: A

  ...
}


trait Semigroup[A]  { self =>
  def append(a1: A, a2: => A): A
  ...
}



// It introduces mappend operator with symbolic alias |+| and ⊹.
trait SemigroupOps[A] extends Ops[A] {
  final def |+|(other: => A): A = A.append(self, other)
  final def mappend(other: => A): A = A.append(self, other)
  final def ⊹(other: => A): A = A.append(self, other)
}
/*
We have mappend, which, as you’ve probably guessed, is the binary function. It takes two values of the same type and returns a value of that type as well.
*/

LYAHFGG also warns that just because it’s named mappend it does not mean it’s appending something, like in the case of *. Let’s try using this.
*/

List(1, 2, 3) mappend List(4, 5, 6)
"one" mappend "two"


// I think the idiomatic Scalaz way is to use |+|:
1 |+| 2



/*
zero represents the identity value for a particular monoid.
 */

Monoid[List[Int]].zero
Monoid[String].zero



/*
Tags.Multiplication

LYAHFGG:

So now that there are two equally valid ways for numbers (addition and multiplication) to be monoids, which way do choose? Well, we don’t have to.


This is where Scalaz 7.1 uses tagged type. The built-in tags are Tags. There are 8 tags for Monoids and 1 named Zip for Applicative. (Is this the Zip List I couldn’t find yesterday?)


/** Type tag to choose a [[scalaz.Monoid]] instance for a numeric type that performs multiplication,
   *  rather than the default monoid for these types which by convention performs addition. */
  sealed trait Multiplication

  val Multiplication = Tag.of[Multiplication]
 */

Tags.Multiplication(10) |+| Monoid[Int @@ Tags.Multiplication].zero


/*
Nice! So we can multiply numbers using |+|. For addition, we use plain Int.
 */

10 |+| Monoid[Int].zero





/*
Another type which can act like a monoid in two distinct but equally valid ways is Bool. The first way is to have the or function || act as the binary function along with False as the identity value. … The other way for Bool to be an instance of Monoid is to kind of do the opposite: have && be the binary function and then make True the identity value.



In Scalaz 7 these are called Boolean @@ Tags.Disjunction and Boolean @@ Tags.Conjunction respectively.
 */

Tags.Disjunction(true) |+| Tags.Disjunction(false)
Monoid[Boolean @@ Tags.Disjunction].zero |+| Tags.Disjunction(true)
Monoid[Boolean @@ Tags.Disjunction].zero |+| Monoid[Boolean @@ Tags.Disjunction].zero
Monoid[Boolean @@ Tags.Conjunction].zero |+| Tags.Conjunction(true)
Monoid[Boolean @@ Tags.Conjunction].zero |+| Tags.Conjunction(false)