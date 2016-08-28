package net.zhenglai.lib
import java.util.concurrent.{Callable, TimeUnit}


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
  def run[A](pa: ParObs[A]): A = ???  //由于Par的意义从容器变成运算描述，我们把get重新命名为run
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

object par {
  import java.util.concurrent.{ExecutorService, Future}

  /*
我们不需要进入多线程编程底层细节，用java Concurrent ExecutorService足够了。ExecutorService提供了以Callable形式向系统提交需运算任务方式；系统立即返回Future,我们可以用Future.get以锁定线程方式读取运算。由于运算结果读取是以锁定线程（blocking）形式进行的，那么使用get的时间节点就很重要了：如果提交一个运算后下一步直接get就会立即锁定线程直至运算完成，那我们就无法得到任何并行运算效果了。Future还提供了运行状态和中断运行等功能为编程人员提供更强大灵活的运算控制。为了获取更灵活的控制，Par的返回值应该从直接锁定线程读取A改成不会产生锁定线程效果的Future：
   */

  /*
  现在Par的含义又从一个数据类型变成了一个函数描述了：传入一个ExecutorService，返回Future。我们可以用run来运行这个函数，系统会立即返回Future,无需任何等待。
   */
  type Par[A] = ExecutorService => Future[A]
  def run[A](es: ExecutorService)(pa: Par[A]): Future[A] = pa(es)


  def unit[A](a: A): Par[A] = es => {
    new Future[A] {

      override def isCancelled: Boolean = false

      override def get(): A = a;

      override def get(timeout: Long, unit: TimeUnit): A = get

      override def cancel(mayInterruptIfRunning: Boolean): Boolean = false

      override def isDone: Boolean = true
    }
  }

  def fork[A](pa: Par[A]): Par[A] = es => {
    es.submit(new Callable[A] {
      override def call(): A = run(es)(pa).get
    })
  }

  def async[A](a: => A): Par[A] = fork(unit(a))
}