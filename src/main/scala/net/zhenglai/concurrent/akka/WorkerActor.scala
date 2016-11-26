package net.zhenglai.concurrent.akka

import akka.actor.{ Actor, ActorLogging }

import scala.util.{ Success, Try }

/**
 * Created by zhenglai on 8/18/16.
 */
class WorkerActor extends Actor with ActorLogging {

  import Messages._

  private val dataStore = collection.mutable.Map.empty[Long, String]

  //   type Receive = PartialFunction[Any, Unit]
  override def receive: Receive = {
    case Create(key, value) =>
      dataStore += key -> value
      sender ! Response(Success(s"$key -> $value added"))

    case Read(key) =>
      sender ! Response(Try(s"${dataStore(key)} found for key = $key"))
    case Update(key, value) =>
      dataStore += key -> value
      sender ! Response(Success(s"$key -> $value updated"))
    case Delete(key) =>
      dataStore -= key
      sender ! Response(Success(s"$key deleted"))
    case Crash(_) =>
      throw WorkerActor.CrashException
    case DumpAll =>
      sender ! Response(Success(s"${self.path}: dataStore = $dataStore"))
  }
}

object WorkerActor {

  case object CrashException extends RuntimeException("Crash")

}
