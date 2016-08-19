
/*
With Abstract Type Members we say "I expect someone to tell me about some type - I’ll refer to it by the name MyType". It’s most basic function is allowing us to define generic classes (templates), but instead of using the class Clazz[A, B] syntax, we name them inside the class


We can use Abstract Type members, in similar situations like we use Type Parameters, but without the pain of having to pass them around explicitly all the time - the passing around happens because it is a field. The price paid here though is that we bind those types by-name.
 */


trait SimplestContainer {
//  type A >: Nothing <: Any // abstract type member
  type A

  def value: A
}

// it does not behave exactly like an abstract field - so you can still create a new instance of SimplestContainer without "implementing" the type member A


object IntContainer extends SimplestContainer {
  // We "provide the type" using a Type Alias
  type A = Int

  def value = 12
}




trait OnlyNumbersContainer {
  type A <: Number
  def value: A
}

trait OnlyNumbers {
  type A <: Number
}

// TODO: bug fix
val ints = new SimplestContainer with OnlyNumbers {
//  def value = 12
  type A = Int
  override def value: A = 12
}

val _ = new SimplestContainer with OnlyNumbers {
//  def value = ""
  override def value: A = ???
}

