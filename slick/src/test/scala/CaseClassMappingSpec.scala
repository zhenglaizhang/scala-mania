import scala.concurrent.Await
import scala.concurrent.duration.Duration

import org.scalatest.FunSuite
import slick.driver.H2Driver.api._
import scala.concurrent.ExecutionContext.Implicits.global

class CaseClassMappingSpec extends FunSuite {

  case class User(name: String, id: Option[Int] = None)

  class Users(tag: Tag) extends Table[User](tag, "USERS") {

    // Auto Increment the id primary key column
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

    // The name cannot be null
    def name = column[String]("NAME")

    // the * projection (e.g. select * ...) auto-transforms the tupled
    // column values to / from a user
    def * = (name, id.?) <> (User.tupled, User.unapply)
  }

  test("create table schema and insert rows") {
    val users = TableQuery[Users]
    val db = Database.forConfig("h2mem1")
    try {
      Await.result(db.run(DBIO.seq(
        // create the schema
        users.schema.create,

        // insert two User instances
        users += User("Zhenglai"),
        users += User("Junlai"),

        // print the users (select * from USERS)
        users.result.map(println)
      )), Duration.Inf)
    } finally db.close()
  }
}
