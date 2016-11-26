// efficiency problems, too many temporary lists
List(1, 2, 3, 4).map(_ + 10).filter(_ % 2 == 0).map(_ * 3)

// strict function vs non-strict function

false && {
  println("!!");
  true
}

true && {
  println("!!");
  true
}

val input = Some(12)
val result = if (input.isEmpty) sys.error("empty input") else input
val a = 21

// () => is syntactic alias for Function0[A]
def if2[A](cond: Boolean, onTrue: () => A, onFalse: () => A): A =
  if (cond) onTrue() else onFalse()
if2(a < 22, () => println("less"), () => println("great"))

def if3[A](cond: Boolean, onTrue: => A, onFalse: => A): A =
  if (cond) onTrue else onFalse

if3(false, sys.error("fail"), 3)

def maybeTwice(b: Boolean, i: => Int) = if (b) i + i else 0
val x = maybeTwice(true, { println("hi"); 1 + 41 })

def maybeTwice2(b: Boolean, i: => Int) = {
  lazy val j = i
  if (b) j + j else 0
}

maybeTwice2(true, { println("hi"); 1 + 41 })