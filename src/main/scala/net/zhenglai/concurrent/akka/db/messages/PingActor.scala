package net.zhenglai.concurrent.akka.db.messages

import akka.actor.{Actor, ActorLogging, Status}

/*
Using message-passing instead of method invocation enforces encapsulation.
 */
class PingActor extends Actor with ActorLogging {

  //  Receive is scala. PartialFunction[scala. Any, scala.Unit].
  override def receive: Receive = {

    /*
    The tell method ! has an implicit ActorRef parameter in the method signature. It defaults to noSender if you're using tell from somewhere outside an actor.

    The Actor has an implicit sender value through self, which is used in the actor, so tell will always provide self as the sender.
   implicit final val self = context.self
     */
    case "Ping" => sender ! "Pong"
    //  access the sender ActorRef via the sender() method.
    case unknown => sender ! Status.Failure(new Exception(s"unknown message $unknown"))
  }
}
