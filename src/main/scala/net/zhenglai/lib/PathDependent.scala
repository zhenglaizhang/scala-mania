package net.zhenglai.lib

/**
 * Created by zhenglai on 8/17/16.
 */
object PathDependent {

  class C1 {
    val c1: D = new D
    val c2: D = {
      new this.D
    }
    var x = "1"

    def setX1(x: String): Unit = this.x = x

    def setX2(x: String): Unit = C1.this.x = x

    class D

  }

}
