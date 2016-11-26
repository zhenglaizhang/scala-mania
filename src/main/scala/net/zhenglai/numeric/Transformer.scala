package net.zhenglai.numeric

/**
 * Created by zhenglai on 8/15/16.
 */
object Transformer {

  val inverse: PartialFunction[Double, Double] = {
    case d if d != 0.0 => 1.0 / d
  }

  // lift the partial function as one that returns an Option
  val inverseSafe = inverse.lift

  inverse(2)
  inverse(10)
  inverseSafe(2).get
  inverseSafe(0)

  // unlift the function returning Option as partial function
  val inverseOld = Function.unlift(inverseSafe)
  inverseOld(0)
}
