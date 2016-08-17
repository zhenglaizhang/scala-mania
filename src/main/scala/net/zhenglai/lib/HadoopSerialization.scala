package net.zhenglai.lib

/**
  * Created by zhenglai on 8/17/16.
  */
object HadoopSerialization {

  object Serialization {

    type Writable[A] = A => Rem[A]
    //
    implicit val fromInt: Writable[Int] = (i: Int) => Rem(i)
    implicit val fromFloat: Writable[Float] = (f: Float) => Rem(f)
    implicit val fromString: Writable[String] = (s: String) => Rem(s)

    case class Rem[A](value: A) {
      def serialized: String = s"-- $value --"
    }
  }

  import Serialization._

  object RemoteConnection {
    def write[T: Writable](t: T): Unit = //
      println(t.serialized) // Use stdout as the "remote connection"
  }

  RemoteConnection.write(100) // Prints -- 100 --
  RemoteConnection.write(3.14f) // Prints -- 3.14 --
  RemoteConnection.write("hello!") // Prints -- hello! --
  // RemoteConnection.write((1, 2))

}
