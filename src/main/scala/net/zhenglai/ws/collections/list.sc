/*
List就像一个管子，里面可以装载一长条任何类型的东西。如需要对管子里的东西进行处理，则必须在管子内按直线顺序一个一个的来，这符合泛函编程的风格。与其它的泛函数据结构设计思路一样，设计List时先考虑List的两种状态：空或不为空两种类型。
 */

object h1 {

  sealed trait List[+A] {
    def sum: Int  = this match {
      case Nil => 0
      case Cons(h: Int, t: List[Int]) => h + t.sum
    }

    def head: A = this match {
      case Nil => sys.error("Empty List!")
      case Cons(h, t) => h
    }

    def tail: List[A] = this match {
      case Nil => sys.error("Empty List!")
      case Cons(h, t) => t
    }

    def take(n: Int): List[A] = n match {
      case k if k < 0 => sys.error("index < 0")
      case 0 => Nil
      case _ => this match {
        case Nil => Nil
        case Cons(h, t) => Cons(h, t.take(n - 1))
      }
    }

    def takeWhile(f: A => Boolean): List[A] = this match {
      case Nil => Nil
      case Cons(h, t) => if (f(h)) Cons(h, t.takeWhile(f)) else Nil
    }

    def drop(n: Int): List[A] = n match {
      case k if k < 0 => sys.error("index < 0")
      case 0 => this
      case _ => this match {
        case Nil => Nil
        case Cons(h, t) => t.drop(n-1)
      }
    }

    def dropWhile(f: A => Boolean): List[A] = this match {
      case Nil => Nil
      case Cons(h, t) => if (f(h)) t.dropWhile(f) else this
    }

    def sum[B >: A](z: B)(f: (B, B) => B): B = this match {
      case Nil => z
//      case Cons(h: Int, t: List[Int]) => f(h, t.sum(z)(f))
      case Cons(h, t) => f(h, t.sum(z)(f))
    }

    def ++[B >: A](xs: List[B]): List[B] = this match {
      case Nil => xs
      case Cons(h, t) => Cons(h, t.++(xs))
    }

    def init: List[A] = this match {
      case Nil => sys.error("Empty list")
      case Cons(_, Nil) => Nil
      case Cons(h, t) => Cons(h, t.init)
    }

    def length: Int = this match {
      case Nil => 0
      case Cons(h, t) => 1 + t.length
    }


    // map flatMap filter => for comprehension (Functor, Applicative, Monad)
    def map[B](f: A => B): List[B] = this match {
      case Nil => Nil
      case Cons(h, t) => Cons(f(h), (t map f))
    }

    def flatMap[B](f: A => List[B]): List[B] = this match {
      case Nil => Nil
      case Cons(h, t) => f(h) ++ (t flatMap f)
    }

    def filter(f: A => Boolean): List[A] = this match {
      case Nil => Nil
      case Cons(h, t) => if (f(h)) Cons(h, t.filter(f)) else t.filter(f)
    }
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

List(1,2,3).head                                  //> res0: Int = 1
List(1,2,3).tail                                  //> res1: ch3.list.List[Int] = Cons(2,Cons(3,Nil))
List(1,2,3).take(2)                               //> res2: ch3.list.List[Int] = Cons(1,Cons(2,Nil))
List(1,2,3).takeWhile(x => x < 3)                 //> res3: ch3.list.List[Int] = Cons(1,Cons(2,Nil))
List(1,2,3) takeWhile {_ < 3}                     //> res4: ch3.list.List[Int] = Cons(1,Cons(2,Nil))
List(1,2,3).drop(2)                               //> res5: ch3.list.List[Int] = Cons(3,Nil)
List(1,2,3).dropWhile(x => x < 3)                 //> res6: ch3.list.List[Int] = Cons(3,Nil)
List(1,2,3) dropWhile {_ < 3}                     //> res7: ch3.list.List[Int] = Cons(3,Nil)


List(1, 2, 3) ++ List(4, 5, 6)

List(1, 2, 3).length
List(1, 2, 3).init



List(1, 2, 3).map(_ + 10)
List(1, 2, 3).flatMap(x => List(x + 10))
List(1, 2, 3).filter(_ != 2)
