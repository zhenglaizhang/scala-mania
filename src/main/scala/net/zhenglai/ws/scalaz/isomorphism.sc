import scalaz.IsomorphismsLow0

/*
Isomorphism (同构)

Definitions: An arrow f: A => B is called an isomorphism, or invertible arrow, if there is a map g: B => A, for which g ∘ f = 1A and f ∘ g = 1B. An arrow g related to f by satisfying these equations is called an inverse for f. Two objects A and B are said to be isomorphic if there is at least one isomorphism f: A => B.


The “identity arrow on A” is denoted as 1A. f(a) == a
 */

object h11 {
  sealed abstract class IsomorphismSig /*extends IsomorphismsLow0*/ {
    trait Iso[Arr[_, _], A, B] {
      self =>

      def to: Arr[A, B]
      def from: Arr[B, A]
    }

    // Set isomorphism
    type IsoSet[A, B] = Iso[Function1, A, B]

    // Alias for IsoSet
    type <=>[A, B] = IsoSet[A, B]
  }

  object Isomorphism extends IsomorphismSig
}

sealed trait Family
case object Mother extends Family
case object Father extends Family
case object Child extends Family


sealed trait Relic
case object Feather extends Relic
case object Stone extends Relic
case object Flower extends Relic

import scalaz._
import Scalaz._
import scalaz.Isomorphism._

val isoFamilyRelic = new (Family <=> Relic) {
  val to: Family => Relic = {
    case Mother => Feather
    case Father => Stone
    case Child => Flower
  }

  val from: Relic => Family = {
    case Feather => Mother
    case Stone => Father
    case Flower => Child
  }
}

/*
implicit val familyEqual = Equal.equalA[Family]
implicit val relicEqual = Equal.equalA[Relic]
implicit val arbFamily: Arbitrary[Family] = Arbitrary {
  Gen.oneOf(Mother, Father, Child)
}
implicit val arbRelic: Arbitrary[Relic] = Arbitrary {
  Gen.oneOf(Feather, Stone, Flower)
}

scala> arrowEqualsProp(isoFamilyRelic.from compose isoFamilyRelic.to, identity[Family] _)
res22: org.scalacheck.Prop = Prop

scala> res22.check
+ OK, passed 100 tests.

scala> arrowEqualsProp(isoFamilyRelic.to compose isoFamilyRelic.from, identity[Relic] _)
res24: org.scalacheck.Prop = Prop

scala> res24.check
+ OK, passed 100 tests.
 */




/*
It’s encouraging to see support for isomorphisms in Scalaz. Hopefully we are going the right direction.

Notation: If f: A => B has an inverse, then the (one and only) inverse for f is denoted by the symbol f-1 (read ’f-inverse’ or ‘the inverse of f‘.)

We can check if the above isoFamilyRelic satisfies the definition using arrowEqualsProp.

scala> :paste
// Entering paste mode (ctrl-D to finish)

implicit val familyEqual = Equal.equalA[Family]
implicit val relicEqual = Equal.equalA[Relic]
implicit val arbFamily: Arbitrary[Family] = Arbitrary {
  Gen.oneOf(Mother, Father, Child)
}
implicit val arbRelic: Arbitrary[Relic] = Arbitrary {
  Gen.oneOf(Feather, Stone, Flower)
}

// Exiting paste mode, now interpreting.

scala> arrowEqualsProp(isoFamilyRelic.from compose isoFamilyRelic.to, identity[Family] _)
res22: org.scalacheck.Prop = Prop

scala> res22.check
+ OK, passed 100 tests.

scala> arrowEqualsProp(isoFamilyRelic.to compose isoFamilyRelic.from, identity[Relic] _)
res24: org.scalacheck.Prop = Prop

scala> res24.check
+ OK, passed 100 tests.
 */

