package net.zhenglai.pattern

/**
  * Created by zhenglai on 8/16/16.
  */

// The trait for subjects who will send notifications to observers.
trait Subject[State] {

  // mutable, not thread-safe
  private var observers: List[Observer[State]] = Nil

  def addObserver(observer: Observer[State]): Unit =
    observers ::= observer

  def notifyObservers(state: State): Unit =
    observers foreach (_.receiveUpdate(state))

}
