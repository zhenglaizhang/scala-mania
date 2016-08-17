package net.zhenglai.concurrent

import scala.async.Async._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * Created by zhenglai on 8/17/16.
  *
  *
  * Async code is cleaner than sequencing Futures; it’s still not as transparent as truly synchronous
  * code, but you get the benefits of asynchronous execution.
  *
  *
  * Using Futures, with or without Async, is a tactic for concurrency, but not a strategy. It
  * doesn’t provide large-scale facilities for managing asynchronous processes, including
  * error handling, on an application-wide scale. For those needs, we have the actor model
  */
object AsyncExample {
  def main(args: Array[String]): Unit = {
    (-1 to 1) foreach {
      id => //
        val fut = AsyncExample.asyncGetRecord(id)
        println(Await.result(fut, Duration.Inf))
    }
  }

  def asyncGetRecord(id: Long): Future[(Long, String)] = async {
    //
    val exists = async {
      val b = recordExists(id)
      println(b)
      b
    }
    if (await(exists)) await(async {
      val r = getRecord(id)
      println(r)
      r
    })
    else (id, "Record not found!")
  }

  def recordExists(id: Long): Boolean = {
    //
    println(s"recordExists($id)...")
    Thread.sleep(1)
    id > 0
  }

  def getRecord(id: Long): (Long, String) = {
    //
    println(s"getRecord($id)...")
    Thread.sleep(1)
    (id, s"record: $id")
  }
}