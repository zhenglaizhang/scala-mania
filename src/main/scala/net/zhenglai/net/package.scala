package net.zhenglai

import java.net.{ Socket, SocketAddress }

package object net {

  // a SocketFactory is a function that produces a Socket
  /*
  type aliases are not new types — they are equivalent to the syntactically substituting the aliased name for its type.
   */

  // Type aliases are bound to toplevel names by using package objects:
  type SocketFactory = SocketAddress => Socket

  val addrToInet: SocketAddress => Long = ???
  val inetToSocket: Long => Socket = ???

  val factory: SocketFactory = addrToInet andThen inetToSocket

  // Dont use subclassing when an alias will do.
  // following trait is not good as type alias above
  trait SocketFactory2 extends (SocketAddress => Socket)

}
