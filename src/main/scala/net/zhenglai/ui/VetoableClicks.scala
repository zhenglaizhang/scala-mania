package net.zhenglai.ui

/**
  * Created by zhenglai on 8/16/16.
  */
trait VetoableClicks extends Clickable {

  val maxAllowed = 1

  private var count = 0

  abstract override def click(): Unit = {
    if (count < maxAllowed) {
      count += 1
      super.click()
    }
  }

}
