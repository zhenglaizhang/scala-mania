package net.zhenglai.ds

/*
泛函编程风格中最重要的就是对一个管子里的元素进行操作。这个管子就是这么一个东西：F[A]，我们说F是一个针对元素A的高阶类型，其实F就是一个装载A类型元素的管子，A类型是相对低阶，或者说是基础的类型。泛函编程风格就是在F内部用对付A类的函数对里面的元素进行操作。

前面Stream设计章节里，我们采用了封装形式的数据结构设计，把数据结构uncons放进了特质申明里

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
  /*
用tuple(A, Stream[A])来代表一个完整的Stream并把它放进一个Option里，本意是空的Stream就可以用None来表示。这个Option就像是那个附加的套子把我们的目标类型(A, Stream[A])套成了F[A]类型。其实我们的目的是对管子里的A类型进行操作，特别是对A类型元素进行模式匹配。但是在之前的设计里我们却对F[A]这个戴着套子的类型进行了模式匹配。
   */
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

  //戴着套子进行模式匹配
  def toList: List[A] = uncons match {
    case scala.None => Nil
    case scala.Some((h, t)) => h :: t.toList
  }

  /*
在前面曾经为Option编写了这个函数：(oa:Option[A]).map[B](f: A => B): Option[B]。我们可以向map传入一个操作A级别类型的函数，比如一段A级别类型的模式匹配方式代码。Option map返回的结果是Option[B]，是一个高阶类型，但我们可以很方便的用getOrElse来取得这个返回Option里面的元素。

通过使用map，用元素类型级别模式匹配，然后用getOrElse取出。Stream为空时采用getOrElse默认值。可以让代码更简洁易名。
   */
  def toList2: List[A] = uncons map {
    case (h, t) => h :: t.toList
  } getOrElse Nil

  //  def toList = toListFast

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

  //戴着套子
  def take(n: Int): Stream[A] = n match {
    case 0 => Stream.empty
    case _ => uncons match {
      case scala.None => Stream.empty
      case scala.Some((h, t)) => Stream.cons(h, t.take(n - 1))
    }
  }

  // use map
  def take2(n: Int): Stream[A] = n match {
    case 0 => Stream.empty
    case _ => uncons map {
      case (h, t) => Stream.cons(h, t.take2(n - 1))
    } getOrElse Stream.empty
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

  def takeWhile2(f: A => Boolean): Stream[A] = {
    uncons map {
      case (h, t) => if (f(h)) Stream.cons(h, t.takeWhile2(f)) else Stream.empty
    } getOrElse Stream.empty
  }

  import Stream._

  import scala.Some

  def dropWhile(f: A => Boolean): Stream[A] = {
    uncons match {
      case scala.None => empty
      case Some((h, t)) => if (f(h)) t.dropWhile(f) else t
    }
  }

  def headOption: scala.Option[A] = uncons match {
    case scala.Some((h, t)) => scala.Some(h)
    case _ => scala.None
  }

  /*
  List的折叠算法无法着中途跳出，而Stream通过“延后计算”（lazy evaluation）是可以实现提早终结计算的。我们先看看Stream的右折叠（foldRight）算法
  由于op的第二个参数B是延后计算的，那么t.foldRight(z)(op)这个表达式的计算就是延后的，系统可以决定先不计算这个表达式从而得到了一个中间停顿的结果。

  //高阶类型操作
   */
  def foldRight[B](z: B)(op: (A, => B) => B): B = uncons match {
    case scala.None => z
    case scala.Some((h, t)) => op(h, t.foldRight(z)(op))
  }

  // monadic style
  def foldRigh2[B](z: B)(op: (A, => B) => B): B = uncons map {
    case (h, t) => op(h, t.foldRigh2(z)(op))
  } getOrElse z

  /*
  注意：当p(a)=true时系统不再运算b，所以整个运算停了下来。
   */
  def exists(p: A => Boolean): Boolean = {
    foldRight(false) { (a, b) => p(a) || b }
  }

  // 同样，用foldRight来实现forAll：
  def forAll(p: A => Boolean): Boolean = {
    foldRight(true) { (a, b) => p(a) && b }
  }

  // 把两个Stream连接起来
  def append[B >: A](b: Stream[B]): Stream[B] = {
    uncons match {
      case scala.None => b
      case Some((h, t)) => cons(h, t.append(b))
    }
  }

  // append symbol representation
  def #++[B >: A](b: Stream[B]): Stream[B] = append(b)

  /*
  当我们遇到数据结构只能存一个元素如Option，Either时我们用map2来对接两个结构。当我们遇到能存多个元素的数据结构如List，Tree时我们就会用append来对接。Stream是一个多元素的数据结构，我们需要实现append：
   */

  def map[B](f: A => B): Stream[B] = uncons match {
    case scala.None => empty[B]
    case scala.Some((h, t)) => cons(f(h), t.map(f))
  }

  /*
S类型即uncons类型>>>Option[(A, Stream[A])], uncons的新状态是 Some((t.head, t.tail))。因为我们采用了数据结构嵌入式的设计，所以必须用uncons来代表Stream，它的下一个状态就是Some((t.head, t.tail))。如果使用子类方式Cons(h,t)，那么下一个状态就可以直接用t来表示，简洁多了。
   */
  def mapByUnfoldInfinite[B](f: A => B): Stream[B] = Stream.unfold(uncons) {
    case Some((h, t)) => scala.Some((f(h), Some((t.head, t.tail))))
    case _ => scala.None
  }

  def head: A = uncons match {
    case scala.None => throw new NoSuchElementException("head of empty stream")
    case scala.Some((h, _)) => h
  }

  def tail: Stream[A] = uncons match {
    case scala.Some((h, t)) => t
    case _ => Stream.empty
  }

  //用递归算法
  def flatMap[B](f: A => Stream[B]): Stream[B] = {
    uncons match {
      case scala.None => empty
      case scala.Some((h, t)) => f(h) #++ t.flatMap(f)
    }
  }

  //用foldRight实现
  def flatMap_1[B](f: A => Stream[B]): Stream[B] = {
    foldRight(empty[B]) { (h, t) => f(h) #++ t }
  }

  //用递归算法
  def filter(p: A => Boolean): Stream[A] = {
    uncons match {
      case scala.None => empty
      case scala.Some((h, t)) => if (p(h)) cons(h, t.filter(p)) else t.filter(p)
    }
  }

  //用foldRight实现
  def filter_1(p: A => Boolean): Stream[A] = {
    foldRight(empty[A]) { (h, t) => if (p(h)) cons(h, t) else t }
  }

  //#:: is the operation symbol for cons
  def #::[B >: A](h: => B): Stream[B] = cons(h, this)

  def takeByUnfold(n: Int): Stream[A] = {
    unfold((uncons, n)) {
      case (Some((h, t)), k) if (k > 0) => Some(h, (Some((t.head, t.tail)), k - 1))
      case _ => scala.None
    }
  }
  def takeWhileByUnfold(f: A => Boolean): Stream[A] = {
    unfold(uncons) {
      case Some((h, t)) if (f(h)) => Some(h, Some((t.head, t.tail)))
      case _ => scala.None
    }
  }
  def filterByUnfold(f: A => Boolean): Stream[A] = {
    unfold(uncons) {
      case Some((h, t)) if (f(h)) => Some(h, Some((t.head, t.tail)))
      case _ => scala.None
    }
  }
  def zipWithByUnfold[B, C](b: Stream[B])(f: (A, B) => C): Stream[C] = {
    unfold((uncons, b.uncons)) {
      case (Some((ha, ta)), Some((hb, tb))) => Some(f(ha, hb), (Some((ta.head, ta.tail)), Some((tb.head, tb.tail))))
      case _ => scala.None
    }
  }
  def zip[B](b: Stream[B]): Stream[(A, B)] = zipWithByUnfold(b) { (_, _) }

  /*
乍看起来好像挺复杂，但尝试去理解代码的意义，上面一段代码会更容易理解一点。 中间插播了一段map,flatMap的示范，目的是希望在后面的设计思考中向泛函编程风格更靠近一点。
   */
  def zipWithByUnfoldWithMap[B, C](b: Stream[B])(f: (A, B) => C): Stream[C] = {
    //起始状态是tuple(Stream[A],Stream[B])，状态转换函数>>> （s1,s2) => Option(a, (s1,s2))
    unfold((this, b)) { s =>
      {
        for {
          a <- s._1.uncons //用flatMap从Option[(A,Stream[A])]取出元素 >>> (A,Stream[A])
          b <- s._2.uncons //用flatMap从Option[(B,Stream[B])]取出元素 >>> (B,Stream[B])
        } yield {
          (f(a._1, b._1), (a._2, b._2)) //返回新的状态：C >>> (f(a,b),(ta,tb))
        }
      }
    }
  }

  def mapByUnfoldWithMap[B](f: A => B): Stream[B] = {
    unfold(this) { s =>
      this.uncons map {
        case (h, t) => (f(h), t)
      }
    }
  }
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

  // unfold是一个最通用的Stream构建函数（stream builder）
  /*
unfold的工作原理模仿了一种状态流转过程：z是一个起始状态，代表的是一个类型的值。然后用户（caller）再提供一个操作函数f。f的款式是：S => Option[(A,S)]，意思是接受一个状态，然后把它转换成一对新的A值和新的状态S，再把它们放入一个Option。如果Option是None的话，这给了用户一个机会去终止运算，让unfold停止递归。从unfold的源代码可以看到f(z) match {} 的两种情况。需要注意的是函数f是针对z这个类型S来操作的，A类型是Stream［A]的元素类型。f的重点作用在于把S转换成新的S。
   */
  def unfold[A, S](z: S)(f: S => scala.Option[(A, S)]): Stream[A] = f(z) match {
    case scala.None => Stream.empty
    case scala.Some((h, s)) => cons(h, unfold(s)(f))
  }

  def unfoldWithMap[A, S](z: S)(f: S => scala.Option[(A, S)]): Stream[A] = {
    f(z) map {
      case (a, s) => cons(a, unfold(s)(f))
    } getOrElse empty
  }

  def constByUnfold[A](x: A): Stream[A] = Stream.unfold(x)(_ => scala.Some(x, x))
}