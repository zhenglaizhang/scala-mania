/*
上两期我们讨论了Monad。我们说Monad是个最有概括性（抽象性）的泛函数据类型，它可以覆盖绝大多数数据类型。任何数据类型只要能实现flatMap+unit这组Monad最基本组件函数就可以变成Monad实例，就可以使用Monad组件库像for-comprehension这样特殊的、Monad具备的泛函式数据结构内部的按序计算运行流程。针对不同的数据类型，flatMap+unit组件实现方式会有所不同，这是因为flatMap+unit代表着承载数据类型特别的计算行为。之前我们尝试了List,Option,甚至更复杂的State等数据类型的Monad实例，过程中我们分别对这些数据类型的unit和flatMap进行了实现。

实际上flatMap+unit并不是Monad唯一的最基本组件函数，还有compose+unit及join+map+unit这两组Monad最基本组件函数，因为我们可以用这些组件相互实现：

所以，我们可以通过直接对数据类型实现join+map+unit或compose+unit来产生Monad实例。

因为我们能够用flatMap来实现map2，所以Monad就是Applicative。但反之Applicative不一定是Monad。


先看看map,map2,flatMap这三个函数：

1   def map[A,B]      (ma: M[A])       (f: A => B)    : M[B]
2   def map2[A,B,C](ma: M[A], mb: M[B])(f: (A,B) => C): M[C]
3   def flatMap[A,B]  (ma: M[A])       (f: A => M[B]) : M[B]
map和map2都是正宗的在高阶数据类型结构内的函数施用，但flatMap的函数是 A=>M[B]，会破坏结果的结构。例如：我们对一个有3个元素的List进行map操作，结果仍然是一个3个元素的List。但如果flatMap的话就可能会产生不同长度的List：
*/