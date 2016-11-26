package net.zhenglai.scalatic

import org.scalatest.{ FunSuite, Matchers, MustMatchers }
import org.scalactic._
import org.scalactic.TripleEquals._
import org.scalactic.Tolerance._

class ScalaticTest extends FunSuite with Matchers {

  test("default equality") {
    Array(1, 2, 3) eq Array(1, 2, 3) shouldEqual false // identity
    //    Array(1, 2, 3) == Array(1, 2, 3) shouldEqual false // not obviously correct
    Array(1, 2, 3) sameElements Array(1, 2, 3) shouldEqual true
    Array(1, 2, 3).deep == Array(1, 2, 3).deep shouldEqual true // might be expensive
    Array(1, 2, 3) === Array(1, 2, 3) shouldEqual true
    //    Array(1, 2, 3) == List(1, 2, 3) shouldEqual false
    Array(1, 2, 3) === List(1, 2, 3) shouldEqual true
    //    List(1, 2, 3) == Array(1, 2, 3) shouldEqual false
    List(1, 2, 3) === Array(1, 2, 3) shouldEqual true
  }

  /*
Scalactic's === operator looks for an implicit Equality[L], where L is the left-hand type: in this case, Person. Because you didn't specifically provide an implicit Equality[Person], === will fall back on default equality, which will call Person's equals method. That equals method, provided by the Scala compiler because Person is a case class
   */
  case class Person(name: String, age: Double)
  test("custom equality") {

    Person("Zzl", 29.0001) === Person("Zzl", 29.0)

    /*
    To make the equality check more forgiving, you could define an implicit Equality[Person] that compares the age Doubles with a tolerance,
     */

    implicit val personEq = new Equality[Person] {
      override def areEqual(a: Person, b: Any): Boolean = a match {
        case p: Person =>
          a.name == p.name && (p.age +- 0.0002) === a.age
        case _ =>
          false
      }
    }

    Person("Zzl", 29.0001) === Person("Zzl", 29.0)
  }
}
