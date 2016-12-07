package net.zhenglai.minia.dsl

import scala.concurrent.duration._
import scala.util.{ Failure, Try }

object TryUtil {
  def retry[T](maxDuration: Int = 5, Interval: Int = 500)(cond: => T): T = {
    val deadline = maxDuration.seconds.fromNow
    var ret: Option[T] = None

    def doJob(cond: => T): Option[T] = {
      try {
        Some(cond)
      } catch {
        case e: Throwable => throw e
      }
    }

    while (Try {
      ret = doJob(cond)
    } match {
      case Failure(e) if deadline.hasTimeLeft => true
      case Failure(e) => throw e
      case _ => false
    }) Thread.sleep(Interval)
    ret.get
  }
}
