package net.zhenglai.dsl

/**
  * Created by zhenglai on 8/15/16.
  */
object Salt {


  def sum_count(seq: Seq[Int]) = seq.sum -> seq.size


  val (sum, count) = sum_count(List(1, 2, 3, 4, 5))

}
