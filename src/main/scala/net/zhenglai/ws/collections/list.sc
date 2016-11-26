/*
List就像一个管子，里面可以装载一长条任何类型的东西。如需要对管子里的东西进行处理，则必须在管子内按直线顺序一个一个的来，这符合泛函编程的风格。与其它的泛函数据结构设计思路一样，设计List时先考虑List的两种状态：空或不为空两种类型。
 */

object h1 {

  sealed trait List[+A] {
    def sum: Int = this match {
      case Nil                        => 0
      case Cons(h: Int, t: List[Int]) => h + t.sum
    }

    def head: A = this match {
      case Nil        => sys.error("Empty List!")
      case Cons(h, t) => h
    }

    def tail: List[A] = this match {
      case Nil        => sys.error("Empty List!")
      case Cons(h, t) => t
    }

    def take(n: Int): List[A] = n match {
      case k if k < 0 => sys.error("index < 0")
      case 0          => Nil
      case _ => this match {
        case Nil        => Nil
        case Cons(h, t) => Cons(h, t.take(n - 1))
      }
    }

    def takeWhile(f: A => Boolean): List[A] = this match {
      case Nil        => Nil
      case Cons(h, t) => if (f(h)) Cons(h, t.takeWhile(f)) else Nil
    }

    def drop(n: Int): List[A] = n match {
      case k if k < 0 => sys.error("index < 0")
      case 0          => this
      case _ => this match {
        case Nil        => Nil
        case Cons(h, t) => t.drop(n - 1)
      }
    }

    def dropWhile(f: A => Boolean): List[A] = this match {
      case Nil        => Nil
      case Cons(h, t) => if (f(h)) t.dropWhile(f) else this
    }

    def sum[B >: A](z: B)(f: (B, B) => B): B = this match {
      case Nil        => z
      //      case Cons(h: Int, t: List[Int]) => f(h, t.sum(z)(f))
      case Cons(h, t) => f(h, t.sum(z)(f))
    }

    def ++[B >: A](xs: List[B]): List[B] = this match {
      case Nil        => xs
      case Cons(h, t) => Cons(h, t.++(xs))
    }

    def init: List[A] = this match {
      case Nil          => sys.error("Empty list")
      case Cons(_, Nil) => Nil
      case Cons(h, t)   => Cons(h, t.init)
    }

    def length: Int = this match {
      case Nil        => 0
      case Cons(h, t) => 1 + t.length
    }

    // map flatMap filter => for comprehension (Functor, Applicative, Monad)
    def map[B](f: A => B): List[B] = this match {
      case Nil        => Nil
      case Cons(h, t) => Cons(f(h), (t map f))
    }

    def flatMap[B](f: A => List[B]): List[B] = this match {
      case Nil        => Nil
      case Cons(h, t) => f(h) ++ (t flatMap f)
    }

    def filter(f: A => Boolean): List[A] = this match {
      case Nil        => Nil
      case Cons(h, t) => if (f(h)) Cons(h, t.filter(f)) else t.filter(f)
    }

    /*
    折叠算法是List的典型算法。通过折叠算法可以实现众多函数组合（function composition）。所以折叠算法也是泛函编程里的基本组件（function combinator）。了解折叠算法的原理对了解泛函组合有着至关紧要的帮助。折叠算法又可分右折叠和左折叠。我们先从右折叠（foldRight）开始:

对List(a,b,c)的右折叠算法：op(a,op(b,op(c,z))) 可以看出括号是从右开始的。计算方式如图二：op(a,sub), sub是重复子树，可以肯定要用递归算法。这里z代表了一个起始值。我们现在可以推算出foldRight的函数款式（function signature）了

foldRight不是一个尾递归算法（tail recursive）

// (List(x1,x2,x3...x{n-1}, xn) foldRight acc) op => x1 op (...(xn op acc)...)
2  // foldRight(Cons(1,Cons(2,Cons(3,Nil))), 0) {_ + _}
3  // 1 + foldRight(Cons(2,Cons(3,Nil)), 0) {_ + _}
4  // 1 + (2 + foldRight(Cons(3,Nil), 0) {_ + _})
5  // 1 + (2 + (3 + foldRight(Nil, 0) {_ + _}))
6  // 1 + (2 + (3 + 0)) = 6
     */
    def foldRight[B](z: B)(op: (A, B) => B): B = this match {
      case Nil        => z
      case Cons(h, t) => op(h, t.foldRight(z)(op))
    }

    /*
左折叠算法就是所有List元素对z的操作op。从图二可见，op对z,a操作后op的结果再作为z与b再进行op操作，如此循环。看来又是一个递归算法，而z就是一个用op累积的值了：op(op(op(z,a),b),c)。左折叠算法的括号是从左边开始的。
     */

    def foldLeft[B](z: B)(op: (B, A) => B): B = {
      @annotation.tailrec
      def foldL[B](l: List[A], z: B)(op: (B, A) => B): B = l match {
        case Nil        => z
        case Cons(h, t) => foldL(t, op(z, h))(op)
      }

      foldL(this, z)(op)
    }

