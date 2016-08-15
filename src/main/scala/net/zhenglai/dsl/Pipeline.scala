package net.zhenglai.dsl

/**
  * Created by zhenglai on 8/15/16.
  */
object Pipeline {

  implicit class toPiped[V](value: V) {
    def |>[R](f: V => R) = f(value)
  }

}
