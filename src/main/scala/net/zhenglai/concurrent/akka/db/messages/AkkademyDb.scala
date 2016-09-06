package net.zhenglai.concurrent.akka.db.messages

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorLogging, ActorSystem, Props, Stash, Status}

import scala.collection.mutable
import scala.concurrent.duration.Duration

/**
  * this actor can be used as a thread-safe caching abstraction (and eventually a full-on distributed key-value store).
  */
class AkkademyDb extends Actor with ActorLogging with Stash {

  val map = new mutable.HashMap[String, Object]

  /*type Receive = PartialFunction[Any, Unit]*/
  /*
  We define the behavior for the response to the SetRequest message using pattern matching to produce the partial function.
   */
  override def receive: Receive = {
    case x: GetRequest  =>
      stash()
    case CheckConnected => throw new ConnectTimeoutException("dummy")
    case Connected      =>
      context.become(online)
      unstashAll()
    case unknown        => {
      log.info("received unknown message: {}", unknown)
      sender ! Status.Failure(new ClassNotFoundException)
    }
  }


  def online: Receive = {
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

    case _: Disconnected => context.unbecome()
  }

  override def preStart(): Unit = {
//    context.system.scheduler.scheduleOnce(Duration.create(1000, TimeUnit.MICROSECONDS))
  }

}


object AkkademyDbMain extends App {
  val system = ActorSystem("akkademy")
  system.actorOf(Props[AkkademyDb], name = "akkademy-db")
}