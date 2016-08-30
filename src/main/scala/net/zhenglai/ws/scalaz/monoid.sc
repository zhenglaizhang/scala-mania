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