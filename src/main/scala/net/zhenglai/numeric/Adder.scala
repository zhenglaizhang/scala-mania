package net.zhenglai.numeric

/**
  * Created by zhenglai on 8/17/16.
  */

object Add {
  implicit val addInt = new Add[Int] {
    override def add(t1: Int, t2: Int): Int = t1 + t2
  }

  implicit val addIntIntPair = new Add[(Int, Int)] {
    override def add(t1: (Int, Int), t2: (Int, Int)): (Int, Int) = {
      (t1._1 + t2._1, t1._2 + t2._2)
    }
  }
}

trait Add[T] {
  def add(t1: T, t2: T): T
}

object Adder {

  import Add._


  def main(args: Array[String]): Unit = {
    println(sumSeq(Vector(1 -> 10, 2 -> 20, 3 -> 30)))
    println(sumSeq(1 to 10))
    //    println(sumSeq(Option(2)))
  }

  def sumSeq[T: Add](seq: Seq[T]): T = seq reduce (implicitly[Add[T]].add(_, _))

}