    /*
    除foldRight,foldLeft之外，折叠算法还包括了：reduceRight,reduceLeft,scanRight,scanLeft。

    reduceLeft是以第一个，reduceRight是以最后一个List元素作为起始值的折叠算法，没有单独的起始值：
     */
    // notice the B >: A bounds!!
    def reduceLeft[B >: A](op: (B, A) => B): B = this match {
      case Nil        => sys.error("Empty list")
      case Cons(h, t) => t.foldLeft[B](h)(op)
    }

    def reduceRight[B >: A](op: (A, B) => B): B = this match {
      case Nil          => throw new UnsupportedOperationException("empty.reduceLeft")
      case Cons(h, Nil) => h
      case Cons(h, t)   => op(h, t.reduceRight(op))
    }

    /*
    scanLeft, scanRight 分别把每次op的结果插入新产生的List作为返回结果。
     */

    // TODO
    //    def scanLeft[B >: A](z: B)(op: (B, A) => B): List[B] = this match {
    //      case Nil => Cons(z, Nil)
    //      case Cons(h, t) => Cons(z, t.scanLeft(op(z, h)(op)))
    //    }
  }

  case class Cons[+A](override val head: A, override val tail: List[A]) extends List[A]

  case object Nil extends List[Nothing]

  object List {
    def apply[A](xs: A*): List[A] = {
      if (xs.isEmpty) Nil
      else Cons(xs.head, apply(xs.tail: _*))
    }
  }

  /*
  以上是一个可以装载A类型元素的List，是一个多态的类型（Polymorphic Type）。+A表示List是协变（Covariant）的，意思是如果apple是fruit的子类（subtype）那么List[apple]就是List[fruit]的子类。Nil继承了List[Nothing],Nothing是所有类型的子类。结合协变性质，Nil可以被视为List[Int],List[String]...
   */
}

object h2 {

  /*
  代码中empty,cons两个方法可以实现List的两个状态。
   */
  trait List[+A] {
    def node: Option[(A, List[A])]

    def isEmpty = node.isEmpty
  }

  object List {
    def empty[A] = new List[A] {
      def node = None
    }

    def cons[A](head: A, tail: List[A]) = new List[A] {
      def node = Some(head -> tail)
    }
  }

}

var xs = Array(1, 2, 3)
xs.head
xs.tail

import h1._
val li = List(1, 2, 3)
val ls = List("one", "two", "three")

val li1 = Cons(1, Cons(2, Cons(3, Nil)))

li.sum

li.sum(1)(_ + _)
ls.sum("hello ")(_ + _)

li.take(1)
li.take(2)
li.take(3)
li.take(0)

"-" * 20

List(1, 2, 3).head //> res0: Int = 1
List(1, 2, 3).tail //> res1: ch3.list.List[Int] = Cons(2,Cons(3,Nil))
List(1, 2, 3).take(2) //> res2: ch3.list.List[Int] = Cons(1,Cons(2,Nil))
List(1, 2, 3).takeWhile(x => x < 3) //> res3: ch3.list.List[Int] = Cons(1,Cons(2,Nil))
List(1, 2, 3) takeWhile { _ < 3 } //> res4: ch3.list.List[Int] = Cons(1,Cons(2,Nil))
List(1, 2, 3).drop(2) //> res5: ch3.list.List[Int] = Cons(3,Nil)
List(1, 2, 3).dropWhile(x => x < 3) //> res6: ch3.list.List[Int] = Cons(3,Nil)
List(1, 2, 3) dropWhile { _ < 3 } //> res7: ch3.list.List[Int] = Cons(3,Nil)

List(1, 2, 3) ++ List(4, 5, 6)

List(1, 2, 3).length
List(1, 2, 3).init

List(1, 2, 3).map(_ + 10)
List(1, 2, 3).flatMap(x => List(x + 10))
List(1, 2, 3).filter(_ != 2)

List(1, 2, 3).foldRight(0)(_ + _)
List(1, 2, 3).foldRight(1) { _ * _ }

List(1, 2, 3).foldRight(Nil: List[Int]) { (a, z) => Cons(a + 10, z) }
/*
注意以上的起始值1和Nil:List[Int]。z的类型可以不是A，所以op的结果也有可能不是A类型，但在以上的加法和乘法的例子里z都是Int类型的。但在List重构例子里z是List[Int]类型，所以op的结果也是List[Int]类型的，这点要特别注意。
 */
List(1, 2, 3).foldLeft(0)(_ + _)
List(1, 2, 3).foldLeft(1) { _ * _ }

// res29: h1.List[Int] = Cons(13,Cons(12,Cons(11,Nil)))
List(1, 2, 3).foldLeft(Nil: List[Int]) { (z, a) => Cons(a + 10, z) }

collection.immutable.List(1, 2, 3).scanLeft(0)(_ + _)
collection.immutable.List(1, 2, 3).scanRight(0)(_ + _)

// http://www.cnblogs.com/tiger-xc/p/4330727.html