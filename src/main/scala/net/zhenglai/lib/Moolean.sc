

sealed abstract class Moolean

case object True extends Moolean

case object False extends Moolean

case object Maybe extends Moolean

object Moolean {
  val not: Moolean => Moolean = m => m match {
    case True  => False
    case False => True
    case Maybe => Maybe
  }

  val equal: (Moolean, Moolean) => Moolean = (a, b) => (a, b) match {
    case (True, True)   => True
    case (False, False) => True
    case (Maybe, Maybe) => True
    case _              => False
  }

  val and: (Moolean, Moolean) => Moolean = (a, b) => (a, b) match {
    case (False, _) | (_, False) => False
    case (Maybe, _) | (_, Maybe) => Maybe
    case _                       => True
  }

  val or: (Moolean, Moolean) => Moolean = (a, b) => (a, b) match {
    case (True, _) | (_, True)   => True
    case (Maybe, _) | (_, Maybe) => Maybe
    case _                       => False
  }

  /*
you’re kind of able to write your own control structures by need. Our algebraic datatype remained untouched. Instead we used implicit type conversion to start a chain for collecting the different parts of the whole expression.
   */
  // Quarternary operator
  implicit def startQuarternary(mool: Moolean) /*: QuarternaryTrueReceiver[Any]*/ = new QuarternaryTrueReceiver(mool)

  class QuarternaryTrueReceiver[+A](mool: Moolean) {
    def ?[A](trueValue: A) = new QuarternaryFalseReceiver(mool, trueValue)
  }

  class QuarternaryFalseReceiver[+A](mool: Moolean, trueValue: A) {
    def |[B >: A](falseValue: B) = new QuarternaryMaybeReceiver(mool, trueValue, falseValue)
  }

  class QuarternaryMaybeReceiver[+A](mool: Moolean, trueVaule: A, falseValue: A) {
    println(s"---- $mool")
    def |[B >: A](maybeValue: B) = mool match {
      case True  => trueVaule
      case False => falseValue
      case Maybe => maybeValue
    }
  }
}
// TODO: bug fix, no output???
"START"
import Moolean._
val shouldBeTrue: String = True ? "TRUE" | "FALSE" | "MAYBE"
val shouldBeZero: Int = Maybe ? 1 | -1 | 0
val shouldBeOne: Int = False ? 1 | -1 | 0

"END"
