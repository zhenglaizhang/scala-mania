package net.zhenglai.ui

import net.zhenglai.pattern.Observer

/**
  * Created by zhenglai on 8/16/16.
  */
class ButtonCountObserver extends Observer[Button] {
  var count = 0

  override def receiveUpdate(state: Button): Unit = count += 1

}
