/*
Case Classes are one of the most useful nifty little compiler tricks available in Scala. While not being very complicated, they help a lot with otherwise very tedious and boring things such as equals, hashCode and toString implementations, and also preparing apply / unapply methods in order to be used with pattern matching, and more.
 */

case class Circle(radius: Double)

/*
By defining a case class we automatically get these benefits:

  instances of it are immutable,

  can be compared using equals, and equality is defined by it’s fields (NOT object identity like it would be the case with a normal class),

  it’s hashcode adheres the equals contract, and is based on the values of the class,

  the radius constructor parameter is a public val

  it’s toString is composed of the class name and values of the fields it contains (for our Circle it would be implemented as def toString = s"Circle($radius)").
 */

case class Point(x: Int, y: Int)
val a = Point(0, 0)
// a.toString == "Point(0,0)"

val b = a.copy(y = 10)
// b.toString == "Point(0,10)"

// equality of case classes is value based (equals and hashCode implementations based on the case class parameters are generated)
a == Point(0, 0)


// usual syntax
Circle(2.5) match {
  case Circle(r) => println("Radius = " + r)
}

// extractor syntax
val Circle(r) = Circle(2.4)
val r2 = r + r