package net.zhenglai.concurrent.akka.db.messages

import akka.actor.{Actor, ActorLogging, Status}

import scala.collection.mutable

/**
  * this actor can be used as a thread-safe caching abstraction (and eventually a full-on distributed key-value store).
  */
class AkkademyDb extends Actor with ActorLogging {

  val map = new mutable.HashMap[String, Object]

  /*type Receive = PartialFunction[Any, Unit]*/
  /*
  We define the behavior for the response to the SetRequest message using pattern matching to produce the partial function.
   */
  override def receive: Receive = {
    /*how the actor should behave in response to different message types (with content if any).*/
    /*Scala is a natural  t as the language has pattern matching as a  rst-class language construct*/
    case SetRequest(key, value) => {
      log.info("received SetRequest - key: {} value: {}", key, value)
      map.put(key, value)
      sender ! Status.Success
    }

    case GetRequest(key) => {
      log.info("received GetRequest - key: {}", key)
      map.get(key) match {
        case Some(x) => sender ! x
        case None    => sender ! Status.Failure(new KeyNotFoundException(key))
      }
    }
    case unknown         => {
      log.info("received unknown message: {}", unknown)
      sender ! Status.Failure(new ClassNotFoundException)
    }
  }
}
