package net.zhenglai.entity

/**
 * Created by zhenglai on 8/16/16.
 */

// We cannot derive one case class from another case class
//  since toString, equals, hashCode wont work properly for subclass
// We are subclassing state!!
case class Employee(
  name: String,
    age: Option[Int] = None,
    address: Option[Address] = None,
    title: String = "[unknown]", //
    manager: Option[Employee] = None
) extends PersonState with EmployeeState {
  // invoking parent class constructor
  // super can not be used to invoke superclass ctor

  // Override toString, otherwise Person.toString will be used
  override def toString = s"Employee($name, $age, $address, $title, $manager)"
}
