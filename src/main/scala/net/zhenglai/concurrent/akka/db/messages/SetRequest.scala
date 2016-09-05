package net.zhenglai.concurrent.akka.db.messages

/**
  * Combination of both an insert and an update in one, or like the set operation on a Map.
  * @param key
  * @param value
  */
case class SetRequest(key: String, value: Object) {

}
