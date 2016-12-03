package net.zhenglai.minia.lib.fun

package object yeller {

  // extends AnyVal to use value classes
  case class Yeller(words: String) extends AnyVal {
    def yell: String = words.toUpperCase + "!!"
  }

  object `package` {
    /* CMD + Shift + T to generate tests*/
    implicit def stringToYeller(words: String): Yeller = Yeller(words)
  }

  object YellerMain extends App {
    import yeller._
    println("Look out".yell)
  }
}
