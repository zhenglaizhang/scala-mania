package net.zhenglai.concurrent.akka.db.messages

import org.scalatest.{BeforeAndAfterEach, FunSpecLike, Matchers}

import scala.concurrent.Await
import scala.concurrent.duration._

class AkkademyDbClientIntegrationSpec extends FunSpecLike with Matchers with BeforeAndAfterEach {

  /**
    * get a reference to an actor system
    *
    * An actor system is a hierarchical group of actors which share common
    * configuration, e.g. dispatchers, deployments, remote capabilities and
    * addresses. It is also the entry point for creating or looking up actors.
    */

  val client = new AkkademyClient("192.168.0.101:2552")

  describe("akkademyDbClient") {
    it("should set a value") {
      client.set("123", new Integer(123))
      val futureResult = client.get("123")
      Await.result(futureResult, 10 seconds) should equal(123)
    }
  }
}
