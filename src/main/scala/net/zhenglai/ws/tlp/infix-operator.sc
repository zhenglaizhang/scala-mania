
// infix notation for methods

object Foo {
  def bar(s: String) = println(s)
}

Foo.bar("hello") // standard
Foo bar "hello" // infix


trait Foo[A, B]

type Test1 = Foo[Int, String]   // standard
type Test2 = Int Foo String     // infix



// use symbols in type names
trait ::[A, B]

type Test3 = ::[Int, String]
type Test4 = Int :: String