package net.zhenglai.lib

/*
基本typeclass主要的作用是通过操作符来保证类型安全，也就是在前期编译时就由compiler来发现错误。

我们来设计一个NoneZero typeclass。这个NoneZero typeclass能确定目标类型值是否为空，如：

0.nonZero = false

3.nonZero = true

"".nonZero = false

"value".nonZero = true

List().nonZero = false

List(1,2,3).nonZero = true
 */
trait NonZero[A] {

  // 抽象函数NonZero：对任何类型A值a，返回Boolean结果
  def nonZero(a: A): Boolean
}


object NonZero {
  /*
  为了方便使用NoneZero typeclass，我们在伴生对象里定义NonZero[A]的构建函数，这样我们就不需要每次都重新实现抽象行为函数nonZero了

  只要我们提供一个f: A => Boolean函数就能用create来构建一个NonZero[A]实例。实际上这个f函数定义了类型A在NonZero tyoeclass中的具体行为
  */
  def create[A](f: A => Boolean): NonZero[A] = new NonZero[A] {
    override def nonZero(a: A): Boolean = f(a)
  }


  /*
  我们按scalaz惯例在object NonZero放一个默认隐式转换：
  */

  implicit val IntNZInstance: NonZero[Int] = create { // partial function here
    case 0 => false
    case _ => true
  }

}

// 注入一个操作方法isNonZero。注意：注入方法是针对所有类型A的，所以需要NonZero[A]作为参数。
class NonZeroOps[A](a: A)(implicit ev: NonZero[A]) {
  def isNonZero: Boolean = ev.nonZero(a)
}


/*
跟着就是隐式作用域解析了（implicit resolution）：

这是一个隐式视域（implicit view）：从类型A转换到NonZeroOps[A]。这样类型A就具备isNonZero这个操作方法了。
 */
object ToNonZeroOps {
  implicit def toNonZeroOps[A](a: A)(implicit ev: NonZero[A]) = new NonZeroOps[A](a)
}

