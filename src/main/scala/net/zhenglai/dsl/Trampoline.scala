package net.zhenglai.dsl

import scala.util.control.TailCalls._

object Trampoline {

  def isOdd(xs: List[Int]): TailRec[Boolean] =
    if (xs.isEmpty) done(false) else tailcall(isEven(xs.tail))

  def isEven(xs: List[Int]): TailRec[Boolean] =
    if (xs.isEmpty) done(true) else tailcall(isOdd(xs.tail))

  for (i <- 1 to 5) {
    val even = Trampoline.isEven((1 to i).toList).result
    println(s"$i is even?\t$even")
  }

}
