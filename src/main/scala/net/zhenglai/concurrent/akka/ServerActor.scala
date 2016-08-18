package net.zhenglai.concurrent.akka

import akka.actor.{Actor, ActorLogging, OneForOneStrategy, SupervisorStrategy}
import akka.util.Timeout

import scala.concurrent.duration._
import scala.util.control.NonFatal

/**
  * Created by zhenglai on 8/18/16.
  */
class ServerActor extends Actor with ActorLogging {

  override val supervisorStrategy: SupervisorStrategy = {
    val decider: SupervisorStrategy.Decider = {
      case WorkerActor.CrashException => SupervisorStrategy.Escalate
      case NonFatal(ex) => SupervisorStrategy.Resume
    }

    OneForOneStrategy(
      maxNrOfRetries = 10,
      withinTimeRange = 100 seconds,
      loggingEnabled = true
    )(decider orElse super.supervisorStrategy.decider)
  }
  implicit val timeout = Timeout(1 seconds)

  override def receive: Receive = {
    case _ => ???
  }
}
