package net.zhenglai.lib

//// 我们用scala的特质（trait）描述了Monoid。它就是一个抽象的数据类型。
//trait Monoid[A] { //被封装的类型A
//val zero: A  //恒等值identity
//
//  def op(a1: A, a2: A): A //二元函数
//}

trait Monoid[A] {
  def mappend(a1: A, a2: A): A

  def mzero: A
}

object Monoid {
  implicit val IntMonoid: Monoid[Int] = new Monoid[Int] {
    def mappend(a: Int, b: Int): Int = a + b

    def mzero: Int = 0
  }
  implicit val StringMonoid: Monoid[String] = new Monoid[String] {
    def mappend(a: String, b: String): String = a + b

    def mzero: String = ""
  }
}

object Test {
  def sum[A: Monoid](xs: List[A]): A = {
    val m = implicitly[Monoid[A]]
    xs.foldLeft(m.mzero)(m.mappend)
  }

  //  You can still provide different monoid directly to the function. We could provide an instance of monoid for Int using multiplications.
  val multiMonoid: Monoid[Int] = new Monoid[Int] {
    override def mappend(a1: Int, a2: Int): Int = a1 * a2
    override def mzero: Int = 1
  }

}

