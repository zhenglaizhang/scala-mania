package net.zhenglai.entity

/**
  * Created by zhenglai on 8/16/16.
  */
trait PersonState {

  val name: String

  val age: Option[Int]

  val address: Option[Address]

}
