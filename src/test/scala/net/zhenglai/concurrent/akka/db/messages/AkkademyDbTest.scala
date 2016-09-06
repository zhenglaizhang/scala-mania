package net.zhenglai.concurrent.akka.db.messages

import akka.actor.ActorSystem
import akka.testkit.TestActorRef
import org.scalatest.{BeforeAndAfterEach, FunSpecLike, Matchers}

class AkkademyDbSpec extends FunSpecLike with Matchers with BeforeAndAfterEach {

  /**
    *  get a reference to an actor system
    *
    * An actor system is a hierarchical group of actors which share common
    * configuration, e.g. dispatchers, deployments, remote capabilities and
    * addresses. It is also the entry point for creating or looking up actors.
    */
  implicit val system = ActorSystem()
  describe("akkademyDb") {
    describe("given SetRequest") {
      it("should place key/value into map") {
        // use Akka Testkit to create a TestActorRef which has a synchronous API, and lets us get at the underlying actor.
        /**
          * Actor instances are hidden away so the act of creating an actor in our actor system returns an ActorRef
          */
        val actorRef = TestActorRef(new AkkademyDb)

        /*
        Because we are using TestActorRef, the call to tell will not continue until the request is processed.
        tell is an asynchronous operation that returns immediately in normal usage.
         */
        actorRef ! SetRequest("key", "value")
        val akkademyDb = actorRef.underlyingActor
        akkademyDb.map.get("key") should equal(Some("value"))
        akkademyDb.map.get("notexist") should not equal(Some("value"))
      }
    }
  }
}
