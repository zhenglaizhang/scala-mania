package net.zhenglai.db

/**
 * Created by zhenglai on 8/16/16.
 */
object Database {

  sealed trait Status

  //
  case class ResultSet( /*...*/ )

  //
  case class Connection( /*...*/ )

  //
  case class DatabaseException(message: String, cause: Throwable) extends RuntimeException(message, cause)

  case class Connected(connection: Connection) extends Status

  case class QuerySucceeded(results: ResultSet) extends Status

  case class QueryFailed(e: DatabaseException) extends Status

  // no additional state, behave like flags indicating a state
  // pay attention that the hashCode is just calculated from the object short name (without full package prefix)
  case object Disconnected extends Status

}

class Database {

  import Database._

  def connect(server: String): Status = ???

  //
  def disconnect(): Status = ???

  def query( /*...*/ ): Status = ???
}
