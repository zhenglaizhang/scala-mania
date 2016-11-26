

trait Abstract {
  println("In Abstract")
  lazy val inverse = 1.0 / value
  //  val inverse = 1.0 / value
  //  val value: Int = 10000
  val value: Int
  println(s"In Abstract inverse = $inverse")
}

val obj = new Abstract {
  println("In obj")
  override val value: Int = 10
}

println(obj.inverse)

// pre-initialization fields

val obj2 = new {
  override val value = 20
} with Abstract

obj2.inverse

class Concrete extends Abstract {
  override val value: Int = 40
}

(new Concrete).inverse