/*
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
          case None => "None"
          case Some(v) => s"Option[${pv.print(v)}]"
        }
      }

    implicit def listPrinter[V](implicit pv: Printer[V]): Printer[List[V]] =
      new Printer[List[V]] {
        def print(ov: List[V]) = ov match {
          case Nil => "Nil"
          case l: List[V] => s"List[${l.map(pv.print).mkString(", ")}]"
        }
      }
  }
  println(s"res: ${res}")

  // res: Option[List[1: Int, 3: Int, 6: Int]]
}

