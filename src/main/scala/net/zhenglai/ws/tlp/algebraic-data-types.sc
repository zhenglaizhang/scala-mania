
/*
An algebraic datatype reflects the idea, that a type is – loosely spoken – a set of certain (data) values.

an algebraic datatype can be defined by giving an enumeration of all the individual data values for a given type

let’s redefine that type as an algebraic datatype and call’em Bool. First of all, the single values of an algebraic datatype are only data – no behaviour at all – maybe that’s why they called datatypes

data Bool = False | True

Algebraic Datatypes – Enumerated Types

After the equals sign starts the enumeration of all values. In fact they are called value constructors because they’ll construct the individual values of the given type. Bool is a nice example of a so called sum type, because it can be characterized as the sum of all individual and unique values, which we just enumerated.

In Scala, you can define an equal algebraic datatype by using case classes.
where we’ll going to destruct values of a given datatype, using pattern matching (in fact, the idea of pattern matching is deeply connected to the idea of algebraic datatypes)

What do you mean by "algebraic?"
Let's do some counting.
  How many values of type Nothing?    => 0
  How many values of type Unit?       => 1
  How many values of type Boolean?    => 2
  How many values of type Byte?       => 256
  How many values of type String?

  How many of type (Byte, Boolean)? → 2 × 256 = 512
  How many of type (Byte, Unit)? → 256 × 1 = 256
  How many of type (Byte, Byte)? → 256 × 256 = 65536
  How many of type (Byte, Boolean, Boolean)? → 256 × 2 × 2 = 1024
  How many of type (Boolean, String, Nothing)? → 2 × many × 0 = 0


 */

// Product types! This and That
type Person = (String, Int)
case class Person2(name: String, age: Int)

/*
  How many of type Byte or Boolean? → 2 + 256 = 258
  How many of type Boolean or Unit? → 2 + 1 = 3
  How many of type (Byte, Boolean) or Boolean? → (256 × 2) + 2 = 514
  How many of type Boolean or (String, Nothing)? → 2 + (many × 0) = 2


Nothing = 0
Unit    = 1
Boolean = 2
Byte    = 256

(Unit , Boolean) = 1 × 2 = 2
 Unit | Boolean  = 1 + 2 = 3 // not Scala syntax

(Byte , Boolean) = 256 × 2 = 512
 Byte | Boolean  = 256 + 2 = 258

(Boolean, Boolean, Boolean, Boolean,
 Boolean, Boolean, Boolean, Boolean) = 256

// sums all the way down
Boolean = true | false
Int = 1 | 2 | 3 | ...
 */

// Sum types => Encoded by Subclassing
sealed trait Pet
case class Color()
case class Cat(name: String) extends Pet
case class Fish(name: String, color: Color) extends Pet
case class Squid(name: String, age: Int) extends Pet

val bob: Pet = Cat("Bob")

// Pro tip: in Scala when you hear ADT it means sum type.
// Destructured by pattern matching
// To use the new ADT, it's common in Scala to define recursive functions that employ the match keyword to deconstruct it.
// Exhaustiveness Checking
//    <console>:14: warning: match may not be exhaustive.

def sayHi(p: Pet): String = p match {
  case Cat(n)      => s"Meow $n!"
  case Fish(n, _)  => s"Hello fishy $n!"
  case Squid(n, _) => s"Hi $n"
}

sayHi(Cat("cat"))
sayHi(Squid("squid", 12))

object hide {
  // Intuition: computations that may fail to return a value
  sealed trait Option[+A]
  case object None extends Option[Nothing]
  case class Some[A](a: A) extends Option[A]

  // Intuition: computations that may return this or that
  sealed trait Either[+A, +B]
  case class Left[A](a: A) extends Either[A, Nothing]
  case class Right[B](b: B) extends Either[Nothing, B]

  // Intuition: computations that may fail with an exception
  sealed trait Try[+A]
  case class Success[A](a: A) extends Try[A]
  case class Failure[A](t: Throwable) extends Try[A]

  // Intuition: computations that may return many answers
  sealed trait List[+A]
  case object Nil extends List[Nothing]
  case class ::[A](head: A, tail: List[A]) extends List[A]

  object List {
    def apply[A](as: A*): List[A] = ???
  }
}

def saveDiv(a: Int, b: Int): Option[Int] =
  if (b != 0) Some(a / b) else None

def safeDiv(a: Int, b: Int): Either[String, Int] =
  if (b != 0) Right(a / b) else Left("Divide by zero!")

saveDiv(10, 2)
saveDiv(10, 0)
safeDiv(10, 0).left.get
safeDiv(10, 2).right.get

import util._ // it's scala.util.Try

def safeDiv2(a: Int, b: Int): Try[Int] =
  Try(a / b)

/*
Because Scala is a hybrid OO – functional Language, it doesn’t support algebraic datatypes in a direct way (like in Haskell)

Class Bool is only for introducing the type, not its values. All single values of that type are then defined by a single case object for each of them.
 */
sealed abstract class Bool
case object True extends Bool
case object False extends Bool

val not: Bool => Bool = b => if (b eq True) False else True
val and: (Bool, Bool) => Bool = (a: Bool, b: Bool) => if (a eq False) False else b
val or: (Bool, Bool) => Bool = (a: Bool, b: Bool) => if (a eq True) True else b

println(and(or(True, not(True)), not(False)))

