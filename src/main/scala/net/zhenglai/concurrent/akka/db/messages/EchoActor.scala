package net.zhenglai.concurrent.akka.db.messages

import akka.actor.Actor

class EchoActor extends Actor {

  import EchoActor._

  var lastValue: Object = _

  override def receive: Receive = {
    case Store(value: Object) =>
      lastValue_=(value)
    case EchoActor.Read =>
      sender ! Result(lastValue)
  }
}

object EchoActor {

  case class Store(value: Any)

  case class Result(value: Any)

  case object Read

}
