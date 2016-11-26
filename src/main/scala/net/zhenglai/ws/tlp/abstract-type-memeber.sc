
/*
An Abstract Type is a type that is not known yet and we can define later, it is defined with the keyword type, it basically works for types like the def keyword works for values

With Abstract Type Members we say "I expect someone to tell me about some type - I’ll refer to it by the name MyType". It’s most basic function is allowing us to define generic classes (templates), but instead of using the class Clazz[A, B] syntax, we name them inside the class


We can use Abstract Type members, in similar situations like we use Type Parameters, but without the pain of having to pass them around explicitly all the time - the passing around happens because it is a field. The price paid here though is that we bind those types by-name.
 */

// put this together this with Dependent Types
trait Foo {
  // T defines a type that we can define later.
  type T

  def value: T
}

object FooString extends Foo {
  type T = String
  def value = "FooString"
}

object FooInt extends Foo {
  // T is an alias
  type T = Int
  def value = 12
}

// the function getValue is able to change his return type depending on the input that we pass
/*
We use Dependent Types and Abstract Types to change the return type of a function and also that the type keyword allows us to define functions at type level
 */
def getValue(f: Foo): f.T = f.value // path dependent type

val fs: String = getValue(FooString)
val fi: Int = getValue(FooInt)

// type is not an alias!
type Str = String // one alias

// not an alias
// this is not just an alias anymore, it is actually a function, that takes T as parameter and returns Either[String, T] as a result.
type Result[T] = Either[String, T]

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

