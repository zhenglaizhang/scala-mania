package net.zhenglai.lib

/*
泛函状态变迁机制（state transition mechanism）：怎么状态就起了变化，实在难以跟踪。我想这主要是因为状态变迁机制经过了函数组合，已经深深的埋藏在运行代码后面。上节我们讨论到RNG，对于了解State类型是个很好的开头。RNG简单描述了泛函方式的状态变迁及支持状态变迁所需要的数据结构和操作函数款式。

我们提到过 type Rand[+A] = RNG => (A, RNG)，Rand是一个随意数产生函数。由于Rand是个类型，一个函数类型，所以可以被当作参数或者返回值来使用。我们把这个定义再扩展一下，变得更通用一些：type State[S, +A] = S => (A, S)。Rand就是State的一个特殊案例：type Rand[+A] = State[RNG, +A] 。我们称State为状态行为，即S => (A,S)是一个定义状态变迁方式的函数。State类型的状态变迁机制就是通过状态行为函数来确定的。再次聚焦一下我们设计State类型的目标：State类型不但可以使我们像设计其它类型一样封装一个较低阶类型元素并且提供一套状态变迁机制，而且状态变迁机制是泛函式的，自然隐性的。

状态行为函数run是State类的内部成员，我们有针对性的把一个State的状态变迁机制通过在构建State类时作为参数注入。然后产生的State实例就会按照我们期待的那样进行状态变迁了。case class自备了apply,这样我们可以直接使用State(???)创建State实例。我会把State(s => (a,s))写成State { s => (a,s)}，这样表达传入的是一段代码会更形象自然一点。State[]既然是一个高阶类型，那么我们应该也为它提供一套在管子内部进行元素操作的函数。切记！切记！在处理管子内封装元素值的同时要按照状态行为函数的要求对类型状态进行相应变迁。
 */
case class State[S, +A](run: S => (A, S)) {
  // 在flatMap里我们用函数f处理了封装元素a, f(a)。同时我们又引用了状态行为函数run对传入的状态s进行了状态变迁 run(s)
  def flatMap[B](f: A => State[S, B]): State[S, B] = State[S, B] {
    s => {
      val (a, s1) = run(s)
      f(a).run(s1)
    }
  }

  def map[B](f: A => B): State[S, B] = State[S, B] {
    s => {
      val (a, s1) = run(s)
      (f(a), s1)
    }
  }

  /*
同样，map也实施了f(a),run(s)。map也可以用flatMap来实现。它们之间的分别只是f: A => B 和 A => State[S,B]。因为我们有unit, unit(a) = State[S,A]，unit(f(a)) = State[S,B]所以我们用unit把map的函数参数A升格就行了。用flatMap来实现map可以把map抽升到更高级：这样map就不用再理会那个状态行为函数了。
   */
  def map_1[B](f: A =>  B): State[S, B] = flatMap {a => State.unit(f(a))}

  // map2的功能是用封装元素类型函数(A,B) => C来把两个State管子里的元素结合起来。我们可以施用flatMap两次来把两个管子里的元素结合起来。对于map3我们可以再加一次。
      def map2[B,C](sb: State[S,B])(f: (A,B) => C): State[S,C] = {
             flatMap {a => sb.map { b => f(a,b) }}
         }
       def map3[B,C,D](sb: State[S,B], sc: State[S,C])(f: (A,B,C) => D): State[S,D] = {
             flatMap {a => sb.flatMap {b => sc.map { c => f(a,b,c) }}}
         }
  def map2_1[B,C](sb: State[S,B])(f: (A,B) => C): State[S,C] ={
    for {
      a <- this
      b <- sb
    } yield f(a,b)
  }
  def map3_1[B,C,D](sb: State[S,B], sc: State[S,C])(f: (A,B,C) => D): State[S,D] ={
    for {
      a <- this
      b <- sb
      c <- sc
    } yield f(a,b,c)
  }
  /*
以上的语法糖（syntatic sugar）for-comprehension让我们俨然进入了一个泛函世界，好像有了一种兴奋的感觉。这种表达形式简洁直白，更加容易理解。同样，在map2,map3里没有涉及到任何状态变迁的东西。我们实现了状态变迁的隐形操作。
   */

}

object State {
  // 这个unit。它就是一个封装元素值和状态都不转变的State实例。unit的唯一功能就是把低阶一级的封装元素类型a升格为State类型。
  def unit[S, A](a: A) = State[S, A](s => (a, s))

  def getState[S]: State[S, S] = State[S, S] { s => (s, s)}

  def setState[S](s: S): State[S, Unit] = State[S, Unit] { _ => ((), s)}
}
