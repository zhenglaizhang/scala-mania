

/*
Monoid的结合性和恒等值的作用以及Monoid如何与串类元素折叠算法相匹配。不过我们只示范了一下基础类型（primitive type）Monoid实例的应用，所以上一节的讨论目的是理论多于实践。在这一节我们将把重点放在一些实用综合类型（composite type）Monoid实例及Monoid的抽象表达及函数组合能力。

Monoid的二元操作函数具有结合特性（associativity），与恒等值（identity）共同应用可以任意采用左折叠或右折叠算法处理串类元素（List element）而得到同等结果。所以使用Monoid op我们可以得出左折叠等于右折叠的结论：

左折叠：op(op(op(a,b),c),d)
右折叠：op(a,op(b,op(c,d)))

但是，如果能够用并行算法的话就是：

并行算法：op(op(a,b),op(c,d)) 我们可以同时运算 op(a,b), op(c,d)
 */

// TODO http://www.cnblogs.com/tiger-xc/p/4447548.html

