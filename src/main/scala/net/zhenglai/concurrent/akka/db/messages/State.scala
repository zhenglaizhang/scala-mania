package net.zhenglai.concurrent.akka.db.messages

/**
  * For our FSMs, we will improve on the hotswap example to store messages in the actor instead of stashing them:
  *
  * • Disconnected: Not online and no messages are queued
  * • Disconnected and Pending: Not online and messages are queued
  * • Connected: Online and no messages are queued
  * • Connected and Pending: Online and Messages are Pending
  *
  */
sealed trait State {

}

case object DisconnectedState extends State

case object ConnectedState extends State

case object ConnectedAndPending extends State

class Request {}

object StateContainerTypes {
  /**
    * For our state container, we'll store a list of requests to be processed on a  ush event:
    */
  type RequestQueue = List[Request]
}


class Flush {}

case object Flush