import scalaz.Scalaz._
import scalaz._

/*
Scala’s for notation allows filtering:
 */

for {
  x <- 1 |-> 50 if x.shows contains '7'
} yield x

1 |-> 5 // to
1 |=> 5 // EphemeralStream

1 |--> (2, 10) // step to


/*
The MonadPlus type class is for monads that can also act as monoids.

Similar to Semigroup[A] and Monoid[A], Plus[F[_]] and PlusEmpty[F[_]] requires their instances to implement plus and empty, but at the type constructor ( F[_]) level.

Plus introduces <+> operator to append two containers:


MonadPlus introduces filter operation.
*/

List(1, 2, 3) <+> List(4, 5, 6)

(1 |-> 50) filter { x => x.shows contains '7' }