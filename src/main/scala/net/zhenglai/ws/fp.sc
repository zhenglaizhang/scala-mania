import scala.annotation.tailrec

/*
面向对象编程范畴（OOP）从80年代C++到90年代java的兴起已经经历了几十年的高潮，是不是已经发展到了尽头，该是函数式编程（FP）开始兴旺发达的时候了吧。这样说似乎心眼儿有点坏，可能会得罪当今大多数的编程人员。不过计算机硬件技术的发展往往会催生新的编程技术，这倒是无可反驳的现象。当今世界上计算机行业中大数据、电子商务、多核CPU，高并发网络的普及使得C++,java这些OOP范畴的编程语言显得那么地不尽人意，函数式编程范畴的编程语言将成为主流，这应该是句大实话了吧。

  说到函数式编程（FP）我们常常会联想到以下几个方面：

1、不可变性 － Immutability

2、函数既值 － Function as value

3、无副作用 － No side effects

这几样特性可以很好地解决多核CPU、多线程、高并发问题。

scala是个OOP和FP混合范畴的编程语言。这是因为考虑到那么许多从OOP世界过来的编程人员可以尽快上手，而且有许多问题可能用OOP方式能得到更好的解决。但重要的是在使用scala编程中到底以OOP还是FP为主。如果我们采用scala的FP为主的话，scala标准库（sdandard library）中的数据类型和函数组件就显得不足够应付，我们必须在用scala FP开发软件前准备好一套较为完整的函数组件库（combinator library）。幸运的是我们现在有了scalaz，它使我们在泛函编程的道路上节省了一大段路程。

  scalaz是一套用scala语言编写的函数库。scalaz为用户提供了大量的数据类型和组件函数来支持函数式编程。实际上scalaz的代码贡献者们是受到了纯函数式编程语言haskell的启发，把haskell中的数据类型、结构、函数组件在scalaz中用scala进行了重新实现。既然我们打算采用scala的FP，我们可能必须把scalaz作为基础组件库来使用，那么我们必须首先了解scalaz的库结构、里面各种数据类型和组件函数、掌握它们的使用方式以及应用模式。

  当然，在学习和介绍scalaz的过程中我们还可以更多了解scala的函数式编程模式以及它所著名的贴切简洁的表现形式。

让我们期待这个系列的scalaz讨论能真正的把我们带入函数式编程范畴的世界。。。

 pure functions - functions that have no side effects(Throwing an exception or halting with an error,  Printing to the console or reading user input,  Drawing on the screen)

functional programming is a restriction on how we write programs, but not on what programs we can express

Because of their MODULARITY, pure functions are easier to test, reuse, parallelize, generalize, and reason about.

 => Two important concepts—referential transparency and the substitution model.

Functional programming is often promoted first as a way to do concurrency. However, I've found it to be more fundamentally useful as a way to decompose programming problems.

Functional programming is often promoted first as a way to do concurrency. However, I've found it to be more fundamentally useful as a way to decompose programming problems.


A pure function is modular and composable because it separates the logic of the computation itself from “what to do with the result” and “how to obtain the input”; it’s a black box


Polymorphic functions: abstracting over types
 */

/*
It's Not "Just About Finger Typing"

Scala removes as much of the overhead (and mental load) as possible, so you can express higher-order concepts as quickly as you can type them. I was amazed to discover that in many cases, Scala is even more succinct than Python.
 */

