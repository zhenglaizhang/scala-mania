
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
 */

