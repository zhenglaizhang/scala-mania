package net.zhenglai.generator

import scalaz._

/**
  * Created by zhenglai on 8/16/16.
  */
object Validator {
  // scalaz Validation
  def positive5(i: Int): Validation[List[String], Int] = {
    if (i > 0) scalaz.Success(i)
    else scalaz.Failure(List(s"Nonpositive integer $i"))
  }

//  for {
//    i1 <- positive5(5)
//    i2 <- positive5(10 * i1)
//    i3 <- positive5(25 * i2)
//    i4 <- positive5(2 * i3)
//  } yield s"$i1$i2$i3$i4"
//
//  for {
//    i1 <- positive5(5)
//    i2 <- positive5(-1 * i1) // EPIC FAIL!
//    i3 <- positive5(25 * i2)
//    i4 <- positive5(-2 * i3) // EPIC FAIL!
//  } yield s"$i1$i2$i3$i4"
//
//
//  positive5(5) +++ positive5(-10) +++ positive5(-20)
}
