/*
Philip这句话：一个单子（Monad）说白了不过就是自函子范畴上的一个幺半群而已

单子（Monad），自函子（Endo-Functor），幺半群（Monoid），范畴（category）




范畴的定义
范畴由三部分组成：

  1. 一组对象。
  2. 一组态射（morphisms）。每个态射会绑定两个对象，假如f是从源对象A到目标对象B的态射，记作：f：A -> B。
  3. 态射组合。假如h是态射f和g的组合，记作：h = g o f。 (f的输出作为g的输入)

态射我们可以简单的理解为函数，假如在某范畴中存在一个态射，它可以把范畴中一个Int对象转化为String对象。在Scala中我们可以这样定义这个态射：f : Int => String = ...。所以态射的组合也就是函数的组合，
 */

val f1: Int => Int = i => i + 1

val f2: Int => String = i => i.toString

val f3 = f2 compose f1
// f2(f1(_))

f3(12)



/*
范畴公理
范畴需要满足以下三个公理。

  1. 态射的组合操作要满足结合律。记作：f o (g o h) = (f o g) o h

  2. 对任何一个范畴 C，其中任何一个对象A一定存在一个单位态射，id_A: A => A。并且对于态射g：A => B 有 id_B o g = g = g o id_A。

  3. 态射在组合操作下是闭合的。所以如果存在态射f: A => B 和g: B => C，那么范畴中必定存在态射 h: A => C 使得 h = g o f。
 */