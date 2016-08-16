package net.zhenglai.pattern

/**
  * Created by zhenglai on 8/16/16.
  */
trait Observer[-State] {

  def receiveUpdate(state: State): Unit
}
