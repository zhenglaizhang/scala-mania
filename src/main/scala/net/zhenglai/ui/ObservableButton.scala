package net.zhenglai.ui

import net.zhenglai.pattern.Subject

/**
  * Created by zhenglai on 8/16/16.
  */
class ObservableButton(name: String) extends Button(name) with Subject[Button] {

  override def click(): Unit = {
    // call the parent class click to perform the normal GUI update logic
    super.click()
    notifyObservers(this)
  }

}
