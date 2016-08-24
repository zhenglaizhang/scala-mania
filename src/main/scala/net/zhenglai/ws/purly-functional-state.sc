
val rand = new scala.util.Random
rand.nextInt()
rand.nextDouble()
rand.nextDouble()

rand.nextInt(10)


/* 0 to 5 off-by-one error */
def rollDie: Int = {
  val rand = new scala.util.Random
  rand.nextInt(6)
}

(1 to 100).map(x => rollDie).groupBy(x => x).mapValues(x => x.size).foreach(println)


trait RNG {
  def nextInt: (Int, RNG)
}

case class SimpleRNG(seed: Long) extends RNG {
  override def nextInt: (Int, RNG) = {
    // use current seed to generate new seed
    val newSeed = (seed * 0x5DEECE66DL + 0xBL) & 0xFFFFFFFFFFFFL
    val nextRNG = SimpleRNG(newSeed)
    val n = (newSeed >>> 16).toInt
    (n, nextRNG)
  }
}


val rand2 = SimpleRNG(42)
rand2.nextInt
rand2.nextInt
rand2.nextInt
rand2.nextInt

val (n1, rand3) = rand2.nextInt
val (n2, rand4) = rand3.nextInt
rand3.nextInt
