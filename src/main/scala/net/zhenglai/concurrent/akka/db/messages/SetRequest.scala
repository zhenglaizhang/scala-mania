package net.zhenglai.concurrent.akka.db.messages

/**
  * Messages should always be immutable.
  * (The Scala case class is serializable.)
  *
  * Combination of both an insert and an update in one, or like the set operation on a Map.
  *
  * @param key
  * @param value
  */
case class SetRequest(key: String, value: Object)

case class SetIfNotExistsRequest(key: String, value: Object)

case class GetRequest(key: String)

case class KeyNotFoundException(key: String) extends Exception

