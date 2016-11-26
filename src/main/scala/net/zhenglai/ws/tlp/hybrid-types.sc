

/*
‘sum of products type’.

algebraic datatypes, which doesn’t feature subtype polymorphism, but only combination (you may wanna think composition) of datatypes
 */

sealed abstract class Shape

// product type
final case class Circle(radius: Double) extends Shape

// product type
final case class Rectangle(width: Double, height: Double)

/*
sum pf product types
Ok, the range of possible different (read disjointed) sort of shapes is given by the sum of all existing value constructors, that is Circle plus Rectangle.
 */

val rect = Rectangle(width = 20.0F, height = 10.0F)

/*
Type cosmetics (type synonyms/alias)

You can introduce a synonym for an existing type at any time, in order to give a type a more descriptive name.

We could introduce some naming conventions and point to the importance of picking meaningful names!
 */

type Width = Double
type Height = Double

case class Rectangle2(width: Width, height: Height) extends Shape

/*
Pay attention

we also saw some obstacles while the compiler only checks for the underlying, masked type.

the compiler won’t complain, since it only checks the underlying type Double, which is ok for both constructor parameters (no matter the type synonyms they exhibit).
 */
val bad = Rectangle2(10.0F: Height, 20.0F: Width)

// nested type composition
case class Point(x: Int, y: Int)

case class Circle2(center: Point, radius: Double) extends Shape