/*
“泛函” == 函数式编程
说到函数式编程（FP）我们常常会联想到以下几个方面：

1、不可变性 － Immutability

2、函数既值 － Function as value

3、无副作用 － No side effects

这几样特性可以很好地解决多核CPU、多线程、高并发问题。

scala是个OOP和FP混合范畴的编程语言。

如果我们采用scala的FP为主的话，scala标准库（sdandard library）中的数据类型和函数组件就显得不足够应付，我们必须在用scala FP开发软件前准备好一套较为完整的函数组件库（combinator library）。

=> ScalaZ
scalaz是一套用scala语言编写的函数库。scalaz为用户提供了大量的数据类型和组件函数来支持函数式编程。实际上scalaz的代码贡献者们是受到了纯函数式编程语言haskell的启发，把haskell中的数据类型、结构、函数组件在scalaz中用scala进行了重新实现


泛函编程就想砌积木一样把函数当成积木块，把函数的输出输入作为积木的楔子和楔孔，把一个函数的输出当作另一个函数的输入组合成一个更大的函数。整个砌积木的过程就是泛函编程。

相对于泛函编程模式还有指令编程模式（Imperative Programming)。我们熟悉的OOP编程就是指令编程模式。在指令编程中我们按顺序用一条条指令改变程序中的一些变量来实现整个程序状态转变。

而在泛函编程中我们首先按照程序要求把一些特定的函数用特定的方式组合起来形成另一个独立的大函数；然后把一些东西输入到这个大函数的输入口；当输入物经过那条由内部组件函数输入输出形成的曲折通道到达输出口时就产生了需要的结果（很像输入物件的变形过程）。输入物每经过一个组件函数，程序的状态就会发生一些转变，整个过程实际上就是程序的状态变形（Program State Transformation)。那么，可不可以说指令编程就对应变量赋值，泛函编程相当于函数组合呢？实际上“函数组合”这个词是泛函编程的灵魂，英文是Functional Composition。

如果泛函编程就是组合函数，那这可是一种全新的编程方式。如何实现函数的组合呢？泛函编程是以数学理论（⋋-culculus）为基础的，程序函数的组合是通过数学函数组合定律来实现的。

泛函编程的函数组合（Functional Composition）遵循一定的数学定律（Mathematical Laws)，这保证了组成的函数具备要求的行为特征（Behavior）。再者，所有组件函数都必须具备行为不可变化特性，即无论在任何场合，都不会因为产生了不同的最终结果而影响它们的行为。如果是这样，组合函数的行为都是可预知的，那么它们在程序中的作用也就可控了。

泛函程序是由纯函数组成。所谓纯函数（Pure Function）是指这个函数的结果完全或只依赖它的输入。对于任何一个输入值只会产生一个唯一的相同结果，而不会因为什么其它的原因影响而变成另一个不同的结果。一个函数是由一个或多个表达式组成。组成一个纯函数的表达式都必须是可以“等量替换“的，意思是每个表达式都可以用这个表达式的结果替代而不会影响整个函数的行为结果。我抛开了英文Referencial Transparent的字面意思把它翻译成”可等量替换的“。我们可以通过”等量替换“方式来分析理解函数行为。纯函数（Pure Function）只依赖输入产生结果，不会造成任何”附带影响“（Side Effect）。所谓”附带影响“是指计算一个表达式后影响了函数的结果。因为泛函程序是由纯函数组成，纯函数是”可等量替换的“，具备行为不可变化特性，所以能保证泛函程序的正确性。

无“附带影响”、可“等量替换”作为泛函程序正确性的保障

它们出现在一些更大的程序中时我们同样可以运用”等量替换“而不改变程序的行为

先来个超简单的例子：这个表达式 1+1=2够简单了吧。在Scala语言中 “+” 是个函数名称，我们可以确定这个“＋”函数是个纯函数，因为我们可以放心的用结果2来“等量替代” 表达式1+1。


StringBuilder.append不是一个纯函数，我们决不能用它来进行函数组合（Function Composition），因为组成的程序行为是不可预料的

StringBuilder.append之所以不是纯函数是因为StringBuilder是一个内容可以改变的数据结构（data structure），是"可改变的“(mutable)数据结构。泛函编程要求尽量使用”不可改变的“（Immutable）数据结构来保证程序的纯洁性。泛函编程就好像是使用”不可改变的“数据结构过程的挣扎
 */

1 + 1

2

// OOP style, 典型的指令式编程（Imperative Programming）；通过改变变量值来实现程序的状态转变
def createErrorMessageOOP(errorCode: Int): String = {
  var result: String = ""

  errorCode match {
    case 1 => result = "Network Error"
    case 2 => result = "IO Error"
    case 3 => result = "Unknown Error"
  }

  return result
}

// 首先，没有中间变量。整个函数简洁明了的多。不经过中间变量直接返回结果；这就是泛函编程的一个风格特征。
// 这个函数的是一个纯函数，也是一个完整函数。因为函数主体涵盖了所有输入值（注意: case _ =>）。我们可以预知任何输入msgId值所产生的结果。还有，函数中没有使用任何中间变量。
def createErrorMessage(errorCode: Int) = errorCode match {
  case 1 => "Network Error"
  case 2 => "IO Error"
  case 3 => "Unknown Error"
}

assert(createErrorMessage(1) == createErrorMessageOOP(1))

/*
泛函编程和数学方程式解题相似；用某种方式找出问题的答案。泛函编程通用的方式包括了模式匹配（pattern matching）以及递归思维（Recursive thinking）。我们先体验一下：
 */

def factorial(n: Int): Int = n match {
  case 1 => 1
  case k => k * factorial(k - 1)
}

factorial(10)

// 我们试着用“等量替换”方式逐步进行约化（reduce）

// 递归程序可以用 loop来实现。主要目的是防止堆栈溢出（stack overflow）。不过这并不妨碍我们用递归思维去解决问题。 阶乘用while loop来写：
def factorial2(n: Int): Int = {
  var k: Int = n
  var acc: Int = 1
  while (k > 1) {
    acc = acc * k
    k = k - 1
  }
  acc
}

factorial2(10)
/*
注意factorial_2使用了本地变量k，acc。虽然从表达形式上失去了泛函编程的优雅，但除了可以解决堆栈溢出问题外，运行效率也比递归方式优化。但这并不意味着完全违背了“不可改变性”（Immutability）。因为变量是锁定在函数内部的。
最后，也可用tail recursion方式编写阶乘。让编译器（compiler）把程序优化成改变成 loop 款式：
 */
def factorial_3(n: Int): Int = {

  // 得出的同样是正确的答案。这段程序中使用了@annotation.tailrec。如果被标准的函数不符合tail recusion的要求，compiler会提示。
  @tailrec
  def go(n: Int, acc: Int): Int = n match {
    case k if k <= 0 => sys.error("Negative number not supported")
    case 1           => acc
    case k           => go(n - 1, acc * k)
  }

  go(n, 1)
}

factorial_3(10)
