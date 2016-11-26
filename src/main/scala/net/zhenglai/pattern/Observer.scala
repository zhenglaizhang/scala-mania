package net.zhenglai.pattern

/**
 * Created by zhenglai on 8/16/16.
 */

// The trait for clients who want to be notified of state changes
trait Observer[-State] {

  def receiveUpdate(state: State): Unit
}
