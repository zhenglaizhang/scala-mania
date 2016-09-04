/*
It’s no secret that some of the fundamentals of Scalaz and Haskell like Monoid and Functor comes from category theory. Let’s try studying category theory and see if we can use the knowledge to further our understanding of Scalaz.



Sets, arrows, composition

CM:

Before giving a precise definition of ‘category’, we should become familiar with one example, the category of finite sets and maps. An object in this category is a finite set or collection. … You are probably familiar with some notations for finite sets:

{ John, Mary, Sam }

There are two ways that I can think of to express this in Scala. One is by using a value a: Set[Person]:
 */


sealed trait Person {}
case object John extends Person {}
case object Mary extends Person {}
case object Sam extends Person {}

val a: Set[Person] = Set[Person](John, Mary, Sam)



/*
Another way of looking at it, is that Person as the type is a finite set already without Set. Note: In CM, Lawvere and Schanuel use the term “map”, but I’m going to change to arrow like Mac Lane and other books.

A arrow f in this cateogry consists of three things:

  a set A, called the domain of the arrow,
  a set B, called the codomain of the arrow,
  a rule assigning to each element a in the domain, an element b in the codomain. This b is denoted by f ∘ a (or sometimes ’f(a)‘), read ’f of a‘.

(Other words for arrow are ‘function’, ‘transformation’, ‘operator’, ‘map’, and ‘morphism’(态射, 同态).)
 */

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

/*
 an “object” in this category is Set[Person] or Person, but the “arrow” favoriteBreakfast accepts a value whose type is Person.


The important thing is: For each dot in the domain, we have exactly one arrow leaving, and the arrow arrives at some dot in the codomain.


I get that a map can be more general than Function1[A, B] but it’s ok for this category




An arrow in which the domain and codomain are the same object is called an endomorphism(自同态). Take the below as one example
*/

val favoritePerson: Person => Person = {
  case John => Mary
  case Mary => John
  case Sam  => Mary
}


/*
An arrow, in which the domain and codomain are the same set A, and for each of a in A, f(a) = a, is called an identity arrow.

The “identity arrow on A” is denoted as 1A.

Again, identity is an arrow, so it works on an element in the set, not the set itself. So in this case we can just use scala.Predef.identity.
 */
identity(John)



/*
in the category of finite sets, the “objects” translate to types like Person and Breakfast, and arrows translate to functions like Person => Person. The external diagram looks a lot like the type-level signatures like Person => Person.

The final basic ingredient, which is what lends all the dynamics to the notion of category is composition of arrows, by which two arrows are combined to obtain a third arrow.
 */