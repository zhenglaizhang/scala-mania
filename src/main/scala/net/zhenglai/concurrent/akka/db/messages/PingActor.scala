package net.zhenglai.concurrent.akka.db.messages

import java.io.IOException

import akka.actor.{Actor, ActorLogging, OneForOneStrategy, Props, Status, SupervisorStrategy}

import scala.concurrent.duration.Duration

/*
Using message-passing instead of method invocation enforces encapsulation.
 */
class PingActor(val response: String) extends Actor with ActorLogging {

  //  Receive is scala. PartialFunction[scala. Any, scala.Unit].
  override def receive: Receive = {

    /*
    The tell method ! has an implicit ActorRef parameter in the method signature. It defaults to noSender if you're using tell from somewhere outside an actor.

    The Actor has an implicit sender value through self, which is used in the actor, so tell will always provide self as the sender.
   implicit final val self = context.self
     */
    case "Ping" => sender ! response
    //  access the sender ActorRef via the sender() method.
    case unknown => sender ! Status.Failure(new Exception(s"unknown message $unknown"))
  }

  override def supervisorStrategy: SupervisorStrategy = {
    import SupervisorStrategy._
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = Duration.create("1 minute")) {
      case _: IOException => Restart
    }
  }
}

object PingActor {
  def props(response: String = "Pong"): Props = {
    Props(classOf[PingActor], response)
  }

}
