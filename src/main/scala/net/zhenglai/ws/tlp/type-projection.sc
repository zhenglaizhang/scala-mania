
/*
Type Projections are similar to Path Dependent Types in the way that they allow you to refer to a type of an inner class.


difference between these path dependent types (the "." syntax) vs. type projections (the "#" syntax)
 */

class Outer {
  class Inner

  def g(inner: Inner) = println("Got my Inner")
  def f(inner: Outer#Inner) = println("Got a Inner")
}

// Type Projection (and alias) refering to Inner
type OuterInnerProjection = Outer#Inner

val out1 = new Outer
val out2 = new Outer

/*
When you declare a class inside another class in Scala, you are saying that each instance of that class has such a subclass. In other words, there's no A.B class, but there are a1.B and a2.B classes, and they are different classes
 */
val out1in = new out1.Inner

//out2.g(out1in)  // error
out1.g(out1in)
out2.f(out1in)

trait R {
  type A = Int
}

val x = null.asInstanceOf[R#A]

null.asInstanceOf[Double]
null.asInstanceOf[Boolean]
null.asInstanceOf[List[Int]]
null.asInstanceOf[Some[Int]]

/*
# is called a type projection, and used to refer to inner types. I'm not solid in the terminology so would not go into details, but as a guideline you use A#B when performing type-level operations (see also type lambdas), while A.B is related to path-dependent types.

Note: When A is a package or object, then A.B behaves as you would expect, the # fun comes into play with traits or classes.
 */

