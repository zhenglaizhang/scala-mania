
/*
Existential Types are something that deeply relates to Type Erasure, which JVM languages "have to live with".
 */

val thingy: Any = ???

thingy match {
  case i: List[_] =>
  //  ^ some type, no idea which one!

  //  case i: List[a] =>
  /*
   lower case 'a', matches all types... what type is 'a'?!

  We don’t know the type of a, because of runtime type erasure. We know though that List is a type constructor, * -> *, so there must have been some type, it could have used to construct a valid List[T]. This "some type", is the existentional type!
   */
}