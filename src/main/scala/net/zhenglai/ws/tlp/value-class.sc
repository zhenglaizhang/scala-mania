/*
In computer science, a value object is a small object that represents a simple entity whose equality is not based on identity: i.e. two value objects are equal when they have the same value, not necessarily being the same object.

Being small, one can have multiple copies of the same value object that represent the same entity: it is often simpler to create a new object rather than rely on a single instance and use references to it.[2]

Value objects should be immutable.

Value objects work better if they have native support for copy-by-value semantics, i.e. the expression

valueObject1 = valueObject2

assigns the value of the valueObject1 by creating a copy of the valueObject2, instead of assigning a reference to the second object, as happens in most object oriented languages for assignments among objects.

Java programmers therefore emulate value objects by creating immutable objects,[11] because if the state of an object does not change, passing references is semantically equivalent to copying value objects.

A class can be made immutable by declaring all attributes blank final,[12] and declaring all attributes to be of immutable type (such as String, Integer, or any other type declared in accordance with these rules), not of mutable type such an ArrayList or even a Date. They should also define equals and hashCode to compare values rather than references.

The term "VALJO" (VALue Java Object) has been coined to refer to the stricter set of rules necessary for a correctly defined immutable value object



----

all Number’s in Scala use this compiler trick to avoid boxing and unboxing numeric values from int to scala.Int etc.


As a quick reminder, let’s recall that Array[Int] is an actual JVM int[] (or for bytecode happy people, it’s the JVM runtime type called: [I) which has tons of performance implications, but in one word — arrays of numbers are fast, arrays of references not as much.
 */

//  a Meter which will serve as wrapper for plain Int
case class Meter(value: Double) extends AnyVal {
  def toFeet: Foot = Foot(value * 0.3048)
}

case class Foot(value: Double) extends AnyVal {
  def toMeter: Meter = Meter(value / 0.3048)
}