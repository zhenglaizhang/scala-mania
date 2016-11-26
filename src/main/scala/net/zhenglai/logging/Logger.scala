package net.zhenglai.logging

/**
 * Created by zhenglai on 8/15/16.
 */
object Logger {

  // In Scala, curried functions are defined with multiple argument lists, each with a single argument
  def log(level: String)(msg: String) = {
    println(s"$level => $msg")
  }

  val info = log("INFO") _

  val error = log("ERROR") _

  val warn = log("WARN") _

}
