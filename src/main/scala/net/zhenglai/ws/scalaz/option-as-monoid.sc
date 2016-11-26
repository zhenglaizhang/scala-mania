/*
One way is to treat Maybe a as a monoid only if its type parameter a is a monoid as well and then implement mappend in such a way that it uses the mappend operation of the values that are wrapped with Just.
 */

import scalaz.Scalaz._
import scalaz._

implicit def optionMonoid[A: Semigroup]: Monoid[Option[A]] = new Monoid[Option[A]] {

  override def zero: Option[A] = None

  override def append(f1: Option[A], f2: => Option[A]): Option[A] = (f1, f2) match {
    case (Some(a1), Some(a2)) => Some(Semigroup[A].append(a1, a2))
    case (Some(a1), None)     => f1
    case (None, Some(a2))     => f2
    case (None, None)         => None
  }
}

/*
Context bound A: Semigroup says that A must support |+|. The rest is pattern matching.
*/

(none: Option[String]) |+| "andy".some
(Ordering.LT: Ordering).some |+| none

/*
But if we don’t know if the contents are monoids, we can’t use mappend between them, so what are we to do? Well, one thing we can do is to just discard the second value and keep the first one. For this, the First a type exists.


Haskell is using newtype to implement First type constructor. Scalaz 7 does it using mightly Tagged type:
*/

Tags.First('a'.some) |+| Tags.First('b'.some)
Tags.First(none: Option[Char]) |+| Tags.First('b'.some)
Tags.First('a'.some) |+| Tags.First(none: Option[Char])

/*
If we want a monoid on Maybe a such that the second parameter is kept if both parameters of mappend are Just values, Data.Monoid provides a the Last a type.

This is Tags.Last:
 */

Tags.Last('a'.some) |+| Tags.Last('b'.some)
Tags.Last(none: Option[Char]) |+| Tags.Last('b'.some)
Tags.Last('a'.some) |+| Tags.Last(none: Option[Char])
