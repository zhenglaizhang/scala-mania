package net.zhenglai.ui

/**
  * Created by zhenglai on 8/16/16.
  */
class Button(val label: String) extends Widget with Clickable {

  def updateUI(): Unit = {
    /* logic to change GUI appearance */
    println(s"updating $label")
  }
}
