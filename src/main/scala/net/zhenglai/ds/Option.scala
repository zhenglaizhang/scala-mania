package net.zhenglai.ds

sealed trait Option[+A] {

  /*
上面的[B >: A]是指类型B是类型A的父类，结合+A变形，Option[B]就是Option[A]的父类：如果A是Apple，那么B可以是Fruit,那么上面的默认值类型就可以是Fruit,或者是Option[Fruit]了。=> B表示输入参数B是拖延计算的，意思是在函数内部真正参考（refrence）这个参数时才会对它进行计算。
   */
  def getOrElse[B >: A](default: => B): B = this match {
    case None    => default
    case Some(x) => x
  }

  def map[B](f: A => B): Option[B] = this match {
    case None    => None
    case Some(x) => Some(f(x))
  }

  def flatMap[B](f: A => Option[B]): Option[B] = this match {
    case None    => None
    case Some(x) => f(x)
  }

  def filter(f: A => Boolean): Option[A] = this match {
    case Some(x) if f(x) => this
    case _               => None
  }

  def orElse[B >: A](ob: => Option[B]): Option[B] = this match {
    case None => ob
    case _    => this
  }
}


case class Some[A](value: A) extends Option[A]

case object None extends Option[Nothing]
