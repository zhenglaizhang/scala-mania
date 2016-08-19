/*
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