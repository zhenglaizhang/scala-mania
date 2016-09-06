package net.zhenglai.concurrent.akka.db.messages

import akka.actor.ActorSystem
import akka.testkit.TestActorRef
import net.zhenglai.concurrent.akka.db.messages.EchoActor.Store
import org.scalatest.{BeforeAndAfterEach, FunSpecLike, Matchers}

class EchoActorSpec extends FunSpecLike with Matchers with BeforeAndAfterEach {

  /**
    *  get a reference to an actor system
    *
    * An actor system is a hierarchical group of actors which share common
    * configuration, e.g. dispatchers, deployments, remote capabilities and
    * addresses. It is also the entry point for creating or looking up actors.
    */
  implicit val system = ActorSystem()


  /*
  This basic pattern can be built on for unit testing Actors synchronously.
   */
  describe("echoActor") {
    describe("given Store") {
      it("should remember the value") {
        // use Akka Testkit to create a TestActorRef which has a synchronous API, and lets us get at the underlying actor.
        /**
          * Actor instances are hidden away so the act of creating an actor in our actor system returns an ActorRef
          */
        val actorRef = TestActorRef(new EchoActor)

        /*
        Because we are using TestActorRef, the call to tell will not continue until the request is processed.
        tell is an asynchronous operation that returns immediately in normal usage.
         */
        actorRef ! Store("value")
        val akkademyDb = actorRef.underlyingActor
        akkademyDb.lastValue should equal("value")
      }


      it("should remember only the last value") {
        val actorRef = TestActorRef(new EchoActor)

        actorRef ! Store("v1")
        actorRef ! Store("v2")
        actorRef.underlyingActor.lastValue should equal ("v2")
      }
    }
  }
}
