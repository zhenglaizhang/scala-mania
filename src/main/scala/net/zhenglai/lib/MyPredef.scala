package net.zhenglai.lib

/**
  * Created by zhenglai on 8/17/16.
  */
object MyPredef {


  type IntMaker = () => Int
  // type alias: () => Int

  implicit def value2Tuple1[A](x: A): Tuple1[A] = Tuple1(x)

  def repeatChar(char: Char)(n: Int) = List.fill(n)(char).mkString

  def repeatString(str: String)(n: Int) = List.fill(n)(str).mkString

  def repeatStringFast(str: String)(n: Int) = str * n

  def repeatStringFast2(str: String)(n: Int) = {
    val buf = new StringBuilder

    for (i <- 0 until n) {
      buf append str
    }

    buf.toString
  }

  class Parent(val value: Int) {
    override
    def toString: String = {
      s"${this.getClass.getName}($value)"
    }
  }

  class Child(value: Int) extends Parent(value)

  case
  class Opt[A](value: A = null) {
    def getOrElse(default: A) = if (value != null) value else default
  }

  //  case class C2[A <: Upper >: Lower](a: A) // Does not compile

  class Upper

  class Middle1 extends Upper

  class Middle2 extends Middle1

  class Lower extends Middle2

  case class C[A >: Lower <: Upper](a: A)
}
