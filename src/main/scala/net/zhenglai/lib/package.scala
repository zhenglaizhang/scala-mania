package net.zhenglai

package object lib {

  import _root_.net.zhenglai.lib.RNG._

  // lambda of Func(RNG):(A, RNG)
  type RAND[+A] = RNG => (A, RNG)

  def rnInt: RAND[Int] = _.nextInt

  def rnDouble: RAND[Double] = nextDouble

  /*
已经把随意数产生器变成了Rand类型，我们应该可以方便地对随意数产生器进行组合、变形了吧？先看一个最基本的组件（combinator）

unit可以说是函数组合的最基本组件，是大大的有用的。unit只有一个作用：把a包进高阶类Rand里，换句话说就是把低阶类A升格到高阶Rand，这样就可以和其它Rand进行组合或者使用Rand函数自我变形了。这个简单的例子再次提示了从返回类型来推导功能实现这种泛函编程风格：Rand[A] >>> RNG => (A, RNG) 即：给我一个RNG我就可以返回一个(A, RNG)
   */
  def unit[A](a: A): RAND[A] = {
    rng => (a, rng)
  }

  def rnDouble2: RAND[Double] = {
    map(rnPositiveInt) {
      _ / Int.MaxValue.toDouble + 1
    }
  }

  def rnPositiveInt: RAND[Int] = nextPositiveInt

  /*
  从函数的实现方式可以得出map就是对一个随意数产生器的结果进行转换后仍然保留Rand的高阶类型格式。还和上一个例子一样：我们一看到返回类型Rand就应该立刻想到 rng => {...(a2, rng2)}这种实现风格了。
   */
  def map[A, B](ra: RAND[A])(f: A => B): RAND[B] = {
    rng => {
      val (x, rng2) = ra(rng)
      (f(x), rng2)
    }
  }

  // combine 2 RANDs
  def map2[A, B, C](ra: RAND[A], rb: RAND[B])(f: (A, B) => C): RAND[C] = {
    rng => {
      val (x, rng2) = ra(rng)
      val (y, rng3) = rb(rng2)
      (f(x, y), rng3)
    }
  }

  def rnPair[A, B](ra: RAND[A], rb: RAND[B]): RAND[(A, B)] = {
    map2(ra, rb){(_,_)}
  }

  def rnIntDoublePair: RAND[(Int, Double)] = {
    rnPair(rnInt, rnDouble)
  }

  def rnDoubleIntPair: RAND[(Double, Int)] = {
    rnPair(rnDouble, rnInt)
  }

  //用递归方式
  // 那么能不能把一个List里面的Rand结合成一个Rand呢
  def sequence[A](lr: List[RAND[A]]): RAND[List[A]] = {
    rng => {
      def go(xs: List[RAND[A]], r: RNG, acc: List[A]): (List[A], RNG) = {
        lr match {
          case Nil => (acc,rng)
          case h :: t => {
            val (x, rng2) = h(rng)
            go(t,rng2,x::acc)
          }
        }
      }
      go(lr,rng,List())
    }
  }
  //用foldRight实现
  def sequence_1[A](lr: List[RAND[A]]): RAND[List[A]] = {
    lr.foldRight(unit(Nil: List[A])) {(h,t) => map2(h,t)(_ :: _)}
  }

  /*
从以上foldRight实现方式可以体会unit的作用：它把List[A］升了格，这样才能与Rand的map2类型匹配。可以发现使用了map,map2,sequence去操作Rand时，我们已经无须理会这个RNG了，这证明我们已经向着更高的抽象层进步了，这也是泛函编程的真义所在。
   */

  def flatMap[A,B](ra: RAND[A])(f: A => RAND[B]): RAND[B] = {
    rng => {
      val (x, rng2) = ra(rng)
      f(x)(rng2)
    }
  }
  def positiveIntByFlatMap: RAND[Int] = {
    flatMap(rnInt) {
      a => {
        if ( a != Int.MinValue) unit(a.abs)
        else positiveIntByFlatMap
      }
    }
  }

  def mapByFlatMap[A,B](ra: RAND[A])(f: A => B): RAND[B] = {
    flatMap(ra){ a => unit(f(a)) }
  }
  def map2ByFlatMap[A,B,C](ra: RAND[A], rb: RAND[B])(f: (A,B) => C): RAND[C] = {
    flatMap(ra){ a => map(rb) {b => f(a,b)}}
  }
  def map3ByFlatMap[A,B,C,D](ra: RAND[A], rb: RAND[B], rc: RAND[C])(f: (A,B,C) => D): RAND[D] = {
    flatMap(ra){ a => flatMap(rb) {b => map(rc) {c => f(a,b,c)}}}
  }

  /*
代码是不是越来越简洁了？而且仿佛进入了数学世界。我是说现在感觉编程已经变成了好像高中做数学题一样：拿到一个函数描述就开始想办法用什么其它现有的函数来解决；然后匹配一下类型，找找以前的例子，等等。。。,完全没有感觉到是在编写计算机程序。
   */
}
