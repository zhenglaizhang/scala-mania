
for {
  x <- List(1, 2)
} yield x + 2

/*
 Scala for-comprehension, being a syntactic sugar, the compiler does the heavy lifting and converts it to a more traditional map operation
 */

List(1, 2) map (_ + 2)

val first = List(1, 2)
val next = List(3, 4)

for {
  i <- first
  j <- next
  k <- List(5, 6)
} yield s"$i -> $j -> $k"

// is resolved by compiler as

first flatMap {
  i =>
    next flatMap {
      j =>
        List(5, 6) map {
          k => s"$i -> $j -> $k"
        }
    }
}
/*
The key abstraction is the flatMap, which binds the computation through chaining. Each invocation of flatMap returns the same data structure type (but of different value), that serves as the input to the next command in chain. In the above snippet, flatMap takes as input a closure (SomeType) => List[AnotherType] and returns a List[AnotherType]. The important point to note is that all flatMaps take the same closure type as input and return the same type as output. This is what "binds" the computation thread - every item of the sequence in the for-comprehension has to honor this same type constraint.


The above is an example of the List monad in Scala. Lists support filtering, and so does the List monad. In the for-comprehension, we can also filter data items through the if-guards :
 */

for {
  i <- 1 until 10
  j <- 1 until (i - 1)
  if isPrime(i + j)
} yield (i, j)

def isPrime(i: Int) = true

case class Order(lineItem: Option[LineItem])

case class LineItem(product: Option[Product])

case class Product(name: String)

val maybeOrder = Some(Order(Some(LineItem(Some(Product("project"))))))

for {
  order <- maybeOrder
  lineItem <- order.lineItem
  product <- lineItem.product
} yield product.name

maybeOrder flatMap {
  order =>
    order.lineItem flatMap {
      lineItem =>
        lineItem.product map {
          product => product.name
        }
    }
}

/*
Once again we have the magic of flatMap binding the thread of computation. And similar to the earlier example of the List monad, every flatMap in the entire thread is homogenously typed - input type being (T => Option[U]) and the output type (Option[U]). The types participating in this sequence of computation is the Maybe monad type, modeled as Option[T] in Scala.


The flatMap (aka bind in Haskell) operation, which works orthogonally across types and serves as the generic binder of the sequence of actions.


 the two monad types discussed, List[T] and Option[T] are container types, if we consider the latter to be a degenerate version with only 2 elements. However, monads can be designed to be of other types as well e.g. those that work on state manipulation or on doing IO. And for these monads, possibly it is more intuitive to comprehend monads as computations.
 */

val list = List("India", "Japan", "France", "Russia")
val capitals =
  Map("India" -> "New Delhi", "Japan" -> "Tokyo", "France" -> "Paris")

for {
  i <- list
  j <- capitals get (i) orElse (Some("None"))
} yield (j)

list flatMap {
  i =>
    capitals.get(i).orElse(Some("None")) map {
      j => j
    }
}

/*
Developing modular software is all about working at the right level of abstraction. And monads offer yet another machinery to mix and match the right abstractions within your codebase



monads are just one (maybe the most important) model of computation

other models of computation are arrows and applicative functors

Scala has binding syntax for monads, but not (yet) for arrows [ for applicative functors extra syntax makes less sense ]

 */ 