/*
One very useful sort of set is a ‘singleton’ set, a set with exactly one element. Fix one of these, say {me}, and call this set ’1‘.

Definition: A point of a set X is an arrows 1 => X. … (If A is some familiar set, an arrow from A to X is called an ’A-element’ of X; thus ’1-elements’ are points.) Since a point is an arrow, we can compose it with another arrow, and get a point again.


 it seems like CM is redefining the concept of the element as a special case of arrow. Another name for singleton is unit set, and in Scala it is (): Unit. So it’s analogous to saying that values are sugar for Unit => X
 */

sealed trait Person {}
case object John extends Person {}
case object Mary extends Person {}
case object Sam extends Person {}

sealed trait Breakfast {}
case object Eggs extends Breakfast {}
case object Oatmeal extends Breakfast {}
case object Toast extends Breakfast {}
case object Coffee extends Breakfast {}

val favoriteBreakfast: Person => Breakfast = {
  case John => Eggs
  case Mary => Coffee
  case Sam  => Coffee
}

val johnPoint: Unit => Person = { case () => John }


johnPoint()
johnPoint(())

val johnsFavouriteBreakfast = favoriteBreakfast compose johnPoint
johnsFavouriteBreakfast()
johnsFavouriteBreakfast(())


/*
First-class functions in programming languages that support fp treat functions as values, which allows higher-order functions. Category theory unifies on the other direction by treating values as functions.
 */