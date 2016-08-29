/*
Scalaz是个通用的函数式编程组件库。它提供的类型、函数组件都必须具有高度的概括性才能同时支持不同数据类型的操作。可以说，scalaz提供了一整套所有编程人员都需要的具有高度概括性的通用函数，它是通过随意多态（ad-hoc polymorphism）来帮助用户使用这些函数的。随意多态就是trait+implicit parameters+implicit conversions。简单的说就是scalaz提供一个概括化的函数，用户可以在各种类型上施用这个同一函数。概括化（generalizing）函数最基本的技巧应该是类型参数变量（parametric type variable）的使用了。
 */

def head[T](xs: List[T]): T = xs(0)

head(List(1, 2, 3))
case class Car(manu: String)
head(Car("Honda") :: Car("Toyota") :: Nil)

/*
无论T是任何类型，Int, String, Car，都可以使用这个head函数。

但作为一个标准库的开发者，除了使用类型变量去概括函数外还必须考虑函数的使用方式以及组件库的组织结构。这篇讨论里我们将从一个组件库开发者的角度来思考、体验如何设计概括化的通用函数。
 */

/*
我们试着从概括化一个函数sum的过程中了解scalaz的设计思路：

针对List,Int的函数：sum(xs: List[Int]): Int

概括化后变成：sum[M[_],A](xs: M[A]): A
 */
def sum(xs: List[Int]): Int = xs.foldLeft(0)(_+_)
sum(List(1, 2, 3))
//你不能这样：sum(List(1.0,2.0,3.0)


/*
我们先看看这个foldLeft: 它需要一个起始值（在这里是Int 0）和一个两个值的操作（在这里是两个Int的加法）。我们可以先把这部分抽象一下：
 */

implicit object intMonoid extends Monoid[Int] {
  def mappend(i1: Int, i2: Int): Int = i1 + i2
  def mzero = 0
}
def sum2(xs: List[Int]): Int = xs.foldLeft(intMonoid.mzero)(intMonoid.mappend)

/*
我们把这个intMonoid抽了出来。那么现在的sum已经具有了一些概括性了，因为foldLeft的具体操作依赖于我们如何定义intMonoid。
*/
implicit object stringMonoid extends Monoid[String] {
  def mappend(s1: String, s2: String): String = s1 + s2
  def mzero = ""
}
def sum3(xs: List[String]): String = xs.foldLeft(stringMonoid.mzero)(stringMonoid.mappend)
//> sum: (xs: List[String])String
sum3(List("Hello,"," how are you"))                //> res0: String = Hello, how are you

/*
按这样推敲，我们可以对任何类型A进行sum操作，只要用一个类型参数的trait就行了：
*/
trait Monoid[A] {
  def mappend(a1: A, a2: A): A
  def mzero: A
}
// 注意具体的操作mappend和起始值都没有定义，这个会留待trait Monoid各种类型的实例里：


object h11 {
  trait Monoid[A] {
    def mappend(a1: A, a2: A): A
    def mzero: A
  }
  object intMonoid extends Monoid[Int]{
    def mappend(i1: Int, i2: Int): Int = i1 + i2
    def mzero = 0
  }
  object stringMonoid extends Monoid[String]{
    def mappend(s1: String, s2: String): String = s1 + s2
    def mzero = ""
  }

   def sum[A](xs: List[A])(m: Monoid[A]): A = xs.foldLeft(m.mzero)(m.mappend)
                                                     //> sum: [A](xs: List[A])(m: scalaz.learn.ex2.Monoid[A])A
   sum(List(1,2,3))(intMonoid)                       //> res0: Int = 6
   sum(List("Hello,"," how are you"))(stringMonoid)  //> res1: String = Hello, how are you
}

h11


// 现在这个sum是不是概括的多了。现在我们可以利用implicit使sum的调用表达更精炼：
object h12 {
  trait Monoid[A] {
    def mappend(a1: A, a2: A): A
    def mzero: A
  }
  implicit object intMonoid extends Monoid[Int]{
    def mappend(i1: Int, i2: Int): Int = i1 + i2
    def mzero = 0
  }
  implicit object stringMonoid extends Monoid[String]{
    def mappend(s1: String, s2: String): String = s1 + s2
    def mzero = ""
  }

