package net.zhenglai.net

import java.net.{Socket, SocketAddress}

// a SocketFactory is a function that produces a Socket
trait SocketFactory extends (SocketAddress => Socket) {

}
