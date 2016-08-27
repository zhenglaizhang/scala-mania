import net.zhenglai.lib.{RNG, seedRNG}
import net.zhenglai.lib.RNG._

/*
函数nextInt返回了一个随意数及新的RNG。如果我们使用同一个RNG产生的结果是一样的r2==r3，恰恰体现了泛函风格。
 */
val rng = seedRNG(System.currentTimeMillis())
val (i, rng2) = rng.nextInt
val (j, rng3) = rng2.nextInt
val (k, rng4) = rng2.nextInt




val (d, rng5) = nextDouble(rng)                   //> d  : Double = 0.6090536781628866
//| rng5  : ch6.rng.RNG = seedRNG(85716684903065)
val (b, rng6) = nextBoolean(rng5)                 //> b  : Boolean = false
//| rng6  : ch6.rng.RNG = seedRNG(123054239736112)
val ((i5,d2), rng7) = nextIntDouble(rng6)         //> i5  : Int = 1054924659
//| d2  : Double = 0.8877875771782303
//| rng7  : ch6.rng.RNG = seedRNG(124944993788778)
val (ints, rng8) = nextInts(5)(rng7)              //> ints  : List[Int] = List(-782449771, -1992066525, -825651621, -440562357, 7
//| 00809062)
//| rng8  : ch6.rng.RNG = seedRNG(230196348539649)

/*
从以上的例子中可以发现这些函数一致的款式：func(RNG):(A,RNG)，即：RNG => (A,RNG)， 是lambda function，纯纯的函数类型申明。这样看来随意数产生器就是一个函数类型，我们可以把产生器当作函数的参数或者返回值来使用。
 */


// lambda of Func(RNG):(A, RNG)
type RAND[+A] = RNG => (A, RNG)

def rnInt: RAND[Int] = _.nextInt
def rnDouble: RAND[Double] = nextDouble
def rnPositiveINt: RAND[Int] = nextPositiveInt

/*
函数申明 def rnInt: Rand[Int] 好像没有参数，但使用时 rnInt(rng) 是需要参数的。不过如果我们想想 Func(RNG):(A,RNG) 的lambda表达形式 RNG => (A,RNG)自然就理解了。
 */
val (x, rng10) = rnInt(rng)
rnPositiveINt(rng)


import _root_.net.zhenglai.lib._
rnDouble2(rng)