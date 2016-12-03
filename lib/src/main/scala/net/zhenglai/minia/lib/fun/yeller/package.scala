package net.zhenglai.minia.lib.fun

package object yeller {

  case class Yeller(words: String) {
    def yell: String = words.toUpperCase + "!!"
  }

  object `package` {
    implicit def stringToYeller(words: String): Yeller = Yeller(words)
  }
}
