/*

Scalaz provides =>
New datatypes (Validation, NonEmptyList, etc)
Extensions to standard classes (OptionOps, ListOps, etc)
Implementation of every single general functions you need (ad-hoc polymorphism, traits + implicits)

* Validation
* NonEmptyList
* Dlist
* More


Extensions to standard classes
* OptionW
* ListW
* etc...
*


Implementation of EVERY single general function you need
* Ad-hoc polymorphism
* Traits + Implicit Parameters + Implicit conversions




Ad-hoc polymorphism

    Polymorphism => def head[T](xs: List[T]): T = ???  |  any T will be fine

Ad-hoc polymorphism => def show[T](t: T): String = ??? | Restrictions => Not all T's can be strings


Typically achieved through inheritance
def show[T <: Show[T]](t: T) = ???


in Scalaz, we use Traits  BUT no inheritance
Make it nice <=> Implicit parameters and implicit conversions


Typeclasses in Scalaz
* Monoid -> append, zero
* FoldLeft
* Functor -> map
* Monad -> bind,return
* Many more ...

Pimps of Scalaz
* Identity -> A
* MA -> M[A]
* MAB -> M[A, B]
* More as necessary -> One per kind


object Scalaz extends ... { ... }
* Include all implicit conversions
  * Pimps and Extensions
* General constructors
 * some(x), none
*/

import scalaz._
import Scalaz._
List(1, 2) |+| List(2, 4)