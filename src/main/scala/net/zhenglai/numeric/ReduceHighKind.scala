package net.zhenglai.numeric

/**
 * Created by zhenglai on 8/17/16.
 */
object ReduceHighKind {

  //  def main(args: Array[String]): Unit = {
  //    sum(Vector(1 -> 10, 2 -> 20, 3 -> 30)) // Result: (6,60)
  //    sum(1 to 10) // Result: 55
  //    sum(Option(2)) // Result: 2
  //    //    sum[Int, Option](None)
  //  }
  //
  //  def sum[T: Add, M[T]](container: M[T])(//
  //                                         implicit red: Reduce[T, M]): T =
  //    red.reduce(container)(implicitly[Add[T]].add(_, _))
  //
  //  trait Reduce[T, -M[T]] {
  //    //
  //    def reduce(m: M[T])(f: (T, T) => T): T
  //  }
  //
  //  object Reduce {
  //    //
  //    implicit def seqReduce[T] = new Reduce[T, Seq] {
  //      def reduce(seq: Seq[T])(f: (T, T) => T): T = seq reduce f
  //    }
  //
  //    implicit def optionReduce[T] = new Reduce[T, Option] {
  //      def reduce(opt: Option[T])(f: (T, T) => T): T = opt reduce f
  //    }
  //  }

}
