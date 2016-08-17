package net.zhenglai.concurrent

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * Created by zhenglai on 8/17/16.
  */
object FutureTest {


  def main(args: Array[String]): Unit = {
    val futures: IndexedSeq[Future[String]] = (0 to 9) map {
      // ForkJoinPool
      i => Future {
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
  }

}
