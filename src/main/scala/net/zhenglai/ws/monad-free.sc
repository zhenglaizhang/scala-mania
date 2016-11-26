/*
任何涉及IO的运算都会面临堆栈溢出问题。这是因为IO通常针对无法预计的数据量以及重复循环操作。所以IO算法设计也会采用与Trampoline一样的数据结构。或者我们应该沿用Trampoline数据结构和算法来设计IO组件库。如此思考那么我们就必须对Trampoline进行深度抽象了。Free Monad就是Trampline的延伸。在介绍Free Monad之前我们先从一个现实的例子来展开讨论：

假设我们要编写一个银行转账的函数，我们可能先把这个函数的款式（function signature）推导出来：
def transfer(amount: Double, from: Account, to: Account, user: User, context: AuthorizationContext with Logger with ErrorHandler with Storage): Unit

这里采用了参数注入（parameter injection）方式：在transfer函数输入参数中注入context object。这个context object里包括了身份验证、操作跟踪、错误处理、数据存取等等。这算是传统OOP编程模式吧。对于一个泛函编程人员来讲：通过这个context object 可以进行一系列的操作。包括IO操作，也就是说可以进行一些含有副作用（side effect）的操作。那么这个函数是无法实现函数组合（function composition）。transfer函数就不是一个泛函编程人员该使用的函数了。


也许我们应该从泛函编程角度来尝试设计这个函数：用泛函编程提倡的不可蜕变（immutability）方式来设计，也就是向函数调用方返回一些东西。

比如我们可以向函数调用方返回一个描述操作的程序：一串命令（instruction）：

def transfer(amount: Double, from: Account, to: Account, user: User）： List[Instruction]
*/

//交互数据类型
trait Interact[A]

//提问，等待返回String类型答案
case class Ask(prompt: String) extends Interact[String]

//告知，没有返回结果
case class Tell(msg: String) extends Interact[Unit]

val prg = Seq(
  Ask("What's your first name?"),
  Ask("What's your last name?"),
  Tell("Hello ??? ???")
)

/*
这个程序prg是有缺陷的：无法实现交互。好像如果能把Ask指令存放到一个临时变量里就可以达到目的了。那么如果我们把这个prg改写成下面这样：

for {
     x <- Ask("What's your first name?")
     y <- Ask("What's your last name?")
     _ <- Tell(s"Hello $y $x!")
} yield ()


这不就是Monad款式吗？原来解决方法就是把交互类型trait Interact[A]变成Monad就行了。

不过要把Interact变成Monad就必须实现unit和flatMap两个函数，检查Interact trait，明显这是不可能的。

那我们把下面的努力都应该放在如何转变成Monad这方面了。既然我们在本篇命题里提到Free Monad是Monad生产线。那么用Free Monad能不能把Interact变成Monad呢？

我们先看看这个Free Monad类型结构：
 */
trait Free[F[_], A]

case class Return[F[_], A](a: A) extends Free[F, A]

case class Bind[F[_], I, A](a: F[I], f: I => Free[F, A]) extends Free[F, A]

