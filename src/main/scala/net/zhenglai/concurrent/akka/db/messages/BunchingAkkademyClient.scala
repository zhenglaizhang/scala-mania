package net.zhenglai.concurrent.akka.db.messages

import akka.actor.FSM
import net.zhenglai.concurrent.akka.db.messages.StateContainerTypes.RequestQueue

class BunchingAkkademyClient extends FSM[State, RequestQueue] {

  startWith(Disconnected, null)

//  when(Disconnected) {
//    case (_: Connected, container: RequestQueue)  =>
//      if (container.headOption.isEmpty)
//        goto(Connected)
//      else
//        goto(ConnectedAndPending)
//    case (x: GetRequest, container: RequestQueue) =>
//      stay using (container :+ x)
//  }
//  when(Connected) {
//    case (x: GetRequest, container: RequestQueue) =>
//      goto(ConnectedAndPending) using (container :+ x)
//  }
//  when(ConnectedAndPending) {
//    case (Flush, container)                       =>
//      remoteDb ! container;
//      container = Nil
//      goto(Connected)
//    case (x: GetRequest, container: RequestQueue) =>
//      stay using (container :+ x)
//  }

}
