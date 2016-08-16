package net.zhenglai.dsl

/**
  * Created by zhenglai on 8/15/16.
  */
object Op extends Enumeration {

  type Op = Value

  val EQ = Value("=")
  val NE = Value("!=")
  val LTGT = Value("<>")
  val LT = Value("<")
  val LE = Value("<=")
  val GT = Value(">")
  val GE = Value(">=")
}

import Op._

// WHERE x op value
//  op: =, !=, <>, <, <=, > or >=
case class WhereOp[T](columnName: String, op: Op, value: T)

// WHERE x IN (a, b, c, ...)
case class WhereIn[T](columnName: String, val1: T, vals: T*)



