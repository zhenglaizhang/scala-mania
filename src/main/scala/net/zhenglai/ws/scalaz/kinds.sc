/*
Types are little labels that values carry so that we can reason about the values. But types have their own little labels, called kinds. A kind is more or less the type of a type. … What are kinds and what are they good for? Well, let’s examine the kind of a type by using the :k command in GHCI.




scala> :k Int
scala.Int's kind is A

scala> :k -v Int
scala.Int's kind is A
*
This is a proper type.

scala> :k -v Option
scala.Option's kind is F[+A]
* -(+)-> *
This is a type constructor: a 1st-order-kinded type.

scala> :k -v Either
scala.util.Either's kind is F[+A1,+A2]
* -(+)-> * -(+)-> *
This is a type constructor: a 1st-order-kinded type.

scala> :k -v Equal
scalaz.Equal's kind is F[A]
* -> *
This is a type constructor: a 1st-order-kinded type.

scala> :k -v Functor
scalaz.Functor's kind is X[F[A]]
(* -> *) -> *
This is a type constructor that takes type constructor(s): a higher-kinded type.

 */