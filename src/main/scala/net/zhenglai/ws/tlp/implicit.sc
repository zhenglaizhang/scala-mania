import scala.runtime.RichInt

/*
An Implicit View can be triggered when the prefix of a selection (consider for example, the.prefix.selection(args) does not contain a member selection that is applicable to args (even after trying to convert args with Implicit Views). In this case, the compiler looks for implicit members, locally defined in the current or enclosing scopes, inherited, or imported, that are either Functions from the type of that the.prefix to a type with selection defined, or equivalent implicit methods.

scalaz是由即兴多态（ad-hoc polymorphism）类型（typeclass）组成。scalaz typeclass在scala中的应用有赖于scala compiler的一项特别功能：隐式转换（implicit conversion），使程序表述更精简。由于隐式转换是一项compiler功能，在程序编译（compile）的时候是由compiler来进行类型转换代码的产生和替代的。

作用域（scope）和绑定（binding）。这两样都是在编译程序时compiler需要解决的问题。所谓作用域解析（scope resolution）就是要确定一个绑定在一个作用域里是可视的，否则程序无法通过编译。

作用域就是一个绑定在一个程序范围内的可视型。作用域可以是某个类的内部或者是某个方法或函数的内部，基本上用｛｝就可以创造一个新的作用域了。在scala作用域可以是多层的，一个域可以存在于另一个作用域内。外部域的绑定在内部域内是可视的，反之则不然
 */

class Foo(x: Int) {
  def bar = {
    val y = x + 1 // x是本地域外的一个绑定
  }
}

class Bar(x: Int) {
  def foo = {
    val x = 0 // 一个作用域内的绑定可以屏蔽（shadow）外域定义的绑定, 本地域绑定。屏蔽了外域的x
    val y = x + 1 // y=1,x是本地域的一个绑定
  }
}


1.min(2)

val implictConv = implicitly[Int => RichInt]
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


/*
scala compiler 在编译程序时会根据情况自动进行隐式转换，即代码替代。在两种情况下scala会进行隐形转换：
1、在期待一个类型的地方发现了另外一个类型：
2、当一个类型并不支持某个方法时：
 */
object ab {
  class A {
    def printA = println("I am A")
  }
  class B
  implicit def bToA(x: B): A = new A
}

import ab._
val a: A = new B
/*
需要进行B => A的隐式转换

在这里由于A类和B类没有任何继承关系，应该无法通过编译，但scala compiler会首先尝试搜寻B=>A的隐式转换实例，当找到bToA函数时compiler会把new B替代成bToA(new B),如此这般才能通过编译。
 */

(new B).printA //需要进行B => A的隐式转换

/*
scala compiler 在隐式转换中的隐式解析（implicit resolution）会用以下的策略来查找标示为implicit的实例：

1、能用作用域解析的不带前缀的隐式绑定即：如Bar，而Foo.Bar则不符合要求

这个在以上的例子里已经示范证明了。

2、如果以上方式无法解析隐式转换的话compiler会搜寻目标类型的隐式作用域（implicit scope）内任何对象中的隐式转换。一个类型的隐式作用域（implicit scope）包括了涉及这个类型的所有伴生模块（companion module）内定义的隐式转换。例如：

def foo(implicit p: Foo)，这个方法的参数必须是Foo类型。如果compiler无法进行作用域解析的话就必须搜寻隐式作用域内的匹配隐式转换。比如Foo的伴生对象（companion object）,如下：
*/

object h11 {
  object Container {
    trait Foo1

    object Foo1 {
      implicit def x = new Foo1 {
        override def toString = "implicit x"
      }
    }
  }
  import Container._
  def foo(implicit p: Foo1) = println(p)

  foo
}

h11

/*
隐式转换解析原理

compiler在object Foo1内找到了匹配的隐式转换，程序通过了编译。

由于compiler会首先进行作用域解析，失败后才搜寻隐式转换作用域，所以我们可以把一些默认隐式转换放到隐式作用域里。然后其它编程人员可以通过import来覆载（override）使用他们自己的隐式转换。

综合以上所述：一个类型T的隐式作用域就是组成这个类型的所有类的伴生对象（companion object）。也就是说，T的形成有可能涉及到一组类型。在进行隐式转换解析过程中，compiler会搜寻这些类型的伴生对象。类型T的组成部分如下：

1、所有类型T的父类：
2、如果T是参数化类型，那么所有类型参数的组成类型及包嵌类的组成类型的伴生对象都在隐式转换解析域中。如在解析List[String]中，所有List和String的伴生对象都在解析域中：
3、如果T是个单例对象（singleton object），那么T的包嵌对象（container object）就是解析域：
 */

object h12 {
  object demo {
    object Container {
      trait A
      trait B
      class T extends A with B
      object A {
        implicit def x = new T {
          override def toString = "implicit x"
        }
      }
    }
    import Container._
    // 类型T由A,B组成。compiler从A的伴生对象中解析到隐式转换。
    def foo(implicit p: T) = println(p)            //> foo: (implicit p: scalaz.learn.demo.Container.Foo)Unit
    foo                                              //> implicit x
  }
}

h12.demo


object h13 {
  object Container {
    trait A
    trait B

    class T[A]

    object A {
      implicit def x = new T[A] {
        override def toString = "implicit x of T[A]"
      }
    }
  }

  import Container._
  // A是T[A]的类型参数。compiler从A的伴生对象中解析到隐式转换。
  def foo(implicit p: T[A]) = println(p)
}
h13.foo



object h14 {
  object Container {
    object T {
      def x = "singleton object T"
    }

    implicit def x = T
  }

  import Container._
  // 单例对象T定义于包嵌对象Container内。compiler从Container中解析到隐式转换。
  def foo(implicit p: T.type) = println(p.x)
}
h14.foo