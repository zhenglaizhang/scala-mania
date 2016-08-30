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