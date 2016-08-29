/*
Bounded members have an upper and a lower bound.

Scalaz equivalent for Bounded seems to be Enum as well.


scala> implicitly[Enum[Char]].min
res43: Option[Char] = Some(?)

scala> implicitly[Enum[Char]].max
res44: Option[Char] = Some( )

scala> implicitly[Enum[Double]].max
res45: Option[Double] = Some(1.7976931348623157E308)

scala> implicitly[Enum[Int]].min
res46: Option[Int] = Some(-2147483648)

scala> implicitly[Enum[(Boolean, Int, Char)]].max
<console>:14: error: could not find implicit value for parameter e: scalaz.Enum[(Boolean, Int, Char)]
              implicitly[Enum[(Boolean, Int, Char)]].max
                        ^
Enum typeclass instance returns Option[T] for max values.
*/

import scalaz._
import Scalaz._

implicitly[Enum[Char]].min
implicitly[Enum[Char]].max
implicitly[Enum[Int]].max
