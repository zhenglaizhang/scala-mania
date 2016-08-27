package net.zhenglai.lib

/*
泛函状态变迁（functional state transition）是一个陌生的课题。泛函状态变迁是通过泛函状态数据类型（functional state）来实现的。State是一个出现在泛函编程里的类型（type）。与其它数据类型一样，State同样需要自身的一套泛函操作函数和组合函数（combinators）

//java code
val rng = new java.util.Random                    //> rng  : java.util.Random = java.util.Random@48533e64

rng.nextInt                                       //> res0: Int = -1412360869
rng.nextInt                                       //> res1: Int = 645622520

rng.nextDouble                                    //> res2: Double = 0.4216477872043267
rng.nextDouble                                    //> res3: Double = 2.306856098814869E-4

这个肯定不是泛函风格的RNG：同一个函数每次引用会产生不同的结果。泛函函数（pure function）的“等量替换”在这里不成立。再者，我们不难想象在以上rng里一定维护了一个状态，每次更新，产生了附带影响（side effect），这又违背了泛函纯函数（pure function）的不产生附带影响的要求（referencial transparency）

泛函的做法重点在于用明确的方式来更新状态，即：不要维护内部状态，直接把新状态和结果一道返回
 */
trait RNG {

  // 泛函状态变迁（state transition）的signature
  def nextInt: (Int, RNG)
}

object testonly {


  case class Bar()

  case class FooState()

  abstract class Foo {
    var s: FooState = ???

    def bar: Bar

    def baz: Int
  }

  // 如果 bar 和 baz 会改变 Foo 的状态，那么我们应该这样设计bar, baz 函数：
  trait Foo_1 {
    def bar: (Bar, Foo_1)

    def baz: (Int, Foo_1)
  }

}