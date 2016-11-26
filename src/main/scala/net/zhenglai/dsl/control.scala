package net.zhenglai.dsl

import scala.annotation.tailrec

/**
 * Created by zhenglai on 8/15/16.
 */
object control {

  @tailrec
  def continue(conditional: => Boolean)(action: => Unit): Unit = {
    if (conditional) {
      action
      continue(conditional)(action)
    }
  }

  var count = 0
  continue(count < 5) {
    println(s"at $count")
    count += 1
  }
}
