package net.zhenglai.generator

/**
  * Created by zhenglai on 8/16/16.
  */

/*
a Stream as infinite
A Stream is evaluated on an as-needed basis and only up to the point that you need it.
 */
object FibonacciStream {


  lazy val fibs: Stream[BigInt] =
      BigInt(0) #::
      BigInt(1) #::
      fibs.zip(fibs.tail).map(n => n._1 + n._2)


  fibs take 10 foreach println

}
