package net.zhenglai.lib
import java.util.concurrent.{ Callable, TimeUnit }

/*
，我们经常会因为工作需要建立一些工具库。所谓工具库就是针对工作上经常会遇到的一些共性问题预先编制的由一整套函数所组成的函数库。通常这些工具库的功能都是在特别定制的一些数据类型支持下由一系列函数围绕着这些数据类型进行运算而实现的。在泛函编程范畴内也不例外。但在泛函工具库里的函数则更重视函数的组合能力（functional composition）；因而泛函的工具库一般称为组件库（combinator library），库内函数则被称之为组件（combinator）。组件库的设计者对函数设计有着共通的最基本目标：通过对组件进行各种函数组合可以实现更大的功能。泛函组件库设计一般针对特别的功能需求或课题：首先尝试用一些数据类型来表述课题需求，然后围绕这些特制的数据类型设计一系列函数针对课题各个最基本需求范畴提供解决方法。


可以把一个普通运算放到另外一个独立的线程（thread）中去运行。这样我们可以同时把多个运算分别放到多个线程中同时运行从而达到并行运算的目的。问题简单明确，但如何对这些在各自独立运行空间的运算进行组合（composition）、变形（transformation）则值得仔细思量。
 */

/*
先从数据类型着手：一个并行运算应该像是一个容器，把一个普通运算封装在里面。我们来随便造个结构出来：Par[A]，A是普通运算返回的结果类型。这个Par类型很像我们前面接触的高阶类型，那个承载A类型元素的管子类型。如果这样去想的话，我们可以用前面所有针对高阶类型的函数对管子内的元素A进行操作处理。那么如果一个运算是封装在Par里在另一个线程中运算完成后总是需要一个方法把结果取出来。
 */
case class ParObs[A]() {
}

object ParObs {

  //把一个普通运算注入Par。把A升格到一个并行运算
  def unit[A](a: => A): ParObs[A] = ???

  //把并行运行结果抽取出来
  def get[A](pa: ParObs[A]): A = run(pa)

  /*
下一个问题是运行线程控制：是由程序员来决定一个运算该放到一个新的线程里还是固定每一个运算都用新的独立线程？假设我们选择用由程序员调用一个函数来确定产生新线程。这样有两个优越：1、可以有更灵活的并行运算策略（有些已经确定很快完成的运算可能没有必要用新的线程，独立线程运算可能消耗更多的资源）；2、独立线程机制和并行运算是松散耦合的：Par的实现中不需要了解线程管理机制。
 */

  /*
  因为我们追求的是线程机制和并行运算的松散耦合，那么我们就不会在Par里实际进行并行运算的运行，那么Par就只是对一个并行运算的描述。fork的返回还是Par，只是增加了对运算环境的描述，也不会真正运行算法。这样来说Par如果是一个运算描述，那么我们就需要一个真正的运行机制来获取运算结果了：

  我们就需要在run的函数实现方法里进行线程管理、计算运行等真正Par的运行了。
   */
  def run[A](pa: ParObs[A]): A = ??? //由于Par的意义从容器变成运算描述，我们把get重新命名为run
  //为pa设定一个新的运行空间。并不改变pa，还是返回Par[A]
  def fork[A](pa: ParObs[A]): ParObs[A] = ???

  //不需要了解任何关于Par的信息。知道fork会为这个运算设定新的运行空间。注意还是返回Par[A]
  def async[A](a: => A): ParObs[A] = fork(unit(a))

  def isDone: Boolean = ???

  def isCancelled: Boolean = ???
}

/*
class ExecutorService {
  def submit[A](a: Callable[A]): Future[A]
}
trait Future[A] {
  def get: A
  def get(timeout: Long, unit: TimeUnit): A
  def cancel(evenIfRunning: Boolean): Boolean
  def isDone: Boolean
  def isCancelled: Boolean
}
*/

object Par {
  import java.util.concurrent.{ ExecutorService, Future }

  /*
我们不需要进入多线程编程底层细节，用java Concurrent ExecutorService足够了。ExecutorService提供了以Callable形式向系统提交需运算任务方式；系统立即返回Future,我们可以用Future.get以锁定线程方式读取运算。由于运算结果读取是以锁定线程（blocking）形式进行的，那么使用get的时间节点就很重要了：如果提交一个运算后下一步直接get就会立即锁定线程直至运算完成，那我们就无法得到任何并行运算效果了。Future还提供了运行状态和中断运行等功能为编程人员提供更强大灵活的运算控制。为了获取更灵活的控制，Par的返回值应该从直接锁定线程读取A改成不会产生锁定线程效果的Future：
   */

  /*
  现在Par的含义又从一个数据类型变成了一个函数描述了：传入一个ExecutorService，返回Future。我们可以用run来运行这个函数，系统会立即返回Future,无需任何等待。
   */
  type Par[A] = ExecutorService => Future[A]
  def run[A](es: ExecutorService)(pa: Par[A]): Future[A] = pa(es)

