
/*
The level of abstraction is such that you can typically express an idea in code more easily and clearly than you can by making diagrams on a whiteboard.
 */

object H1 {

  val b = new Building

  class Building

}

object H2 {

  val b = new Building(100)

  class Building(feet: Int) {
    val squareFeet = feet
  }

  println(b.squareFeet)
}

H2

object H3 {

  val b = Building(100)
  val h = new House(10000)

  println(b)

  /*
  Scala has the case class which does even more for you. For one thing, arguments automatically become fields, without saying val before them

Note the new is no longer necessary to create an object, the same form that Python uses. And case classes rewrite toString for you, to produce nice output.

   A case class automatically gets an appropriate hashcode and == so you can use it in a Map

  */
  case class Building(feet: Int)

  class House(feet: Int) extends Building(feet)
  println(h.feet)
}

H3

object H4 {

  /*
  We can also mix in behavior using traits.
   */
  trait Bathroom

  trait Kitchen

  case class Building(feet: Int)

  trait Bedroom {
    def occupants() = {
      1
    }
  }

  /*
  You can talk about what it's doing, rather than explaining meaningless syntactic requirements as you must do in Java.
   */
  class House(feet: Int) extends Building(feet) with Bathroom with Kitchen with Bedroom

  val h = new House(999)
  /*
  Notice that the method occupants() is now part of House, via the mixin effect of traits.
   */
  val o = h.occupants()
  val feet = h.feet
  o
  feet
}

H4.o
H4.feet
