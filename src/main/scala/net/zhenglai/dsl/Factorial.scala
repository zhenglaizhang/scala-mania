package net.zhenglai.dsl

import scala.annotation.tailrec

/**
  * Created by zhenglai on 8/15/16.
  */
object Factorial {

  def factorial(i: BigInt): BigInt = {

    @tailrec
    def fac(i: BigInt, accumulator: BigInt): BigInt = {
      if (i <= 1) accumulator
      else
        fac(i - 1, i * accumulator)
    }

    fac(i, 1)
  }
}
