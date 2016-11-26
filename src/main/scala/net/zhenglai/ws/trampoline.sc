/*
 泛函编程方式其中一个特点就是普遍地使用递归算法，而且有些地方还无法避免使用递归算法。比如说flatMap就是一种推进式的递归算法，没了它就无法使用for-comprehension，那么泛函编程也就无法被称为Monadic Programming了。虽然递归算法能使代码更简洁易明，但同时又以占用堆栈（stack）方式运作。堆栈是软件程序有限资源，所以在使用递归算法对大型数据源进行运算时系统往往会出现StackOverflow错误。如果不想办法解决递归算法带来的StackOverflow问题，泛函编程模式也就失去了实际应用的意义了。

针对StackOverflow问题，Scala compiler能够对某些特别的递归算法模式进行优化：把递归算法转换成while语句运算，但只限于尾递归模式（TCE, Tail Call Elimination），我们先用例子来了解一下TCE吧：


f you do find a call that you think should be optimised by the compiler, but isn't, then you should check that the call:
    is a tail call,
    is in a final method or local function, and
    is to itself.
For example, the code for factorial below would not be optimised. The call is not in tail position (the tail operation is the multiplication), and the method is public and non-final, so it could be overridden by a subclass.


But there are some types of recursive code that the compiler will not be able to optimise. For example, if your code is mutually recursive, as it is with odd1 and even1, then you will need to try something else. One thing you might consider, is using a trampoline.
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
      case Nil => return z
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

Trampoline代表一个可以一步步进行的运算。每步运算都有两种可能：Done(a),直接完成运算并返回结果a，或者More(k)运算k后进入下一步运算；下一步又有可能存在Done和More两种情况。注意Trampoline的runT方法是明显的尾递归，而且runT有final标示，表示Scala可以进行TCE。

A trampoline is a loop that repeatedly runs functions. Each function, called a thunk, returns the next function for the loop to run. The trampoline never runs more than one thunk at a time, so if you break up your program into small enough thunks and bounce each one off the trampoline, then you can be sure the stack won't grow too big.

Here is our program again, rewritten in trampolined style. Call objects contain the thunks and a Done object contains the final result. Instead of making a tail call directly, each method now returns its call as a thunk for the trampoline to run. This frees up the stack after each iteration. The effect is very similar to tail-call optimisation.

Trampolined code is harder to read and write, and it executes more slowly. However, trampolines can be invaluable when your program would otherwise run out of stack space, and the only other alternative is to convert it into an imperative style


Trampoline。它主要是为了解决堆栈溢出（StackOverflow）错误而设计的。Trampoline类型是一种数据结构，它的设计思路是以heap换stack：对应传统递归算法运行时在堆栈上寄存程序状态，用Trampoline进行递归算法时程序状态是保存在Trampoline的数据结构里的。数据结构是在heap上的，所以可以实现以heap换stack的效果。这种以数据结构代替函数调用来解决问题的方式又为泛函编程提供了更广阔的发展空间。
 */
trait Trampoline[+A] { // Bounce
  @annotation.tailrec
  final def runT: A = this match {
    case Done(a) => a
    case More(k) => k().runT
  }
}

case class Done[+A](a: A) extends Trampoline[A]
case class More[+A](thunk: () => Trampoline[A]) extends Trampoline[A]

def even2[A](xs: List[A]): Trampoline[Boolean] = xs match {
  case Nil    => Done(true)
  case h :: t => More(() => odd2(t))
}

def odd2[A](xs: List[A]): Trampoline[Boolean] = xs match {
  case Nil    => Done(false)
  case h :: t => More(() => even2(t))
}

even2((1 to 10000).toList).runT
odd2((1 to 10000).toList).runT
