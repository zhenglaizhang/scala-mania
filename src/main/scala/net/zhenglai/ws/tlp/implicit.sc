import scala.runtime.RichInt

/*
An Implicit View can be triggered when the prefix of a selection (consider for example, the.prefix.selection(args) does not contain a member selection that is applicable to args (even after trying to convert args with Implicit Views). In this case, the compiler looks for implicit members, locally defined in the current or enclosing scopes, inherited, or imported, that are either Functions from the type of that the.prefix to a type with selection defined, or equivalent implicit methods.
 */

1.min(2)

val implictConv = implicitly[Int => RichInt ]
implictConv(1)


/*
Implicit Views can also be triggered when an expression does not conform to the Expected Type,
 */
8: scala.runtime.RichInt


/*
Accessing an Implicit Parameter Introduced by a Context Bound

Implicit parameters are arguably a more important feature of Scala than Implicit Views. They support the type class pattern.

The standard library uses this in a few places -- see scala.Ordering and how it is used in SeqLike#sorted. Implicit Parameters are also used to pass Array manifests, and CanBuildFrom instances.

Scala 2.8 allows a shorthand syntax for implicit parameters, called Context Bounds. Briefly, a method with a type parameter A that requires an implicit parameter of type M[A]
 */

//def foo[A](implicit ma: M[A])
// could be rewritten as
//def foo[A : M]()
/*
def foo[A: M] = {
  val ma = implicitly[M[A]]
}
*/


trait Show[T] { def show(t: T): String }
object Show {
  implicit def IntShow: Show[Int] = new Show[Int] { def show(i: Int) = i.toString }
  implicit def StringShow: Show[String] = new Show[String] { def show(s: String) = s }

  def ShoutyStringShow: Show[String] = new Show[String] { def show(s: String) = s.toUpperCase }
}

case class Person(name: String, age: Int)
object Person {
  implicit def PersonShow(implicit si: Show[Int], ss: Show[String]): Show[Person] = new Show[Person] {
    def show(p: Person) = "Person(name=" + ss.show(p.name) + ", age=" + si.show(p.age) + ")"
  }
}

val p = Person("bob", 25)
implicitly[Show[Person]].show(p)

Person.PersonShow(si = implicitly, ss = Show.ShoutyStringShow).show(p)


/*
implicitly is just a convenience method to look up an implicit value that you know already exists. So it fails to compile when there is no such implicit value in scope.
 */

def find[C: Numeric](a: C, b: C): C = implicitly[Numeric[C]].plus(a, b)

def find2[C](a: C, b: C)(implicit n: Numeric[C]) = n.plus(a, b)

find(1, 2)
find(12, 12)