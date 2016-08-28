/*
 泛函编程方式其中一个特点就是普遍地使用递归算法，而且有些地方还无法避免使用递归算法。比如说flatMap就是一种推进式的递归算法，没了它就无法使用for-comprehension，那么泛函编程也就无法被称为Monadic Programming了。虽然递归算法能使代码更简洁易明，但同时又以占用堆栈（stack）方式运作。堆栈是软件程序有限资源，所以在使用递归算法对大型数据源进行运算时系统往往会出现StackOverflow错误。如果不想办法解决递归算法带来的StackOverflow问题，泛函编程模式也就失去了实际应用的意义了。

针对StackOverflow问题，Scala compiler能够对某些特别的递归算法模式进行优化：把递归算法转换成while语句运算，但只限于尾递归模式（TCE, Tail Call Elimination），我们先用例子来了解一下TCE吧：
 */

def foldR[A, B](xs: List[A], b: B, f: (A, B) => B): B = xs match {
  case Nil    => b
  case h :: t => f(h, foldR(t, b, f))
}

foldR((1 to 100).toList, 0, { (a: Int, b: Int) => a + b })

// java.lang.StackOverflowError
// foldR((1 to 10000).toList, 0, {(a: Int, b: Int) => a + b})
// 以上的右折叠算法中自引用部分不在最尾部，Scala compiler无法进行TCE，所以处理一个10000元素的List就发生了StackOverflow。


@annotation.tailrec
def foldL[A, B](xs: List[A], b: B, f: (B, A) => B): B = xs match {
  case Nil    => b
  case h :: t => foldL(t, f(b, h), f)
}

// 在这个左折叠例子里自引用foldL出现在尾部位置，Scala compiler可以用TCE来进行while转换：
foldL((1 to 10000).toList, 0, { (a: Int, b: Int) => a + b })

def foldL2[A, B](xs: List[A], b: B, f: (B, A) => B): B = {
  var z = b
  var az = xs
  while (true) {
    az match {
      case Nil     => return z
      case x :: xs => {
        z = f(z, x)
        az = xs
      }
    }
  }

  z
}
/*
经过转换后递归变成Jump，程序不再使用堆栈，所以不会出现StackOverflow。

但在实际编程中，统统把递归算法编写成尾递归是不现实的。有些复杂些的算法是无法用尾递归方式来实现的，加上JVM实现TCE的能力有局限性，只能对本地（Local）尾递归进行优化。
 */


def even[A](xs: List[A]): Boolean = xs match {
  case Nil    => true
  case h :: t => odd(t)
}

def odd[A](xs: List[A]): Boolean = xs match {
  case Nil    => false
  case h :: t => even(t)
}

even((1 to 100).toList)

// java.lang.StackOverflowError
//even((1 to 1000000).toList)

/*
在上面的例子里even和odd分别为跨函数的各自的尾递归，但Scala compiler无法进行TCE处理，因为JVM不支持跨函数Jump：

我们可以通过设计一种数据结构实现以heap交换stack。Trampoline正是专门为解决StackOverflow问题而设计的数据结构：
 */

trait Trampoline[+A] {
  final def runT: A = this match {
    case Done(a) => a
    case More(k) => k().runT
  }
}

case class Done[+A](a: A) extends Trampoline[A]
case class More[+A](k: () => Trampoline[A]) extends Trampoline[A]
