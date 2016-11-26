/*
 延后计算（lazy evaluation）是指将一个表达式的值计算向后拖延直到这个表达式真正被使用的时候。在讨论lazy-evaluation之前，先对泛函编程中比较特别的一个语言属性”计算时机“(strict-ness)做些介绍。strict-ness是指系统对一个表达式计算值的时间点模式：即时计算的（strict)，或者延后计算的（non-strict or lazy）。non-strict或者lazy的意思是在使用一个表达式时才对它进行计值。
 */

def nonLazyF(x: Int) = {
  println("inside function")
  x + 1
}
//nonLazyF(1 / 0)
// no output here! calculation is done before enter nonLazyF

def lazyF(x: => Int) = {
  println("inside function")
  x + 1
}

//lazyF(1 / 0)
// "inside function" got printed!

/*
我们看到参数x的类型是 => Int, 代表x参数是non-strict的。non-strict参数每次使用时都会重新计算一次。从内部实现机制来解释：这是因为编译器（compiler）遇到non-strict参数时会把一个指针放到调用堆栈里，而不是惯常的把参数的值放入。所以每次使用non-strict参数时都会重新计算一下。

实际上很多语言中的布尔表达式（Boolean Expression）都是non-strict的，包括 &&, ||
 */

def pair(x: => Int): (Int, Int) = (x, x)
pair({
  println("hello...")
  5
})

import net.zhenglai.lib.MyPredef._

If(true, 1, 0)
If(false, 1, 0)

/*
non-strict参数在函数内部有可能多次运算；如果这个函数内部多次使用了这个参数。同样道理，如果这个参数是个大型计算的话，又会产生浪费资源的结果。在Scala语言中lazy声明可以解决non-strict参数多次运算问题。lazy值声明（lazy val）不但能延后赋值表达式的右边运算，还具有缓存（cache）的作用：在真正使时才运算表达式右侧，一旦赋值后不再重新计算。
 */
def pairWithCache(x: => Int): (Int, Int) = {
  lazy val y = x
  (y, y)
}
pairWithCache({
  println("hello...")
  5
})

