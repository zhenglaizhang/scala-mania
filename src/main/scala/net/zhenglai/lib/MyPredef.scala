package net.zhenglai.lib

/**
  * Created by zhenglai on 8/17/16.
  */
object MyPredef {


  implicit def value2Tuple1[A](x: A): Tuple1[A] = Tuple1(x)


  class Parent(val value: Int) {
    override def toString: String = {
      s"${this.getClass.getName}($value)"
    }
  }

  class Child(value: Int) extends Parent(value)

  case class Opt[A](value: A = null) {
    def getOrElse(default: A) = if (value != null) value else default
  }



  class Upper
  class Middle1 extends Upper
  class Middle2 extends Middle1
  class Lower extends Middle2
  case class C[A >: Lower <: Upper](a: A)
//  case class C2[A <: Upper >: Lower](a: A) // Does not compile
}