  def sum[A](xs: List[A])(implicit m: Monoid[A]): A = xs.foldLeft(m.mzero)(m.mappend)
  //> sum: [A](xs: List[A])(m: scalaz.learn.ex2.Monoid[A])A
  sum(List(1,2,3))// (intMonoid)                       //> res0: Int = 6
  sum(List("Hello,"," how are you"))//(stringMonoid)  //> res1: String = Hello, how are you
}
h12


/*
现在调用sum是不是贴切多了？按照scalaz的惯例，我们把implicit放到trait的companion object里：
*/
object h13 {
  trait Monoid[A] {
    def mappend(a1: A, a2: A): A
    def mzero: A
  }
  object Monoid {
    implicit object intMonoid extends Monoid[Int]{
      def mappend(i1: Int, i2: Int): Int = i1 + i2
      def mzero = 0
    }
    implicit object stringMonoid extends Monoid[String]{
      def mappend(s1: String, s2: String): String = s1 + s2
      def mzero = ""
    }
  }
}

/*
这样，用户可以定义自己的Monoid实例在sum中使用。
但现在这个sum还是针对List的。我们必须再进一步概括到任何M[_]。我们先把用一个针对List的foldLeft实例来实现sum：
*/

object h14 {
  object listFoldLeft {
    def foldLeft[A, B](xs: List[A])(b: B)(f: (B, A) => B): B = xs.foldLeft(b)(f)
  }

  def sum[A](xs: List[A])(implicit m: Monoid[A]): A = listFoldLeft.foldLeft(xs)(m.mzero)(m.mappend)
}


object h15 {
  import h13.Monoid._
  trait FoldLeft[M[_]] {
    def foldLeft[A, B](xs: M[A])(b: B)(f: (B, A) => B): B
  }

  object FoldLeft {
    implicit object listFoldLeft extends FoldLeft[List] {
      override def foldLeft[A, B](xs: List[A])(b: B)(f: (B, A) => B): B = xs.foldLeft(b)(f)
    }
  }

/*
现在这个sum[M[_],A]是个全面概括的函数了。上面的sum也可以这样表达：
 */
  def sum[M[_], A](xs: M[A])(implicit m: h13.Monoid[A], fl: FoldLeft[M]): A = fl.foldLeft(xs)(m.mzero)(m.mappend)

  sum(List(1, 2, 3, 4))
  sum(List("hello", "how are you"))


  def sum1[A: h13.Monoid, M[_]: FoldLeft](xs: M[A]): A = {
    val m = implicitly[Monoid[A]]
    val fl = implicitly[FoldLeft[M]]
    fl.foldLeft(xs)(m.mzero)(m.mappend)
  }
  sum1(List(1, 2, 3, 4))
}


/*
在scalaz里为每个类型提供了足够的操作符号。使用这些符号的方式与普通的操作符号没有两样如 a |+| b，这是infix符号表述形式。scalaz会用方法注入（method injection）方式把这些操作方法集中放在类型名称+后缀op的trait里如，MonoidOp。

假如我想为所有类型提供一个操作符|+|，然后用 a |+| b这种方式代表plus(a,b)，那么我们可以增加一个Monoid的延伸trait：MonoidOp，再把这个|+|放入：
*/

trait MonoidOp[A] {
  val M: Monoid[A]
  val a1: A
  def |+|(a2: A) = M.mappend(a1, a2)
}

// 现在可以用infix方式调用|+|如 a |+| b。下一步是用implicit把这个|+|方法加给任何类型A:
implicit def toMonoidOp[A: Monoid](a: A) = new MonoidOp[A] {
  val M = implicitly[Monoid[A]]
  val a1 = a
}

/*
以上所见，implicit toMonoidOp的意思是对于任何类型A，如果我们能找到A类型的Monoid实例，那么我们就可以把类型A转变成MonoidOp类型，然后类型A就可以使用操作符号|+|了。现在任何类型具备Monoid实例的类型都可以使用|+|符号了
 */
1 |+| 2
"hello" |+| "world"