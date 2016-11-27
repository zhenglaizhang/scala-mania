package net.zhenglai.slick

import slick.driver.H2Driver.api._
import slick.lifted.{ ProvenShape, ForeignKeyQuery }

// Suppliers table
class Suppliers(tag: Tag)
    extends Table[(Int, String, String, String, String, String)](tag, "SUPPLIERS") {

  def id: Rep[Int] = column[Int]("SUP_ID", O.PrimaryKey)

  def name: Rep[String] = column[String]("SUP_NAME")

  def street: Rep[String] = column[String]("STREET")

  def city: Rep[String] = column[String]("CITY")

  def state: Rep[String] = column[String]("STATE")

  def zip: Rep[String] = column[String]("ZIP")

  override def * : ProvenShape[(Int, String, String, String, String, String)] =
    (id, name, street, city, state, zip)
}

// Coffees table
class Coffees(tag: Tag)
    extends Table[(String, Int, Double, Int, Int)](tag, "COFFEES") {

  def name: Rep[String] = column[String]("COF_NAME", O.PrimaryKey)

  def supID: Rep[Int] = column[Int]("SUP_ID")

  def price: Rep[Double] = column[Double]("PRICE")

  def sales: Rep[Int] = column[Int]("SALES")

  def total: Rep[Int] = column[Int]("TOTAL")

  def * : ProvenShape[(String, Int, Double, Int, Int)] =
    (name, supID, price, sales, total)

  // A reified foreign key relation that can be navigated to create a join
  def supplier: ForeignKeyQuery[Suppliers, (Int, String, String, String, String, String)] =
    foreignKey("SUP_FK", supID, TableQuery[Suppliers])(_.id)
}
