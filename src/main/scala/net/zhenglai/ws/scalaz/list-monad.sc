import scalaz._
import Scalaz._

/*
On the other hand, a value like [3,8,9] contains several results, so we can view it as one value that is actually many values at the same time. Using lists as applicative functors showcases this non-determinism nicely.

Let’s look at using List as Applicatives again:
 */

^(List(1, 2, 3), List(10, 100, 100)) { _ * _ }



