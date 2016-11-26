/*
Types are little labels that values carry so that we can reason about the values. But types have their own little labels, called kinds. A kind is more or less the type of a type. … What are kinds and what are they good for? Well, let’s examine the kind of a type by using the :k command in GHCI.




scala> :k Int
scala.Int's kind is A

scala> :k -v Int
scala.Int's kind is A
*
This is a proper type.

scala> :k -v Option
scala.Option's kind is F[+A]
* -(+)-> *
This is a type constructor: a 1st-order-kinded type.

scala> :k -v Either
scala.util.Either's kind is F[+A1,+A2]
* -(+)-> * -(+)-> *
This is a type constructor: a 1st-order-kinded type.

scala> :k -v Equal
scalaz.Equal's kind is F[A]
* -> *
This is a type constructor: a 1st-order-kinded type.

scala> :k -v Functor
scalaz.Functor's kind is X[F[A]]
(* -> *) -> *
This is a type constructor that takes type constructor(s): a higher-kinded type.






From the top. Int and every other types that you can make a value out of is called a proper type and denoted with a symbol * (read “type”). This is analogous to value 1 at value-level. Using Scala’s type variable notation this could be written as A.



A first-order value, or a value constructor like (_: Int) + 3, is normally called a function. Similarly, a first-order-kinded type is a type that accepts other types to create a proper type. This is normally called a type constructor. Option, Either, and Equal are all first-order-kinded. To denote that these accept other types, we use curried notation like * -> * and * -> * -> *. Note, Option[Int] is *; Option is * -> *. Using Scala’s type variable notation they could be written as F[+A] and F[+A1,+A2].


A higher-order value like (f: Int => Int, list: List[Int]) => list map {f}, a function that accepts other functions is normally called higher-order function. Similarly, a higher-kinded type is a type constructor that accepts other type constructors. It probably should be called a higher-kinded type constructor but the name is not used. These are denoted as (* -> *) -> *. Using Scala’s type variable notation this could be written as X[F[A]].


In case of Scalaz 7.1, Equal and others have the kind F[A] while Functor and all its derivatives have the kind X[F[A]]. Scala encodes (or complects) the notion of type class using type constructor, and the terminology tend get jumbled up. For example, the data structure List forms a functor, in the sense that an instance Functor[List] can be derived for List. Since there should be only one instance for List, we can say that List is a functor. See the following discussion for more on “is-a”:


Since List is F[+A], it’s easy to remember that F relates to a functor. Except, the typeclass definition Functor needs to wrap F[A] around, so its kind is X[F[A]]. To add to the confusion, the fact that Scala can treat type constructor as a first class variable was novel enough, that the compiler calls first-order kinded type as “higher-kinded type”
 */

trait Test {
  type F[_]
}
/*
scala> trait Test {
         type F[_]
       }
<console>:14: warning: higher-kinded type should be enabled
by making the implicit value scala.language.higherKinds visible.
This can be achieved by adding the import clause 'import scala.language.higherKinds'
or by setting the compiler option -language:higherKinds.
See the Scala docs for value scala.language.higherKinds for a discussion
why the feature should be explicitly enabled.
         type F[_]
              ^
 */

import scalaz._
import Scalaz._

List(1, 2, 3).shows
List(1, 2, 3).show

/*
But if you want to use Show[A].shows, you have to know it’s Show[List[Int]], not Show[List]. Similarly, if you want to lift a function, you need to know that it’s Functor[F] (F is for Functor):

scala> Functor[List[Int]].lift((_: Int) + 2)
<console>:14: error: List[Int] takes no type parameters, expected: one
              Functor[List[Int]].lift((_: Int) + 2)
                      ^

scala> Functor[List].lift((_: Int) + 2)
res13: List[Int] => List[Int] = <function1>
In the cheat sheet I started I originally had type parameters for Equal written as Equal[F], which is the same as Scalaz 7’s source code. Adam Rosien pointed out to me that it should be Equal[A].
 */ 