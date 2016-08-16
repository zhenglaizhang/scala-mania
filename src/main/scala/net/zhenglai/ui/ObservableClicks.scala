package net.zhenglai.ui

import net.zhenglai.pattern.Subject

/**
  * Created by zhenglai on 8/16/16.
  */
trait ObservableClicks extends Clickable with Subject[Clickable] {
  override def click(): Unit = {
    super.click()
    notifyObservers(this)
  }
}
