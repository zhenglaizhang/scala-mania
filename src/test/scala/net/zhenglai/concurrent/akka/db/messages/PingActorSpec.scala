package net.zhenglai.concurrent.akka.db.messages

import akka.actor.{ ActorRef, ActorSystem, Props }
import akka.pattern.ask
import akka.util.Timeout
import org.scalatest.{ BeforeAndAfterEach, FunSpecLike, Matchers }

import scala.concurrent.duration._
import scala.concurrent.{ Await, Future }

class PingActorSpec extends FunSpecLike with Matchers with BeforeAndAfterEach {

  /**
   * get a reference to an actor system
   *
   * An actor system is a hierarchical group of actors which share common
   * configuration, e.g. dispatchers, deployments, remote capabilities and
   * addresses. It is also the entry point for creating or looking up actors.
   */
  implicit val system = ActorSystem()

  implicit val timeout = Timeout(5 seconds)

  val pingActor = system.actorOf(PingActor.props("Pong"))

  def askThat(actor: ActorRef, message: String): Future[String] = (actor ? message).mapTo[String]

  /*
  This basic pattern can be built on for unit testing Actors synchronously.
   */
  describe("Ping-Pong Actor") {
    describe("when Ping is sent") {
      it("should respond with Pong") {
        /* we ask the actor for a response to a message
        This gives us back a placeholder—a Future—that represents the actor's reply. In the actor code, the actor will send a message back to sender(), which we will receive as the response to this future.

        Asking an actor for a response demonstrates how to talk to an actor from outside the actor system by getting a response via a future.

        Scala future requires a timeout (specied via implicit parameters in the ask method), so the future will fail if the timeout is violated.
         */
        val future = pingActor ? "Ping"

        /*
           * Sends a one-way asynchronous message. E.g. fire-and-forget semantics.
         */
        pingActor ! "Ping"

        /*
        The Actor is untyped, so we get back a Future[AnyRef]. We call future. mapTo[String] to change the future's type to the expected type of the result.

       Don't sleep or block outside tests.!!!
       You should only have non-blocking code outside of the test context.
         */
        val result = Await.result(future.mapTo[String], 1 second)
        assert(result == "Pong")
      }

      it("should fail on unknown message") {
        val future = pingActor ? "unknown"
        intercept[Exception] {
          Await.result(future.mapTo[String], 1 second)
        }
      }
    }

    describe("when Echo is set as response and Ping is sent") {
      it("should response with Echo") {
        //        val otherPingActor = system.actorOf(PingActor.props("Echo"))
        val otherPingActor = system.actorOf(Props(classOf[PingActor], "Echo"))
        val future = otherPingActor ? "Ping"
        Await.result(future.mapTo[String], 1 second) should equal("Echo")
      }
    }

    describe("Future example") {
      import scala.concurrent.ExecutionContext.Implicits.global

      it("should print to console") {

        /*
        Note that onSuccess takes a partial function, so it  ts well with the Akka untyped responses—pattern matching takes care of determining the type of the result.
         */
        (pingActor ? "Ping").onSuccess {
          case x: String => assert(x == "Pong")
        }

        /*
        The most common use case is the need to transform a response asynchronously before doing something with it
        TODO: bugfix
        These will give you back new Futures of type Char.
         */
        (pingActor ? "Ping").map {
          case x: String => x.charAt(0)
        } onSuccess {
          case x: Char => assert(x == 'P')
        }

        (askThat(pingActor, "Ping")).onSuccess {
          case x: String => assert(x == "Pong")
        }

        /*
        compose our futures to make these chained asynchronous operations.
         */
        val f: Future[String] = askThat(pingActor, "Ping") flatMap { x => askThat(pingActor, x) }
        /*
        This is a very powerful way of handling pipelines of data processing. You can make a call to a remote service and then make a call to a second service with the result.
         */

        Thread.sleep(100)
      }
    }

    describe("Future ") {
      import scala.concurrent.ExecutionContext.Implicits.global
      it("should handle failures(Throwable) elegantly") {
        askThat(pingActor, "unknown").onFailure {
          case e: Exception => println("Got exception")
        }
      }

      it("could recover from failure with default value") {
        askThat(pingActor, "unknown").recover {
          case e: Exception => "default"
        }.onSuccess {
          case x: String => assert(x == "default")
        }
      }

      it("could recover from failure with another asynchronous operatione") {
        /*
        recoverWith is the function we want to invoke—this is like flatMap for the error case
         */
        askThat(pingActor, "unknown").recoverWith {
          case e: Exception => askThat(pingActor, "Ping")
        }.onSuccess {
          case x: String => assert(x == "Pong")
        }
      }

      it("could be chained together") {
        /*
        recoverWith is the function we want to invoke—this is like flatMap for the error case
         */
        askThat(pingActor, "unknown").flatMap {
          x => askThat(pingActor, x)
        }.recoverWith {
          case e: Exception => askThat(pingActor, "Ping")
        }.onSuccess {
          case x: String => assert(x == "Pong")
        }

        /*
        Any failure along the way becomes the failure at the end of the chain. This leaves us with an elegant pipeline of operations where exceptions are handled at the end regardless of which operation caused the failure. We can focus on describing the happy path without extraneous error checking throughout the pipeline. At the end, failure as an effect is described separately.
         */
      }

      it("could be combined together") {
        val f1 = Future {
          4
        }
        val f2 = Future {
          5
        }

        /*
        In this way, we can parallelize work, making multiple requests at the same time to get responses back to users faster.
        TODO: is this really parallelized?
         */
        val futureAddition = for {
          r1 <- f1
          r2 <- f2
        } yield (r1 + r2)

        val r = Await.result(futureAddition, 1 second)
        assert(r == 9)
      }

      it("could be flipped/sequenced") {
        import scala.concurrent.ExecutionContext.Implicits.global
        val listOfFutures: List[Future[String]] = List("Pong", "Pong", "failed").map(x => askThat(pingActor, x))
        Future.sequence(listOfFutures)
        // List[Future[String]] => Future[List[String]]
        /*
         The future generated by sequence will fail if any of the futures in the list fail. We can recover each future before sequencing if we want to get any successful values instead of failing everything:
         */

        Future.sequence(listOfFutures.map(future => future.recover { case _: Exception => "" }))
      }
    }
  }
}
