
val rand = new scala.util.Random
rand.nextInt()
rand.nextDouble()
rand.nextDouble()

rand.nextInt(10)

(1 to 100).map(x => rollDie).groupBy(x => x).mapValues(x => x.size).foreach(println)

/* 0 to 5 off-by-one error */
def rollDie: Int = {
  val rand = new scala.util.Random
  rand.nextInt(6)
}

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
val (n1, rand3) = rand2.nextInt
val (n2, rand4) = rand3.nextInt

rand3.nextInt

case class FooState()

case class Bar()

case class Baz()

abstract class FooOld {
  private var fooState: FooState = ???

  def bar: Bar

  def baz: Int
}

abstract class FooNew {
  def bar: (Bar, FooNew)
  def baz: (Int, FooNew)
}

// a state action
type Rand[+A] = RNG => (A, RNG)

val int: Rand[Int] = _.nextInt

/*
a simple RNG state transition is the unit action which passes the RNG state through without using it, always returning a constant value rather than a random value
 */
def unit[A](a: A): Rand[A] = rng => (a, rng)

def map[A, B](s: Rand[A])(f: A => B): Rand[B] = rng => {
  val (a, rng2) = s(rng)
  (f(a), rng2)
}