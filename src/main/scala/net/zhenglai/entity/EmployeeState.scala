package net.zhenglai.entity

/**
 * Created by zhenglai on 8/16/16.
 */
trait EmployeeState {

  val title: String

  val manager: Option[Employee]

}
