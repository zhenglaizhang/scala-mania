

trait Cool {
  def c = ""
}

trait Awesome {
  def a = ""
}

class Base {
  def b = ""
}

class BA extends Base with Awesome

class BC extends Base with Cool

// as you might expect, you can upcast these instances into any of the traits they've mixed-in
val ba: BA = new BA
val bc: Base with Cool = new BC
val bc2: Cool = new BC

val b1: Base = ba
val b2: Base = bc

ba.a
bc.c
b1.b


// diamond problem

trait A { def common = "A" }

trait B extends A { override def common = "B" }
trait C extends A { override def common = "C" }

class D1 extends B with C
// D1 -> C -> A -> B -> A
// D1 -> C -> B -> A -> AnyRef -> Any
//  the superclass of C is B

class D2 extends C with B
// D1 -> B -> C -> A -> AnyRef -> Any

(new D1).common == "C"
(new D2).common == "B"
