package net.zhenglai.lib
/*
 use best with a stable type structure and some volatile (mostly unpredictable increasing) functions on that type structure).
 */

sealed trait Bool


final case object True extends Bool

final case object False extends Bool

object Bool {
  val and: (Bool, Bool) => Bool = (a: Bool, b: Bool) => if (a eq False) False else b

  val or: (Bool, Bool) => Bool = (a: Bool, b: Bool) => if (a eq True) True else b

  def not: Bool => Bool = (b) => if (b eq True) False else True
}

