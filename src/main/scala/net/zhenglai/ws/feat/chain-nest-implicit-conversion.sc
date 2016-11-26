/*
The enrich-my-library pattern allows one to seemingly add a method to a class by making available an implicit conversion from that class to one that implements the method.

Scala does not allow two such implicit conversions taking place, however, so one cannot got from A to C using an implicit A to B and another implicit B to C. Is there a way around this restriction?

Scala has a restriction on automatic conversions to add a method, which is that it won’t apply more than one conversion in trying to find methods.
 */

class A(val n: Int)

class B(val m: Int, val n: Int)

class C(val m: Int, val n: Int, val o: Int) {
  def total = m + n + o
}

import scala.language.implicitConversions

// This demonstrates implicit conversion chaining restrictions
object T1 {
  // to make it easy to test on REPL
  implicit def toA(n: Int): A = new A(n)

  implicit def aToB(a: A): B = new B(a.n, a.n)

  implicit def bToC(b: B): C = new C(b.m, b.n, b.m + b.n)

  // won't work
  //  println(5.total)
  //  println(new A(5).total)

  // works
  println(new B(5, 5).total)
  println(new C(5, 5, 10).total)
}

T1

/*
However, if an implicit definition requires an implicit parameter itself, Scala will look for additional implicit values for as long as needed.
 */

object T2 {
  implicit def toA(n: Int): A = new A(n)

  implicit def aToB[A1](a: A1)(implicit f: A1 => A): B =
    new B(a.n, a.n)

  implicit def bToC[B1](b: B1)(implicit f: B1 => B): C =
    new C(b.m, b.n, b.m + b.n)

  // works
  println(5.total)
  println(new A(5).total)
  println(new B(5, 5).total)
  println(new C(5, 5, 10).total)
}

T2

object T1Translated {
  implicit def toA(n: Int): A = new A(n)

  implicit def aToB(a: A): B = new B(a.n, a.n)

  implicit def bToC(b: B): C = new C(b.m, b.n, b.m + b.n)

  // Scala won't do this
  println(bToC(aToB(toA(5))).total)
  println(bToC(aToB(new A(5))).total)

  // Just this
  println(bToC(new B(5, 5)).total)

  // No implicits required
  println(new C(5, 5, 10).total)
}

object T2Translated {
  implicit def toA(n: Int): A = new A(n)

  implicit def aToB[A1](a: A1)(implicit f: A1 => A): B =
    new B(a.n, a.n)

  implicit def bToC[B1](b: B1)(implicit f: B1 => B): C =
    new C(b.m, b.n, b.m + b.n)

  // Scala does this
  println(bToC(5)(x => aToB(x)(y => toA(y))).total)
  println(bToC(new A(5))(x => aToB(x)(identity)).total)
  println(bToC(new B(5, 5))(identity).total)

  // no implicits required
  println(new C(5, 5, 10).total)
}

/*
So, while bToC is being used as an implicit conversion, aToB and toA are being passed as implicit parameters, instead of being chained as implicit conversions.
 */

class T {
  val t = "T"
}

class U
class V

object T {
  /*
  A view bound was a mechanism introduced in Scala to enable the use of some type A as if it were some type B. The typical syntax is this:

  def f[A <% B](a: A) = a.bMethod


  In other words, A should have an implicit conversion to B available, so that one can call B methods on an object of type A. The most common usage of view bounds in the standard library (before Scala 2.8.0, anyway), is with Ordered, like this:

def f[A <% Ordered[A]](a: A, b: A) = if (a < b) a else b
Because one can convert A into an Ordered[A], and because Ordered[A] defines the method <(other: A): Boolean, I can use the expression a < b.

   */
  implicit def UToT[UU <% U](u: UU) = new T
}

object U {
  implicit def VToU[VV <% V](v: VV) = new U
}

object V {
  implicit def StringToV(s: String) = new V
}

object Test {
  import T._
  import U._
  import V._

  def run = {
    "good": T

    // compiles to:
    // (T.UToT[java.lang.String]("")({
    // ((v: java.lang.String) => U.VToU[java.lang.String](v)({
    // ((s: String) => V.StringToV(s))
    // }))
    // }): T)

    "good".t // this also triggers the conversion.
  }

}

Test.run

/*
Context bounds were introduced in Scala 2.8.0, and are typically used with the so-called type class pattern, a pattern of code that emulates the functionality provided by Haskell type classes, though in a more verbose manner.

While a view bound can be used with simple types (for example, A <% String), a context bound requires a parameterized type, such as Ordered[A] above, but unlike String.

A context bound describes an implicit value, instead of view bound’s implicit conversion. It is used to declare that for some type A, there is an implicit value of type B[A] available. The syntax goes like this:

def f[A : B](a: A) = g(a) // where g requires an implicit value of type B[A]
This is more confusing than the view bound because it is not immediately clear how to use it. The common example of usage in Scala is this:

def f[A : ClassManifest](n: Int) = new Array[A](n)

An Array initialization on a parameterized type requires a ClassManifest to be available, for arcane reasons related to type erasure and the non-erasure nature of arrays.

Another very common example in the library is a bit more complex:
 */

