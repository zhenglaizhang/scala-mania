package net.zhenglai.ui

import net.zhenglai.pattern.Subject

/**
  * Created by zhenglai on 8/16/16.
  */
object ObserverApp {

  def main(args: Array[String]): Unit = {
    val button = new ObservableButton("Click Me!")
    val bco1 = new ButtonCountObserver
    val bco2 = new ButtonCountObserver
    button addObserver bco1
    button addObserver bco2
    (1 to 5) foreach (_ => button.click())
    assert(bco1.count == 5)
    assert(bco2.count == 5)

    onTheFly(Seq(bco1, bco2))
  }


  def onTheFly(observers: Seq[ButtonCountObserver]): Unit = {
    val button = new Button("On the fly") with Subject[Button] {
      override // only one concern, handling clicks
      def click(): Unit = {
        super.click()
        notifyObservers(this)
      }
    }

    observers foreach {
      button.addObserver
    }

    (1 to 10) foreach (_ => button.click())

    observers foreach { ob =>
      assert(ob.count == 15)
    }
  }

}
