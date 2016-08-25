
/*
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
 */

