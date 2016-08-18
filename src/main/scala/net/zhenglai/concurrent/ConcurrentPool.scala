package net.zhenglai.concurrent

import java.util.concurrent.{ConcurrentHashMap, ConcurrentLinkedQueue}

class ConcurrentPool[K, V] {

  type Queue = ConcurrentLinkedQueue[V]

  type Map = ConcurrentHashMap[K, V]

}
