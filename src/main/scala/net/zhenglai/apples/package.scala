package net.zhenglai

/*
It’s a custom to put package object’s in a file called package.scala into the package they’re the object for. I
 */
package object apples extends RedApples with GreenApples {

  val redApples = List(red1, red2)

  val greenApples = List(green1, green2)
}

trait RedApples {
  val red1, red2 = "red"
}

trait GreenApples {
  val green1, green2 = "green"
}