  /*
unit[A](a: A): Par[A] : 我们硬生生的按照Par的类型款式造了一个Future实例，这样我们才可以用Future.get的形式读取运算结果值。看看这个例子：unit(42+1)，在调用函数unit时由于传入参数是即时计算的，所以在进入unit前已经完成了计算结果43。然后人为的把这个结果赋予Future.get，这样我们就可以和真正的由ExecutorService返回的Future一样用同样的方式读取结果。所以说unit纯粹是一个改变格式的升格函数，没有任何其它作用。
   */
  def unit[A](a: A): Par[A] = es => {
    new Future[A] {

      override def isCancelled: Boolean = false

      override def get(): A = a;

      override def get(timeout: Long, unit: TimeUnit): A = get

      override def cancel(mayInterruptIfRunning: Boolean): Boolean = false

      override def isDone: Boolean = true
    }
  }

  // fork(pa: => Par[A])才可以保证在提交任务前都不会计算表达式a
  def fork[A](pa: => Par[A]): Par[A] = es => {
    es.submit(new Callable[A] {
      override def call(): A = run(es)(pa).get
    })
  }

  /*
async[A](a: => A): Par[A]：这个async函数把表达式a提交到主线程之外的另一个线程。新的线程由ExecutorService提供，我们无须理会，这样可以实现线程管理和并行运算组件库的松散耦合。由于async的传人函数是延后计算类型，所以我们可以把表达式a提交给另一个线程去运算。
   */
  def async[A](a: => A): Par[A] = fork(unit(a))

  /*
  先用泛函方式启动并行运算。如果我们并行启动两个运算：
   */
  def map2[A, B, C](pa: Par[A], pb: Par[B])(f: (A, B) => C): Par[C] = {
    es =>
      new Future[C] {

        //在这里按pa的定义来确定在那个线程运行。如果pa是fork Par则在非主线程中运行
        var fa = run(es)(pa)
        var fb = run(es)(pb)

        override def isCancelled: Boolean = fa.isCancelled && fb.isCancelled
        override def get(): C = f(fa.get, fb.get)
        override def get(timeout: Long, unit: TimeUnit): C = {
          val start = System.nanoTime
          val a = fa.get
          val end = System.nanoTime

          //fa.get用去了一些时间。剩下给fb.get的timeout值要减去
          val b = fb.get(timeout - unit.convert((end - start), TimeUnit.NANOSECONDS), unit)

          f(a, b)
        }
        override def cancel(mayInterruptIfRunning: Boolean): Boolean = fa.cancel(mayInterruptIfRunning) || fb.cancel(mayInterruptIfRunning)

        override def isDone: Boolean = fa.isDone && fb.isDone
      }
  }

  def map3[A, B, C, D](pa: Par[A], pb: Par[B], pc: Par[C])(f: (A, B, C) => D): Par[D] = {
    map2(pa, map2(pb, pc) { (b, c) => (b, c) }) { (a, bc) =>
      val (b, c) = bc
      f(a, b, c)
    }
  }

  def map4[A, B, C, D, E](pa: Par[A], pb: Par[B], pc: Par[C], pd: Par[D])(f: (A, B, C, D) => E): Par[E] = { //| 71.Par.Par[C]
    map2(pa, map2(pb, map2(pc, pd) { (c, d) => (c, d) }) { (b, cd) => (b, cd) }) { (a, bcd) =>
      {
        val (b, (c, d)) = bcd
        f(a, b, c, d)
      }
    }
  }
  def map5[A, B, C, D, E, F](pa: Par[A], pb: Par[B], pc: Par[C], pd: Par[D], pe: Par[E])(f: (A, B, C, D, E) => F): Par[F] = { //| 71.Par.Par[C]
    map2(pa, map2(pb, map2(pc, map2(pd, pe) { (d, e) => (d, e) }) { (c, de) => (c, de) }) { (b, cde) => (b, cde) }) { (a, bcde) =>
      {
        val (b, (c, (d, e))) = bcde
        f(a, b, c, d, e)
      }
    }
  }

  //我们可以run pa, get list 后进行排序，然后再封装进Future[List[Int]]
  def sortPar(pa: Par[List[Int]]): Par[List[Int]] = {
    es =>
      {
        val l = run(es)(pa).get
        new Future[List[Int]] {
          def get = l.sorted
          def isDone = true
          def isCancelled = false
          def get(t: Long, u: TimeUnit) = get
          def cancel(e: Boolean) = false
        }
      }
  }
  //也可以用map2来实现。因为map2可以启动并行运算，也可以对par内元素进行操作。但操作只针对一个par,
  //我们用unit(())替代第二个par。现在我们可以对一个par的元素进行操作了
  def sortedPar(pa: Par[List[Int]]): Par[List[Int]] = {
    map2(pa, unit(())) { (a, _) => a.sorted }
  }
  //map是对一个par的元素进行变形操作，我们同样可以用map2实现了
  def map[A, B](pa: Par[A])(f: A => B): Par[B] = {
    map2(pa, unit(())) { (a, _) => f(a) }
  }
  //然后用map去对Par[List[Int]]排序
  def sortParByMap(pa: Par[List[Int]]): Par[List[Int]] = {
    map(pa) { _.sorted }
  }
  /*
  sortPar(async({println(Thread.currentThread.getName); List(4,1,2,3)}))(es).get
  sortParByMap(async({println(Thread.currentThread.getName); List(4,1,2,3)}))(es).get
   */

