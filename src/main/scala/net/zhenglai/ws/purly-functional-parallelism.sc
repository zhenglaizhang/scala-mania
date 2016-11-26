
def sum(xs: Seq[Int]): Int = xs.foldLeft(0)((a, b) => a + b)
sum(Seq(1, 2, 3, 4))

def sum(xs: IndexedSeq[Int]): Int = {
  if (xs.size <= 1) xs.headOption getOrElse 0
  else {
    val (l, r) = xs.splitAt(xs.size / 2)
    sum(l) + sum(r)
  }
}

sum(Array(1, 2, 3, 4))

/*
container type for our result, Par[A] (for parallel ),
 */
trait Par[A] {
  def unit[A](a: => A): Par[A]

  def get[A](a: Par[A])
}

