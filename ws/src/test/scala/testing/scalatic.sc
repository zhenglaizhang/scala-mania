
// custom equality
case class Person(name: String, age: Double)

/*
   Scalactic's === operator looks for an implicit Equality[L], where L is the left-hand type

   Because you didn't specifically provide an implicit Equality[Person], === will fall back on default equality, which will call Person's equals method. That equals method, provided by the Scala compiler because Person is a case class
  */
import org.scalactic._
import TripleEquals._
Person("Zhenglai", 29.0001) === Person("Zhenglai", 29.0)


// custom equality

import Tolerance._
implicit val personEq = new Equality[Person] {
  def areEqual(a: Person, b: Any): Boolean = b match {
    case p: Person =>
      a.name == p.name && p.age +- 0.0002 === a.age
    case _ => false
  }
}

Person("Zhenglai", 29.0001) === Person("Zhenglai", 29.0)


/*
You can obtain a default equality via the default method of the Equality companion object, or from the defaultEquality method defined in TripleEqualsSupport
 */





// Constrained equality
