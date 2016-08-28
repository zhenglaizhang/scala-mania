package net.zhenglai.lib

import java.util.concurrent.Executors

import org.scalatest.{FlatSpec, Matchers}


/*
从应用例子里我们可以了解线程的管理是由现有的java工具提供的（Executors.newCachedThreadPool）,我们无须了解线程管理细节。我们同时确定了线程的管理机制与我们设计的并行运算Par是松散耦合的。

注意：unit并没有使用ExecutorService es, 而是直接返回一个注明完成运算（isDone=true）的Future，这个Future的get就是unit的传入参数a。如果我们再用这个Future的get来得取表达式的运算结果的话，这个运算是在当前主线程中运行的。async通过fork选择新的线程；并向新的运行环境提交了运算任务。我们来分析一下运算流程：

1、val a = unit(4+7)，unit构建了一个完成的 new Future; isDone=true,设置了 Future.get = 4 + 7，run(es)(a)在主线程中对表达式 4+7 进行了运算并得取结果 11。

2、val b = async(2+1) >>> fork(unit(2+1)), run(es)(b) >>> submit(new Callable), 注意 def call = run(es)(b).get ： 这里提交的运算run(es)(b).get实际上又提交了一次运算并直接锁定线程（blocking）等待读取运算结果。第一次提交Callable又需要锁定线程等待提交运算完成计算。如果线程池只能提供一个线程的话，第一次提交了Callable会占用这个唯一的线程并等待第二次提交运算得出的结果，由于没有线程可以提供给二次提交运算，这个运算永远无法得到结果，那么run(es)(b).get就会产生死锁了（dead lock）。


我们在这节介绍了一个简单的泛函并行组件库设计，可以把一个运算放到主线程之外的另一个新的线程中计算。但是抽取运算结果却还是会锁定线程（blocking）。我们下一节将会讨论如何通过一些算法函数来实现并行运算。
 */
class ParSpec extends FlatSpec with Matchers {

  import Par._

  "A Par" should "execute successfully and return correct result" in {
    val a = Par.unit(4 + 7)

    val b = async(2 + 1)

    val es = Executors.newCachedThreadPool()

    Par.run(es)(a).get should be(11)
    Par.run(es)(b).get should be(3)

    es.shutdown();
  }

  // one buggy tests :)
  "A async Par" should "execute on different background thread" in {
    val a = Par.unit {
      println(Thread.currentThread().getName, 42 + 1)
    }
    val b = Par.async {
      println(Thread.currentThread().getName, 42 + 1)
    }

    val es = Executors.newCachedThreadPool()
    /*
(pool-60-thread-3-ScalaTest-running-ParSpec,43)
(pool-63-thread-1,43)
     */
    Par.run(es)(a).get should equal(Par.run(es)(b).get)

    es.shutdown();
  }

  "map2 function f" should "execute in main thread" in {
    val es = Executors.newCachedThreadPool()
    map2(
      async {
        println(Thread.currentThread().getName)
        41 + 2
      },
      async {
        println(Thread.currentThread().getName)
        33 + 4
      }) { (a, b) => {
      (Thread.currentThread().getName, a + b)
    }
    }(es).get should be(Thread.currentThread().getName, 43 + 37)

    es.shutdown()
  }

  "map2 function f" should "execute in pool thread via fork" in {
    val es = Executors.newCachedThreadPool()
    fork(map2(
      async {
        println(Thread.currentThread().getName)
        41 + 2
      },
      async {
        println(Thread.currentThread().getName)
        33 + 4
      }) { (a, b) => {
      (Thread.currentThread().getName, a + b)
    }
    })(es).get should not be(Thread.currentThread().getName, 43 + 37)

    es.shutdown()
  }

}
