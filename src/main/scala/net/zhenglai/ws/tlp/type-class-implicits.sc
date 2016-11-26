/*

A typeclass is a sort of interface that defines some behavior. If a type is a part of a typeclass, that means that it supports and implements the behavior the typeclass describes.

It provides purely functional data structures to complement those from the Scala standard library. It defines a set of foundational type classes (e.g. Functor, Monad) and corresponding instances for a large number of data structures.


Implicits:
  implicit parameters
  implicit conversions
 */

// Implicit Parameters

/*
we declare a function that takes an implicit parameter and if there is a unique valid implicit instance available in the scope, the function will take it automatically and we won’t need to pass it explicitly


context =>
a typical example is the ExecutionContext that we need when we use Future, Future.map for instance is defined in this way: def map[S](f: T => S)(implicit executor: ExecutionContext): Future[S]
 */

implicit val value = 3
// implicits parameters are resolved also when we add type parameters:
// the compiler is able to resolve implicits even when there are type parameters
// type inference => T is Int => got Printer[Int] in scope
val res = foo(3)

foo1
/*
the function foo now takes two types parameters,T and R, but only T is extracted from the parameter t that we pass, R is instead computed from the implicit resolution, and then we can use it as return type
 */
val res4 = foo2(3)
val res5 = foo2("ciao")

def foo1(implicit i: Int) = println(i)

foo(10000)
foo("hello")

def foo[T](t: T)(implicit p: Printer[T]) = p.print(t)

// for instance foo(true) won’t compile because there isn’t an instance of Printer[Boolean] available in scope
//val error = foo(false)

def foo2[T, R](t: T)(implicit r: Resolver[T, R]): R = r.resolve(t)

/*
Type Classes

use type parameters and implicits together we can give to this a name, this technique is called Type Classes
it is the way Polymorphism is implemented in Haskell

an abstract type that takes one type parameter
trait Printer[T] { ... } and different implicit implementations in the companion object
 */
trait Printer[T] {
  def print(t: T): String
}

// the resolution works even if not all the type parameters are known
trait Resolver[T, R] {
  def resolve(t: T): R
}

object Printer {
  implicit val ip: Printer[Int] = new Printer[Int] {
    override def print(t: Int): String = t.toString
  }

  implicit val sp: Printer[String] = new Printer[String] {
    override def print(t: String): String = t
  }
}
object Resolver {
  implicit val ib: Resolver[Int, Boolean] = new Resolver[Int, Boolean] {
    override def resolve(t: Int): Boolean = t > 1
  }

  implicit val sd: Resolver[String, Double] = new Resolver[String, Double] {
    override def resolve(t: String): Double = t.length.toDouble
  }
}

/*
Multi-step Resolution

Another important aspect about implicits is that the resolution doesn’t stop at the first level, we can have implicits that are generated taking implicit parameters themselves, and this can go on until the compiler finds a stable implicit value,
this is a very good way to kill the compiler!

to do this there is compile time overhead but also runtime, because we’ll have to instantiate a Printer instance per cycle
 */

object Holder {

  val res = print(Option(List(1, 3, 6)))

  def print[T](t: T)(implicit p: Printer[T]) = p.print(t)

  trait Printer[T] {
    def print(t: T): String
  }

  object Printer {
    implicit val intPrinter: Printer[Int] = new Printer[Int] {
      def print(i: Int) = s"$i: Int"
    }

    implicit def optionPrinter[V](implicit pv: Printer[V]): Printer[Option[V]] =
      new Printer[Option[V]] {
        def print(ov: Option[V]) = ov match {
          case None    => "None"
          case Some(v) => s"Option[${pv.print(v)}]"
        }
      }

    implicit def listPrinter[V](implicit pv: Printer[V]): Printer[List[V]] =
      new Printer[List[V]] {
        def print(ov: List[V]) = ov match {
          case Nil        => "Nil"
          case l: List[V] => s"List[${l.map(pv.print).mkString(", ")}]"
        }
      }
  }
  println(s"res: ${res}")

  // res: Option[List[1: Int, 3: Int, 6: Int]]
}

object raw {
  object Statistics {
    def median(xs: Vector[Double]): Double = xs(xs.size / 2)
    def quartiles(xs: Vector[Double]): (Double, Double, Double) =
      (xs(xs.size / 4), median(xs), xs(xs.size / 4 * 3))
    def iqr(xs: Vector[Double]): Double = quartiles(xs) match {
      case (lowerQuartile, _, upperQuartile) => upperQuartile - lowerQuartile
    }
    def mean(xs: Vector[Double]): Double = {
      xs.reduce(_ + _) / xs.size
    }
  }

