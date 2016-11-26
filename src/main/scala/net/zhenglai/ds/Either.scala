package net.zhenglai.ds

/*
 Convention dictates
 *  that Left is used for failure and Right is used for success.
 *
 *  For example, you could use `Either[String, Int]` to detect whether a
 *  received input is a String or an Int.
 */

// implementation here is not generic

// TODO http://www.cnblogs.com/tiger-xc/p/4338979.html
sealed trait Either[+E, +A] {
  def map[B](f: A => B): Either[E, B] = this match {
    case Right(x) => Right(f(x))
    case Left(e)  => Left(e)
  }

  def flatMap[EE >: E, B](f: A => Either[EE, B]): Either[EE, B] = this match {
    case Left(e)  => Left(e)
    case Right(x) => f(x)
  }

  def orElse[EE >: E, AA >: A](default: Either[EE, AA]): Either[EE, AA] = this match {
    case Left(_)  => default
    case Right(x) => Right(x)
  }

  //用递归算法
  def map2[EE >: E, B, C](b: Either[EE, B])(f: (A, B) => C): Either[EE, C] = (this, b) match {
    case (Left(e), _)         => Left(e)
    case (_, Left(e))         => Left(e)
    case (Right(a), Right(b)) => Right(f(a, b))
  }
  //用for comprehension
  def map2_1[EE >: E, B, C](b: Either[EE, B])(f: (A, B) => C): Either[EE, C] = {
    for {
      aa <- this
      bb <- b
    } yield f(aa, bb)
  }
  //用 flatMap写
  def map2_2[EE >: E, B, C](b: Either[EE, B])(f: (A, B) => C): Either[EE, C] = {
    flatMap(aa => b map (bb => f(aa, bb)))
  }
}

/*
Either需要处理两个类型E和A：E代表异常类型，A代表计算类型。与Option一样，Either也有两种状态：Left代表无法完成计算，返回值E是对异常情况的描述、Right则代表计算正常完成，返回计算结果A。从英文解释，Either不是Right就是Left。这种情况被称为类型的“不联合性”（disjoint union）。
 */
case class Left[+E](value: E) extends Either[E, Nothing]

case class Right[+A](value: A) extends Either[Nothing, A]
