package net.zhenglai.concurrent.akka.db.messages

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import org.scalatest.{BeforeAndAfterEach, FunSpecLike, Matchers}

import scala.concurrent.Await
import scala.concurrent.duration._

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
  /*
  This basic pattern can be built on for unit testing Actors synchronously.
   */
  describe("Ping-Pong Actor") {
    describe("when Ping is sent") {
      it("should respond with Pong") {
        /* we ask the actor for a response to a message
        This gives us back a placeholder—a Future—that represents the actor's reply. In the actor code, the actor will send a message back to sender(), which we will receive as the response to this future.
         */
        val future = pingActor ? "Ping"

        /*
        The Actor is untyped, so we get back a Future[AnyRef]. We call future. mapTo[String] to change the future's type to the expected type of the result.
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
  }
}
