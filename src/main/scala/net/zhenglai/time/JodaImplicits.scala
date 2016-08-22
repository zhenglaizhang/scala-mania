package net.zhenglai.time

import scala.annotation.implicitNotFound

// Creating a type class
@implicitNotFound("No member of type class NumberLike in scope for ${T}")
trait NumberLike[T] {
  def plus(x: T, y: T): T

  def divide(x: T, y: Int): T

  def minus(x: T, y: T): T
}

object JodaImplicits {

  import org.joda.time.Duration

  implicit object NumberLikeDuration extends NumberLike[Duration] {
    def plus(x: Duration, y: Duration): Duration = x.plus(y)

    def divide(x: Duration, y: Int): Duration = Duration.millis(x.getMillis / y)

    def minus(x: Duration, y: Duration): Duration = x.minus(y)
  }

}
