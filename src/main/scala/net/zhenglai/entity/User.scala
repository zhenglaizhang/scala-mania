package net.zhenglai.entity

/**
  * Created by zhenglai on 8/18/16.
  */

case class User() {

  // 
}

case object User {
  //  User.getUser provides no more information than User.get
  def get(id: Int): Option[User] = ???
}
