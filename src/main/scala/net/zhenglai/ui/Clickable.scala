package net.zhenglai.ui

/**
 * Created by zhenglai on 8/16/16.
 */
trait Clickable {

  def click(): Unit = updateUI()

  protected def updateUI(): Unit

}
