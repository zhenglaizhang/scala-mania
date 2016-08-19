package net.zhenglai.date

/*
 it’s actually not a built-in and is implemented by smartly using the Scala type system to make it look like an enum.
 */
object WeekDay extends Enumeration {
  //  define an Type Alias for Enumerations internal Value type, since we make the name match the object’s name
  type WeekDay = Value

  // "multi assignment", so every val on the left-hand side gets assigned a different instance of Value.
  val Mon, Tue, Wed, Thu, Fri, Sat, Sun = Value
}

object Main extends App {
  val a, b = 12

  /*
  This import causes two things, first we can refer to Mon without prefixing it with WeekDay, but it also brings the type WeekDay into scope
   */
  import WeekDay._

  def isWorkingDay(wd: WeekDay) = !(wd == Sat || wd == Sun)

  WeekDay.values.filter(isWorkingDay).foreach(println)
}
