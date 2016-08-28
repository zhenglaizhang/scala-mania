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
