package net.zhenglai.lib

// 我们用scala的特质（trait）描述了Monoid。它就是一个抽象的数据类型。
trait Monoid[A] { //被封装的类型A
val zero: A  //恒等值identity

  def op(a1: A, a2: A): A //二元函数
}
