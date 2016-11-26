import scala.annotation.tailrec
/*
Defensive copying is not needed, because the list is immutable.


 */

def append[A](a1: List[A], a2: List[A]): List[A] = a1 match {
  case Nil          => a2
  case head +: tail => head +: append(tail, a2)
}

append(List(1, 2, 3), List(4, 5, 6))

@tailrec
def drop[A](l: List[A], n: Int): List[A] = {
  if (n == 0) l
  else l match {
    case Nil          => Nil
    case head +: tail => drop(tail, n - 1)
  }
}

drop(List(1, 2, 3), 1)
drop(List(1, 2, 3), 3)
drop(List(1, 2, 3), 10)

def dropWhile[A](xs: Seq[A])(p: A => Boolean): Seq[A] = xs match {
  case head +: tail if (p(head)) => dropWhile(tail)(p)
  case _                         => xs
}

dropWhile(Seq(1, 2, 3))(_ < 3)

def foldRight[A, B](xs: Seq[A], z: B)(f: (A, B) => B): B = xs match {
  case Nil          => z
  case head +: tail => f(head, foldRight(tail, z)(f))
}

Seq(1, 2, 3).foldLeft(2)(_ + _)

def map[A, B](xs: Seq[A])(f: A => B): Seq[B] = xs match {
  case Nil          => Nil
  case head +: tail => f(head) +: map(tail)(f)
}

map(Seq(1, 2, 3, 4))(_ * 2)

/*
 编程即是编制对数据进行运算的过程。特殊的运算必须用特定的数据结构来支持有效运算

 泛函编程使用泛函数据结构（Functional Data Structure）来支持泛函程序。泛函数据结构的特点是”不可变特性“（Immutability）, 是泛函编程中函数组合（composition）的必需。
 泛函数据结构及运算方法具备以下特征：

1、不可变特性（Immutable）

2、运算在数据结构内进行。尽量避免使用中间变量

3、运算返回新的数据结构作为结果

 */

// OO style
var arr = Array(1, 2, 3)
var sum = arr(0) + arr(1) + arr(2)
// 以上运算是需要中间变量的。而且是在结构外进行的

val arr1 = Array(1, 2, 3)

// 泛函运算直接在数据结构内进行，不需要中间变量。
val sum1 = arr1.sum

// arr1是赋值后新的数据结构。arr没有变化。这样我们可以放心使用arr来进行函数组合了。
val arr2 = arr1 map { x => if (x == 1) sum else x }
arr2 foreach println
/*
arr1先复制了arr内的数据后再修改内容，所以arr没有变。这样理解有对也有不对：从效果来说arr1是复制了arr。但从具体做法上系统只是把arr(0)下面节点的指针指向了arr1(0)，并没有进行实质的数据复制。
 */
