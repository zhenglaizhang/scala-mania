package net.zhenglai.test
import net.zhenglai.test.Prop.{ FailedCase, SuccessCount }

trait Prop {
  // type alias to help the readability

  def check: Either[(FailedCase, SuccessCount), SuccessCount]

  def &&(p: Prop): Boolean = {
    ???
  }

}

object Prop {
  type SuccessCount = Int

  type FailedCase = String
}
