package net.zhenglai.ds

/*
其一，我们可以发现所有List的操作都是在内存中进行的，要求List中的所有元素都必须在操作时存在于内存里。如果必须针对大型数据集进行List操作的话就明显不切实际了。其二，List的抽象算法如折叠算法、map, flatMap等是无法中途跳出的，无论如何都一直进行到底；只有通过递归算法在才能在中途停止运算。但递归算法不够抽象，经常出现重复的代码。最要命的是递归算法会随着数据量增加堆栈内存占用（non-tail-recursive），处理大型数据集同样不实际。以上缺陷使List的应用被局限在小规模的数据集处理范围。

List由于内存占用问题不适合大数据集处理，但它的计算模式又是排列数据模式必须的选择。Stream数据类型具备了List的排列数据计算模式但有不需要将全部数据搬到内存里，可以解决以上提到的大数据集处理问题。Stream的特性是通过“延后计算”（lazy evaluation）来实现的。可以想象一下可能的原理：Stream内元素读取是在具体使用时才进行的。不用说，Stream是典型的只读数据类型。既然要继承List的计算模式，

实际上Stream就是对一个List的描述，一个类型的声明。它的实例生成延后到了具体使用的时候，此时需要的元素已经搬入内存，成了货真价实的List了：
 */
//sealed trait Stream[+A]
//
//
//object Stream {
//
//}
//
//case object Empty extends Stream[Nothing] {
//}
//
//
///*
//A nonempty stream consists of a head and a tail, which are both non-strict. Due to technical limitations, these are thunks that must be explicitly forced, rather than by-name parameters.
//
//活脱脱的List结构嘛。不过Stream的头元素（head）和无头尾（tail）是延后计算的（non-strict）。由于Cons不是普通函数而是一个类，不容许延后计算类参数，所以传入的是一个函数 () => ???
//// `val' parameters may not be call-by-name
// */
//case class Cons[+A](head: () => A, tail: () => Stream[A]) extends Stream[A]


trait Stream[+A] {
  def uncons: scala.Option[(A, Stream[A])]

  def isEmpty: Boolean = uncons.isEmpty

  def toList_1: List[A] = {
    @annotation.tailrec
    def go(s: Stream[A], acc: List[A]): List[A] = {
      s.uncons match {
        case scala.None => acc
        case scala.Some((h, t)) => go(t, h :: acc)
      }
    }

    go(this, Nil).reverse
  }

  def toList = toListFast

  def toListFast: List[A] = {
    val buf = new collection.mutable.ListBuffer[A]

    @annotation.tailrec
    def go(s: Stream[A]): List[A] = {
      s.uncons match {
        case scala.Some((h, t)) => {
          buf += h
          go(t)
        }
        case _ => buf.toList
      }
    }

    go(this)
  }

  def take(n: Int): Stream[A] = n match {
    case 0 => Stream.empty
    case _ => uncons match {
      case scala.None => Stream.empty
      case scala.Some((h, t)) => Stream.cons(h, t.take(n - 1))
    }
  }

  def drop(n: Int): Stream[A] = n match {
    case 0 => this
    case _ => uncons match {
      case scala.None => this
      case scala.Some((h, t)) => t.drop(n - 1)
    }
  }

  def takeWhile(f: A => Boolean): Stream[A] = uncons match {
    case scala.None => Stream.empty
    case scala.Some((h, t)) => if (f(h)) Stream.cons(h, t.takeWhile(f)) else Stream.empty
  }

  import Stream._

  import scala.Some
  def dropWhile(f: A => Boolean): Stream[A] = {
    uncons match {
      case scala.None => empty
      case Some((h,t)) => if ( f(h) ) t.dropWhile(f) else t
    }
  }
  def headOption: scala.Option[A] = uncons match {
    case scala.Some((h,t)) => scala.Some(h)
    case _ => scala.None
  }
  def tail: Stream[A] = uncons match {
    case scala.Some((h,t)) => t
    case _ => Stream.empty
  }

  /*
  List的折叠算法无法着中途跳出，而Stream通过“延后计算”（lazy evaluation）是可以实现提早终结计算的。我们先看看Stream的右折叠（foldRight）算法
  由于op的第二个参数B是延后计算的，那么t.foldRight(z)(op)这个表达式的计算就是延后的，系统可以决定先不计算这个表达式从而得到了一个中间停顿的结果。
   */
  def foldRight[B](z: B)(op: (A, => B) => B): B = uncons match {
    case scala.None => z
    case scala.Some((h, t)) => op(h, t.foldRight(z)(op))
  }

  /*
  注意：当p(a)=true时系统不再运算b，所以整个运算停了下来。
   */
  def exists(p: A => Boolean): Boolean = {
    foldRight(false){(a,b) => p(a) || b }
  }

  // 同样，用foldRight来实现forAll：
  def forAll(p: A => Boolean): Boolean = {
             foldRight(true){(a,b) => p(a) && b}
         }

  /*
  当我们遇到数据结构只能存一个元素如Option，Either时我们用map2来对接两个结构。当我们遇到能存多个元素的数据结构如List，Tree时我们就会用append来对接。Stream是一个多元素的数据结构，我们需要实现append：
   */

  // 把两个Stream连接起来
  def append[B >: A](b: Stream[B]): Stream[B] = {
    uncons match {
      case scala.None => b
      case Some((h, t)) => cons(h, t.append(b))
    }
  }

  // append symbol representation
  def #++[B >: A](b: Stream[B]): Stream[B] = append(b)

  def map[B](f: A => B): Stream[B] = uncons match {
    case scala.None => empty[B]
    case scala.Some((h, t)) => cons(f(h), t.map(f))
  }

  //用递归算法
  def flatMap[B](f: A => Stream[B]): Stream[B] = {
    uncons match {
      case scala.None => empty
      case scala.Some((h,t)) => f(h) #++ t.flatMap(f)
    }
  }

  //用foldRight实现
  def flatMap_1[B](f: A => Stream[B]): Stream[B] = {
    foldRight(empty[B]){(h,t) => f(h) #++ t}
  }
  //用递归算法
  def filter(p: A => Boolean): Stream[A] = {
    uncons match {
      case scala.None => empty
      case scala.Some((h,t)) => if(p(h)) cons(h,t.filter(p)) else t.filter(p)
    }
  }
  //用foldRight实现
  def filter_1(p: A => Boolean): Stream[A] = {
    foldRight(empty[A]){(h,t) => if(p(h)) cons(h,t) else t}
  }

  //#:: is the operation symbol for cons
  def #::[B >: A](h: => B): Stream[B] = cons(h, this)
}

/*
设计方案采用了结构封装形式：数据结构uncons，两种状态empty, cons都被封装在类结构里。最起码我们现在可以直接使用=> A 来表达延后计算参数了。
实际上Stream就是对一个List的描述，一个类型的声明。它的实例生成延后到了具体使用的时候，此时需要的元素已经搬入内存，成了货真价实的List了
 */
object Stream {
  def empty[A]: Stream[A] = new Stream[A] {
    def uncons = scala.None
  }

  // Stream的操作也都是对操作的描述，是延后计算的。当元素被搬到List时系统才回真正计算这些Stream元素的值。
  def cons[A](h: => A, t: => Stream[A]): Stream[A] = new Stream[A] {
    def uncons = scala.Some((h, t))
  }


  def apply[A](as: A*): Stream[A] = {
    if (as.isEmpty) empty
    else cons(as.head, apply(as.tail: _*))
  }
}