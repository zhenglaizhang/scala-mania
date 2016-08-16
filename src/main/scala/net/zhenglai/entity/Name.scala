package net.zhenglai.entity

/**
  * Created by zhenglai on 8/16/16.
  */
class Name(var first: String, var last: String) {
  def hello_=(hello: String) = println(s"hello $first $last")
}