  /*
  Now, of course, we want to support more than just double numbers. So let’s implement all these methods again for Int numbers, right?

  Also, in situations such as these, we quickly run into situations where we cannot overload a method without some dirty tricks, because the type parameter suffers from type erasure.
   */

}

object adpaterPattern {
  /*
   Users of our library can pass in a NumberLike adapter for Int (which we would likely provide ourselves) or for any possible type that might behave like a number, without having to recompile the module in which our statistics methods are implemented.

    always wrapping your numbers in an adapter is not only tiresome to write and read, it also means that you have to create a lot of instances of your adapter classes when interacting with our library.
   */
  object Statistics {
    trait NumberLike[A] {
      def get: A
      def plus(y: NumberLike[A]): NumberLike[A]
      def minus(y: NumberLike[A]): NumberLike[A]
      def divide(y: Int): NumberLike[A]
    }
    case class NumberLikeDouble(x: Double) extends NumberLike[Double] {
      def get: Double = x
      def minus(y: NumberLike[Double]) = NumberLikeDouble(x - y.get)
      def plus(y: NumberLike[Double]) = NumberLikeDouble(x + y.get)
      def divide(y: Int) = NumberLikeDouble(x / y)
    }
    type Quartile[A] = (NumberLike[A], NumberLike[A], NumberLike[A])
    def median[A](xs: Vector[NumberLike[A]]): NumberLike[A] = xs(xs.size / 2)
    def quartiles[A](xs: Vector[NumberLike[A]]): Quartile[A] =
      (xs(xs.size / 4), median(xs), xs(xs.size / 4 * 3))
    def iqr[A](xs: Vector[NumberLike[A]]): NumberLike[A] = quartiles(xs) match {
      case (lowerQuartile, _, upperQuartile) => upperQuartile.minus(lowerQuartile)
    }
    def mean[A](xs: Vector[NumberLike[A]]): NumberLike[A] =
      xs.reduce(_.plus(_)).divide(xs.size)
  }
}

/*
Type classes, one of the prominent features of the Haskell language, despite their name, haven’t got anything to do with classes in object-oriented programming.

A type class C defines some behaviour in the form of operations that must be supported by a type T for it to be a member of type class C. Whether the type T is a member of the type class C is not inherent in the type. Rather, any developer can declare that a type is a member of a type class simply by providing implementations of the operations the type must support. Now, once T is made a member of the type class C, functions that have constrained one or more of their parameters to be members of C can be called with arguments of type T.

As such, type classes allow ad-hoc and retroactive polymorphism. Code that relies on type classes is open to extension without the need to create adapter objects.

type classes – a pattern that allows you to design your programs to be open for extension without giving up important information about concrete types.
 */

object Math {
  import annotation.implicitNotFound
  // Creating a type class
  @implicitNotFound("No member of type class NumberLike in scope for ${T}")
  trait NumberLike[T] {
    def plus(x: T, y: T): T
    def divide(x: T, y: Int): T
    def minus(x: T, y: T): T
  }
  /*
  We have created a type class called NumberLike. Type classes always take one or more type parameters, and they are usually designed to be stateless, i.e. the methods defined on our NumberLike trait operate only on the passed in arguments. In particular, where our adapter above operated on its member of type T and one argument, the methods defined for our NumberLike type class take two parameters of type T each – the member has become the first parameter of the operations supported by NumberLike.
   */

  // Providing default implementation

  /*
  The second step in implementing a type class is usually to provide some default implementations of your type class trait in its companion object
   */

  object NumberLike {
    implicit object NumberLikeDouble extends NumberLike[Double] {
      def plus(x: Double, y: Double): Double = x + y
      def divide(x: Double, y: Int): Double = x / y
      def minus(x: Double, y: Double): Double = x - y
    }
    implicit object NumberLikeInt extends NumberLike[Int] {
      def plus(x: Int, y: Int): Int = x + y
      def divide(x: Int, y: Int): Int = x / y
      def minus(x: Int, y: Int): Int = x - y
    }
  }

  /*
  members of type classes are usually singleton objects. Also, please note the implicit keyword before each of the type class implementations. This is one of the crucial elements for making type classes possible in Scala, making type class members implicitly available under certain conditions.
   */

  // Coding against type classes
  object Statistics {

    /*
    The idea to constrain a parameter to types that are members of a specific type class is realized by means of the implicit second parameter list. What does this mean? Basically, that a value of type NumberLike[T] must be implicitly available in the current scope. This is the case if an implicit value has been declared and made available in the current scope, very often by importing the package or object in which that implicit value is defined.

    If and only if no other implicit value can be found, the compiler will look in the companion object of the type of the implicit parameter. Hence, as a library designer, putting your default type class implementations in the companion object of your type class trait means that users of your library can easily override these implementations with their own ones, which is exactly what you want. Users can also pass in an explicit value for an implicit parameter to override the implicit values that are in scope.
     */
    def mean[T](xs: Vector[T])(implicit ev: NumberLike[T]): T =
      ev.divide(xs.reduce(ev.plus(_, _)), xs.size)
  }
}