  // 实际上map2做了两件事：启动了两个并行运算、对运算结果进行了处理。这样说map2是可以被分解成更基本的组件函数：
  //启动两项并行运算
  def product[A, B](pa: Par[A], pb: Par[B]): Par[(A, B)] = {
    es => unit((run(es)(pa).get, run(es)(pb).get))(es)
  } //> product: [A, B](pa: ch71.Par.Par[A], pb: ch71.Par.Par[B])ch71.Par.Par[(A, B
  //| )]
  //处理运算结果
  //  def map[A,B](pa: Par[A])(f: A => B): Par[B] = {
  //    es => unit(f(run(es)(pa).get))(es)
  //  }                                               //> map: [A, B](pa: ch71.Par.Par[A])(f: A => B)ch71.Par.Par[B]
  //再组合map2
  def map2_pm[A, B, C](pa: Par[A], pb: Par[B])(f: (A, B) => C): Par[C] = {
    map(product(pa, pb)) { a => f(a._1, a._2) }
  } //> map2_pm: [A, B, C](pa: ch71.Par.Par[A], pb: ch71.Par.Par[B])(f: (A, B) => C
  //| )ch71.Par.Par[C]

  //  我们还可以把函数A => B转换成A => Par[B]，意思是把对A的运算变成并行运算Par[B]:

  def asyncF[A, B](f: A => B): A => Par[B] = a => async(f(a))

  //用递归法实现
  def sequence_r[A](lp: List[Par[A]]): Par[List[A]] = {
    lp match {
      case Nil => unit(List())
      case h :: t => map2(h, fork(sequence_r(t))) { _ :: _ }
    }
  } //> sequence_r: [A](lp: List[ch71.Par.Par[A]])ch71.Par.Par[List[A]]
  //用foldLeft
  def sequenceByFoldLeft[A](lp: List[Par[A]]): Par[List[A]] = {
    lp.foldLeft(unit[List[A]](Nil)) { (t, h) => map2(h, t) { _ :: _ } }
  } //> sequenceByFoldLeft: [A](lp: List[ch71.Par.Par[A]])ch71.Par.Par[List[A]]
  //用foldRight
  def sequenceByFoldRight[A](lp: List[Par[A]]): Par[List[A]] = {
    lp.foldRight(unit[List[A]](Nil)) { (h, t) => map2(h, t) { _ :: _ } }
  } //> sequenceByFoldRight: [A](lp: List[ch71.Par.Par[A]])ch71.Par.Par[List[A]]
  //用IndexedSeq切成两半来实现
  def sequenceBalanced[A](as: IndexedSeq[Par[A]]): Par[IndexedSeq[A]] = {
    if (as.isEmpty) unit(Vector())
    else if (as.length == 1) map(as.head) { a => Vector(a) }
    else {
      val (l, r) = as.splitAt(as.length / 2)
      map2(sequenceBalanced(l), sequenceBalanced(r)) { _ ++ _ }
    }
  } //> sequenceBalanced: [A](as: IndexedSeq[ch71.Par.Par[A]])ch71.Par.Par[IndexedS
  def sequence[A](lp: List[Par[A]]): Par[List[A]] = { //| eq[A]]
    map(sequenceBalanced(lp.toIndexedSeq)) { _.toList }
  }

  def parMap[A, B](as: List[A])(f: A => B): Par[List[B]] = fork {
    val lps = as.map { asyncF(f) }
    sequence(lps)
  } //> parMap: [A, B](as: List[A])(f: A => B)ch71.Par.Par[List[B]]
  //  fork(parMap(List(1,2,3,4,5)){ _ + 10 })(es).get  //> pool-1-thread-1
  //| pool-1-thread-2
  //| pool-1-thread-3
  //| pool-1-thread-4
  //| pool-1-thread-5
  //| pool-1-thread-6
  //| pool-1-thread-8
  //| pool-1-thread-7
  //| pool-1-thread-9
  //| pool-1-thread-10
  //| pool-1-thread-14
  //| pool-1-thread-12
  //| pool-1-thread-15
  //| pool-1-thread-11
  //| pool-1-thread-13
  //| res3: List[Int] = List(11, 12, 13, 14, 15)
}