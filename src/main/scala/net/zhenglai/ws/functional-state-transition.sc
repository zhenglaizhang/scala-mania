import net.zhenglai.lib.seedRNG

/*
函数nextInt返回了一个随意数及新的RNG。如果我们使用同一个RNG产生的结果是一样的r2==r3，恰恰体现了泛函风格。
 */
val rng = seedRNG(System.currentTimeMillis())
val (i, rng2) = rng.nextInt
val (j, rng3) = rng2.nextInt
val (k, rng4) = rng2.nextInt
