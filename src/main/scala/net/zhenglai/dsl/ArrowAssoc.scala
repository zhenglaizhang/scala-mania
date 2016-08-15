package net.zhenglai.dsl


/**
  * Created by zhenglai on 8/15/16.
  */

object Implicits {

  implicit final class ArrowAssoc[A](val self: A) {
    def ->[B](y: B): Tuple2[A, B] = Tuple2(self, y)
  }


  implicit class jsonForStringContext(val sc: StringContext) {
  }
}


