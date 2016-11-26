package net.zhenglai.concurrent

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{ Await, Future }
import scala.util.{ Failure, Success, Try }

/**
 * Created by zhenglai on 8/17/16.
 */

case class ThatsOdd(i: Int) extends RuntimeException(s"odd $i received")

object FutureTest {

  val doComplete: PartialFunction[Try[String], Unit] = {
    case s @ Success(_) => println(s)
    case f @ Failure(_) => println(f)
  }

  def main(args: Array[String]): Unit = {
    val futures: IndexedSeq[Future[String]] = (0 to 9) map {
      // ForkJoinPool
      i =>
        Future {
          val s = i.toString
          println(s)
          s
        }
    }

    // fold walks through the Futures in the same order in which they were constructed.
    // Future.fold and similar methods execute asynchronously themselves; they return a new Future.
    val f: Future[String] = Future.reduce(futures)(_ + _)

    // block current thread until the Future f completes
    // block on the result
    val n = Await.result(f, Duration.Inf)

    println(s"Final result n is $n")

    println("---------------------")
    val futures2 = (0 to 9) map {
      case i if i % 2 == 0 => Future.successful(i.toString)
      case i               => Future.failed(ThatsOdd(i))
    }

    futures2 foreach (_ onComplete doComplete)

    println("---------------------")
    val futures3: IndexedSeq[Future[String]] = (0 to 9) map {
      // ForkJoinPool
      i =>
        Future {
          val s = i.toString
          println(s)
          s
        }
    }

    futures3 foreach {
      f =>
        f.map(_.toInt * 2).filter(_ > 10).foreach(i => println(s"Processed $i"))
    }
  }

}
