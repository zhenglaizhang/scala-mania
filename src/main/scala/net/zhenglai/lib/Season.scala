package net.zhenglai.lib

/*
a special (most simple) form of algebraic datatypes – enumerated types. In this case, all values of that type could be enumerated one by one.
 */
sealed abstract class Season

final case object Winter extends Season

final case object Spring extends Season

final case object Summer extends Season

final case object Fall extends Season

object Season {
  /*
a powerful technique called pattern matching which will allow us to deconstruct (whatever that means by now) any given value of a certain algebraic datatype, helping us to write more condensed and readable functions!
   */
  val next: Season => Season = next => next match {
    case Winter => Spring
    case Spring => Summer
    case Summer => Fall
    case Fall   => Winter
  }

  val next2: Season => Season = next => fromInt(toInt(next))

  val toInt: Season => Int = s => s match {
    case Spring => 0
    case Summer => 1
    case Fall   => 2
    case Winter => 3
  }

  val fromInt: Int => Season = i => i match {
    case 0 => Spring
    case 1 => Summer
    case 2 => Fall
    case 3 => Winter
  }
}

