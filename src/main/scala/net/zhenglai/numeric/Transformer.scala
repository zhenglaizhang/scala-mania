package net.zhenglai.numeric

/**
  * Created by zhenglai on 8/15/16.
  */
object Transformer {


  val inverse: PartialFunction[Double, Double] = {
    case d if d != 0.0 => 1.0 / d
  }
}
