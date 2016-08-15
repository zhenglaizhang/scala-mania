package net.zhenglai.dsl

import scala.annotation.tailrec

/**
  * Created by zhenglai on 8/15/16.
  */
object Factorial {

  def factorial(i: Int): Long = {

    @tailrec
    def fac(i: Int, accumulator: Long): Long = {
      if (i <= 1) accumulator
      else
        fac(i - 1, i * accumulator)
    }

    fac(i, 1)
  }

}