import Math._

import scala.annotation.implicitNotFound
val numbers = Vector[Double](13, 23.0, 42, 45, 61, 73, 96, 100, 199, 420, 900, 3839)
println(Statistics.mean(numbers))

/*
If we try this with a Vector[String], we get an error at compile time, stating that no implicit value could be found for parameter ev: NumberLike[String]. If you don’t like this error message, you can customize it by annotating your type class trait with the @implicitNotFound annotation
 */

/*
Error:(214, 17) No member of type class NumberLike in scope for String
Statistics.mean(Vector("Hello"));}
 */
//Statistics.mean(Vector("Hello"))

/*
A second, implicit parameter list on all methods that expect a member of a type class can be a little verbose. As a shortcut for implicit parameters with only one type parameter, Scala provides so-called context bounds.


A context bound T : NumberLike means that an implicit value of type NumberLike[T] must be available, and so is really equivalent to having a second implicit parameter list with a NumberLike[T] in it. If you want to access that implicitly available value, however, you need to call the implicitly method, as we do in the iqr method. If your type class requires more than one type parameter, you cannot use the context bound syntax.
 */
object Statistics2 {
  import Math.NumberLike
  def mean[T](xs: Vector[T])(implicit ev: NumberLike[T]): T =
    ev.divide(xs.reduce(ev.plus(_, _)), xs.size)
  def median[T: NumberLike](xs: Vector[T]): T = xs(xs.size / 2)
  def quartiles[T: NumberLike](xs: Vector[T]): (T, T, T) =
    (xs(xs.size / 4), median(xs), xs(xs.size / 4 * 3))
  def iqr[T: NumberLike](xs: Vector[T]): T = quartiles(xs) match {
    case (lowerQuartile, _, upperQuartile) =>
      implicitly[NumberLike[T]].minus(upperQuartile, lowerQuartile)
  }
}

object JodaImplicits {

  import org.joda.time.Duration

  implicit object NumberLikeDuration extends NumberLike[Duration] {
    def plus(x: Duration, y: Duration): Duration = x.plus(y)

    def divide(x: Duration, y: Int): Duration = Duration.millis(x.getMillis / y)

    def minus(x: Duration, y: Duration): Duration = x.minus(y)
  }

}

//import JodaImplicits._
//import Math.Statistics._
//import org.joda.time.Duration._
//
//val durations = Vector(standardSeconds(20), standardSeconds(57), standardMinutes(2),
//  standardMinutes(17), standardMinutes(30), standardMinutes(58), standardHours(2),
//  standardHours(5), standardHours(8), standardHours(17), standardDays(1),
//  standardDays(4))
//println(mean(durations).getStandardHours)

case class Song(name: String, artist: String)
case class Address(street: String, number: Int)

@implicitNotFound(s"No member of type class LabelMarker exists in scope for {T}")
trait LabelMarker[T] {
  def output(t: T): String
}

object LabelMarker {
  implicit object addressLabelMarker extends LabelMarker[Address] {
    override def output(address: Address): String = {
      s"${address.number} ${address.street} street"
    }
  }

  implicit object songLabelMarker extends LabelMarker[Song] {
    override def output(song: Song): String = {
      s"${song.artist} - ${song.name}"
    }
  }

  def label[T: LabelMarker](t: T) = implicitly[LabelMarker[T]].output(t)
}

import LabelMarker._
val l1 = label(Song("Hey ya", "Outkast"))
val l2 = label(Address("Something", 273))

/*
Typeclasses capture the notion of retroactive extensibility. With static method overloads, you have to define them all at once in one place, but with typeclasses you can define new instances anytime you want for any new types in any modules.
 */

object SomeModule {
  case class Car(title: String)
  implicit object carLabelMaker extends LabelMarker[Car] {
    override def output(car: Car): String = {
      s"Great car ${car.title}"
    }
  }
}
import SomeModule._
val void = println(label(Car("Mustang")))
//val void2 = println(label(12))

/*
Once class is written, to make it inherited from some class or trait, you have to touch its definition, which may be unavailable to you (e.g. it's class from some library). With typeclasses you don't have to touch class definition to write instance for that class. That's one of aspects of mentioned retroactive extensibility. And no, rules for T type parameter are just the same as for regular generic classes.
 */ 