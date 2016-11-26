package net.zhenglai.numeric

/**
 * Created by zhenglai on 8/18/16.
 */
object square {
  def square(x: Int) = x * x

  Seq(None, Some(12)) map {
    case Some(x) => x
    case None    =>
  }

  // not recommended
  Seq(None, Some(12)) map {
    case Some(x) => x
    case None    =>
  }
}
