package net.zhenglai.minia.dsl

package object pipe {

  // TODO: value class?
  implicit final class PipeOperator[A](val a: A) extends AnyVal {
    def |>[B](f: A => B): B = f(a)
  }

}
