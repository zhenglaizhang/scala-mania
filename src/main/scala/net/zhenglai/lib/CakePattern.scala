package net.zhenglai.lib

/**
  * Created by zhenglai on 8/17/16.
  */
object CakePattern {

  def main(args: Array[String]): Unit = {
    MyApp.run()
  }

  trait Persistence {
    def startPersistence(): Unit
  }

  //
  trait Midtier {
    def startMidtier(): Unit
  }

  trait UI {
    def startUI(): Unit
  }

  trait Database extends Persistence {
    //
    def startPersistence(): Unit = println("Starting Database")
  }

  trait BizLogic extends Midtier {

    def startMidtier(): Unit = println("Starting BizLogic")
  }

  trait WebUI extends UI {
    def startUI(): Unit = println("Starting WebUI")
  }

  trait App {
    self: Persistence with Midtier with UI =>
    def run() = {
      startPersistence()
      startMidtier()
      startUI()
    }
  }

  object MyApp extends App with Database with BizLogic with WebUI

}
