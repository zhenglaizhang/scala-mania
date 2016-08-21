/*
Feature is a read-only type that allows you to work with the values it will compute and handle failure to do so in an elegant way.

We had a sequential block of code that we passed to the apply method of the Future companion object, and, given an ExecutionContext was in scope, it magically executed that code block asynchronously, returning its result as a Future.


Where Future provides an interface exclusively for querying, Promise is a companion type that allows you to complete a Future by putting a value into it. This can be done exactly once. Once a Promise has been completed, it’s not possible to change it any more.

A Promise instance is always linked to exactly one instance of Future
 */

import concurrent.Future
import concurrent.ExecutionContext.Implicits.global

// the Future returned is a Promise, too
val f: Future[String] = Future { "Hello Promise" }
f.getClass
// class scala.concurrent.impl.Promise$DefaultPromise
// The object you get back is a DefaultPromise, which implements both Future and Promise.

/*
there is obviously no way to complete a Future other than through a Promise – the apply method on Future is just a nice helper function that shields you from this.
 */

import concurrent.Promise
case class TaxCut(reduction: Int)
// either give the type as a type parameter to the factory method:
val taxCut = Promise[TaxCut]()
// or give the compiler a hint by specifying the type of your val:
val taxCut2: Promise[TaxCut] = Promise()

/*
The returned Future might not be the same object as the Promise, but calling the future method of a Promise multiple times will definitely always return the same object to make sure the one-to-one relationship between a Promise and its Future is preserved.
 */
val taxCutF: Future[TaxCut] = taxCut.future


/*
In Scala, you can complete a Promise either with a success or a failure.
Once you have done this, that Promise instance is no longer writable, and future attempts to do so will lead to an exception.


completing your Promise like this leads to the successful completion of the associated Future. Any success or completion handlers on that future will now be called, or if, for instance, you are mapping that future, the mapping function will now be executed.

 the completion of the Promise and the processing of the completed Future will not happen in the same thread. It’s more likely that you create your Promise, start computing its value in another thread and immediately return the uncompleted Future to the caller.
 */
taxCut.success(TaxCut(20))


object Government {
  def redeemCampaignPledge(): Future[TaxCut] = {
    val p = Promise[TaxCut]()
    Future {
      println("Starting the new legislative period.")
      Thread.sleep(2000)
      p.success(TaxCut(20))
      println("We reduced the taxes! You must reelect us!!!!1111")
    }
    p.future
  }
}
/*
Please don’t get confused by the usage of the apply method of the Future companion object in this example. I’m just using it because it is so convenient for executing a block of code asynchronously. I could just as well have implemented the computation of the result (which involves a lot of sleeping) in a Runnable that is executed asynchrously by an ExecutorService, with a lot more boilerplate code. The point is that the Promise is not completed in the caller thread.
 */

val fut = Government.redeemCampaignPledge
fut.foreach{ x =>
  println(s"Got future result $x")
}


import scala.util.{Success, Failure}
val taxCutF2: Future[TaxCut] = Government.redeemCampaignPledge()
println("Now that they're elected, let's see if they remember their promises...")
taxCutF2.onComplete {
  case Success(TaxCut(reduction)) =>
    println(s"A miracle! They really cut our taxes by $reduction percentage points!")
  case Failure(ex) =>
    println(s"They broke their promises! Again! Because of a ${ex.getMessage}")
}


Thread.sleep(9999)







// Future-based programming in practice
/*
If you want to make use of the future-based paradigm in order to increase the scalability of your application, you have to design your application to be non-blocking from the ground-up, which basically means that the functions in all your application layers are asynchronous and return futures.

 If you are using a modern Scala web framework, it will allow you to return your responses as something like a Future[Response]

 This is important since it allows your web server to handle a huge number of open connections with a relatively low number of threads. By always giving your web server Future[Response], you maximize the utilization of the web server’s dedicated thread pool.

 a service in your application might make multiple calls to your database layer and/or some external web service, receiving multiple futures, and then compose them to return a new Future, all in a very readable for comprehension, such as the one you saw in the previous article. The web layer will turn such a Future into a Future[Response].
 */


/*
Non-blocking IO

If at all possible, make use of libraries that are based on Java’s non-blocking IO capabilities, either by using Java’s NIO API directly or through a library like Netty. Such libraries, too, can serve many connections with a reasonably-sized dedicated thread pool.

Blocking IO

To avoid that, place all the code talking to the database inside a future block
// get back a Future[ResultSet] or something similar:
Future {
  queryDB(query)
}
we always used the implicitly available global ExecutionContext to execute such future blocks. It’s probably a good idea to create a dedicated ExecutionContext that you will have in scope in your database layer.

You can create an ExecutionContext from a Java ExecutorService, which means you will be able to tune the thread pool for executing your database calls asynchronously independently from the rest of your application:

Long-running computations

it will occasionally have to call long-running tasks that don’t involve any IO at all, which means they are CPU-bound. These, too, should not be executed by a web server thread.

Future {
  longRunningComputation(data, moreData)
}
Again, if you have long-running computations, having them run in a separate ExecutionContext for CPU-bound tasks is a good idea. How to tune your various thread pools is highly dependent on your individual application
 */

import java.util.concurrent.Executors
import concurrent.ExecutionContext

val executorService = Executors.newFixedThreadPool(4)
val executionContext = ExecutionContext.fromExecutorService(executorService)

