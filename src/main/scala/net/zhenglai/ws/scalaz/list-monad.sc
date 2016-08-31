import scalaz.Scalaz._
import scalaz._

/*
On the other hand, a value like [3,8,9] contains several results, so we can view it as one value that is actually many values at the same time. Using lists as applicative functors showcases this non-determinism nicely.

Let’s look at using List as Applicatives again:
 */


// TODO
^(List(1, 2, 3), List(10, 100, 100)) {
  _ * _
}



/*
let’s try feeding a non-deterministic value to a function:
*/


List(3, 4, 5) >>= { x => List(x, -x) }
List(3, 4, 5) flatMap { x => List(x, -x) }
//List(3, 4, 5) * { x => List(x, -x) }

/*
So in this monadic view, List context represent mathematical value that could have multiple solutions. Other than that manipulating Lists using for notation is just like plain Scala:
 */


for {
  n <- List(1, 2)
  ch <- List('a', 'b')
} yield (n, ch)


