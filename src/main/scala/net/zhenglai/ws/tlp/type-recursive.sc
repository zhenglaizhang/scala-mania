
/*
To use recursion, define a trait with a type declaration. A type declaration is an abstract type that can have type parameters and bounds. Then, define a subtrait that implements the type declaration. Recursion is allowed in this implementation, with restrictions that are discussed later.
 */


// defne abstract type and bounds
trait Recurse {
  type Next <: Recurse

  // this is the recursive function definition
  // Note that Int is arbitrary and could be replaced by any type.
  type X[R <: Recurse] <: Int
}

// implementation
trait RecurseA extends Recurse {
  type Next = RecurseA

  // this is the implementation
  type X[R <: Recurse] = R#X[R#Next]
}

// TODO