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

函数nextInt返回了一个随意数及新的RNG。如果我们使用同一个RNG产生的结果是一样的r2==r3，恰恰体现了泛函风格。

所有类型的泛函式随意数产生器都可以从Int RNG nextInt推导出来：
 */
trait RNG {

  // 泛函状态变迁（state transition）的signature
  def nextInt: (Int, RNG)
}

//起始状态RNG, 种子RNG
case class seedRNG(seed: Long) extends RNG {
  // 泛函状态变迁（state transition）的signature
  override def nextInt: (Int, RNG) = {
    val seed2 = (seed*0x5DEECE66DL + 0xBL) & ((1L << 48) - 1)
    ((seed2 >>> 16).asInstanceOf[Int], seedRNG(seed2))
  }
}

object RNG {
  //值在 0.0 - 1.0 之间的Double随意数
  def nextDouble(rng: RNG): (Double, RNG) = {
    val (i,rng2) = rng.nextInt
    if ( i == Int.MaxValue ) (0.0, rng2)
    else ( i.toDouble / Int.MaxValue.toDouble, rng2)
  }
  def nextPositiveInt(rng: RNG): (Int, RNG) =  {
    val (i, rng2) = rng.nextInt
    if ( i == Int.MaxValue ) (Int.MaxValue, rng2)
    else (i.abs, rng2)
  }
  def nextBoolean(rng: RNG): (Boolean, RNG) = {
    rng.nextInt match {
      case (i, rng2) => (i % 2 == 0, rng2)
    }
  }
  //产生一个随意tuple (Int, Double)
  def nextIntDouble(rng: RNG): ((Int, Double), RNG) = {
    val (i,rng2) = nextPositiveInt(rng)
    val (d,rng3) = nextDouble(rng2)
    ((i,d),rng3)
  }
  //产生一个随意数的n长度List
  def nextInts(n: Int)(rng: RNG): (List[Int], RNG) = {
    def go(n: Int, rng: RNG, acc: List[Int]): (List[Int], RNG) = {
      if ( n <= 0 ) (acc, rng)
      else {
        val (i,rng2) = rng.nextInt
        go(n-1,rng2,i :: acc)
      }
    }
    go(n,rng,Nil: List[Int])
  }
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