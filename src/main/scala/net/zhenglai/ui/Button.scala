package net.zhenglai.ui

/**
  * Created by zhenglai on 8/16/16.
  */
class Button(val label: String) extends Widget {

  // only one concern, handling clicks
  def click(): Unit = updateUI()

  def updateUI(): Unit = {
    println(s"updating $label")
  }
}