def f[A: ClassManifest](n: Int) = new Array[A](n)

def g[A: Ordering](a: A, b: A) = implicitly[Ordering[A]].compare(a, b)

/*
Here, implicitly is used to retrive the implicit value we want, one of type Ordering[A], which class defines the method compare(a: A, b: A): Int.
*/

/*
It shouldn’t be surprising that both view bounds and context bounds are implemented with implicit parameters, given their definition. Actually, the syntax I showed are syntactic sugars for what really happens.


def f[A <% B](a: A) = a.bMethod
def f[A](a: A)(implicit ev: A => B) = a.bMethod



def g[A : B](a: A) = h(a)
def g[A](a: A)(implicit ev: B[A]) = h(a)


So, naturally, one can write them in their full syntax, which is specially useful for context bounds:
 */

def h[A](a: A, b: A)(implicit ev: Ordering[A]) = ev.compare(a, b)

/*
What are View Bounds used for?
View bounds are used mostly to take advantage of the enrich/pimp my library pattern, through which one “adds” methods to an existing class, in situations where you want to return the original type somehow. If you do not need to return that type in any way, then you do not need a view bound.

The classic example of view bound usage is handling Ordered. Note that Int is not Ordered, for example, though there is an implicit conversion. The example previously given needs a view bound because it returns the non-converted type:

def f[A <% Ordered[A]](a: A, b: A): A = if (a < b) a else b
This example won’t work without view bounds. However, if I were to return another type, then I don’t need a view bound anymore:

def f[A](a: Ordered[A], b: A): Boolean = a < b
The conversion here (if needed) happens before I pass the parameter to f, so f doesn’t need to know about it.

Besides Ordered, the most common usage from the library is handling String and Array, which are Java classes, like they were Scala collections. For example:

def f[CC <% Traversable[_]](a: CC, b: CC): CC = if (a.size < b.size) a else b
If one tried to do this without view bounds, the return type of a String would be a WrappedString (Scala 2.8), and similarly for Array.

The same thing happens even if the type is only used as a type parameter of the return type:

def f[A <% Ordered[A]](xs: A*): Seq[A] = xs.toSeq.sorted
What are Context Bounds used for?
Context bounds are mainly used in what has become known as typeclass pattern, as a reference to Haskell’s type classes. Basically, this pattern implements an alternative to inheritance by making functionality available through a sort of implicit adapter pattern.

The classic example is Scala 2.8’s Ordering, which replaced Ordered throughout Scala’s library. The usage is:

def f[A : Ordering](a: A, b: A) = if (implicitly[Ordering[A]].lt(a, b)) a else b
Though you’ll usually see that written like this:

def f[A](a: A, b: A)(implicit ord: Ordering[A]) = {
    import ord._
    if (a < b) a else b
}
Which take advantage of some implicit conversions inside Ordering that enable the traditional operator style. Another example in Scala 2.8 is the Numeric:

def f[A : Numeric](a: A, b: A) = implicitly[Numeric[A]].plus(a, b)
A more complex example is the new collection usage of CanBuildFrom, but there’s already a very long answer about that, so I’ll avoid it here. And, as mentioned before, there’s the ClassManifest usage, which is required to initialize new arrays without concrete types.

The context bound with the typeclass pattern is much more likely to be used by your own classes, as they enable separation of concerns, whereas view bounds can be avoided in your own code by good design (it is used mostly to get around someone else’s design).

Though it has been possible for a long time, the use of context bounds has really taken off in 2010, and is now found to some degree in most of Scala’s most important libraries and frameworks. The most extreme example of its usage, though, is the Scalaz library, which brings a lot of the power of Haskell to Scala. I recommend reading up on typeclass patterns to get more acquainted it all the ways in which it can be used.

Related questions of interest:

A discussion on types, origin and precedence of implicits
Chaining implicits



It'd be better if the context bound section can indicates further the ad hoc polymorphism is realizable with type classes, though you've stated something like "an alternative to inheritance"

I know view bounds may be deprecated soon
 */

/*
Well looks like most of the view bounds (<%) can be converted to context bounds. Lets see different strategies that can help us do the same. Suppose we have:
 */

def foo[T <% Int](x: T) = x

// compiler try best and initialize a[Int]
implicit def a[T](n: T) = n match {
  case x: String => x.toInt
}

foo("123")

type L[X] = X => Int

def goo[T: L](x: T): Int = x
goo("23")

// We could combine the type declaration with the function definition as:
def goo1[T: ({ type L[X] = X => Int })#L](x: T): Int = x

def goo2[T](x: T)(implicit ev: T => Int): Int = x

goo2("123")

/*More generalized version

scala> def goo[E,T : ({type L[X] = X => E})#L](x: T):E = x
goo: [E, T](x: T)(implicit evidence$1: T => E)E

scala> def goo2[T, E](x: T)(implicit ev: T => E):E = x
goo2: [T, E](x: T)(implicit ev: T => E)E

scala> goo("1000")
res10: String = 1000

scala> goo2("1000")
res11: String = 1000
 */ 