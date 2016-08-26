package net.zhenglai.lib

/**
  * Created by zhenglai on 8/17/16.
  */
object MyPredef {


  type IntMaker = () => Int
  // type alias: () => Int

  implicit def value2Tuple1[A](x: A): Tuple1[A] = Tuple1(x)

  /*
  I would suggest aiming for clarity first and then worrying about efficiency only when you have concrete evidence that that's an issue in your program.
   */
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

  // non-strict
  /*
  如果valTrue和valFalse都是几千行代码的大型复杂计算，那么non-strict特性会节省大量的计算资源，提高系统运行效率。除此之外，non-strict特性是实现无限数据流（Infinite Stream）的基本要求
   */
  def If[A](cond: Boolean, trueVal: => A, falseVal: => A): A = {
    // cond is strict
    if (cond) {
      println("run trueVal")
      trueVal
    }
    else {
      println("run falseVal")
      falseVal
    }
  }

  class Parent(val value: Int) {
    override
    def toString: String = {
      s"${this.getClass.getName}($value)"
    }
  }

  class Child(value: Int) extends Parent(value)

  //  case class C2[A <: Upper >: Lower](a: A) // Does not compile

  case
  class Opt[A](value: A = null) {
    def getOrElse(default: A) = if (value != null) value else default
  }

  class Upper

  class Middle1 extends Upper

  class Middle2 extends Middle1

  class Lower extends Middle2

  case class C[A >: Lower <: Upper](a: A)

}
