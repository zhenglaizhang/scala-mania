package net.zhenglai.entity

/**
 * Created by zhenglai on 8/16/16.
 */
case class Person(name: String, age: Option[Int] = None, address: Option[Address] = None) extends PersonState {
  // if no companion object apply defined, we still need to use new to construct the object
  // named / optional parameters and overloaded apply factory methods in objects

  // auxiliary constructor / secondary constructor
  //  def this(name: String) = this(name, None, None)

  //  def this(name: String, age: Int) = this(name, Some(age), None)

  //  def this(name: String, age: Int, address: Address) = this(name, Some(age), Some(address))

  //  def this(name: String, address: Address) = this(name, None, Some(address))
}

// The compiler does not automatically generate apply methods for secondary constructors in case classes.

object Person {
  def apply(name: String): Person = new Person(name)

  def apply(name: String, age: Int): Person = new Person(name, Some(age))

  def apply(name: String, age: Int, address: Address): Person =
    new Person(name, Some(age), Some(address))

  def apply(name: String, address: Address): Person =
    new Person(name, address = Some(address))
}