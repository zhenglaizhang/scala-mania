package net.zhenglai.minia

package object dsl {

  // TODO: value class?
  implicit final class PipeOperator[A](val a: A) extends AnyVal {
    def |>[B](f: A => B): B = f(a)
  }


  def loopDo[A](i: IndexedSeqt)(f: => A) = (1 to i).foreach(_ => f)
}
