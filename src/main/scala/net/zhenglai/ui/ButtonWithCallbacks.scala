package net.zhenglai.ui

/**
  * Created by zhenglai on 8/16/16.
  */

/*
() => Unit:   pure side effects
 */
class ButtonWithCallbacks(val label: String, val callbacks: List[() => Unit]) extends Widget {

  def click(): Unit = {
    updateUI()

    callbacks foreach {
      _ ()
    }
  }

  protected def updateUI(): Unit = {
    println(s"Drawing $label")
  }
}


object ButtonWithCallbacks {

  def apply(label: String, callback: () => Unit) =
    new ButtonWithCallbacks(label, List(callback))

  def apply(label: String) =
    new ButtonWithCallbacks(label, Nil)
}