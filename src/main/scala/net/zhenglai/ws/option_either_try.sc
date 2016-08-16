import scala.util.{Failure, Success, Try}


type Or[A, B] = Either[A, B]
val l: String Or Int = Left("boo")

def positive(i: Int): Option[Int] = if (i > 0) Some(i) else None


positive(12)
positive(-12)

positive2(12).right
positive2(-12).right

def positive2(i: Int): String Or Int = if (i > 0) Right(i) else Left(s"Non positive number $i")
l.left
l.right

l.left map (_.length)
for (s <- l.left) yield s.length

l.right map (_.toDouble) // nothing changed

def addInts(s1: String, s2: String): NumberFormatException Or Int = {
  try {
    Right(s1.toInt + s2.toInt)
  } catch {
    case ex: NumberFormatException => Left(ex)
  }
}


println(addInts("1", "2"))
println(addInts("", "2"))


def positive3(i: Int): Try[Int] = Try {
  assert(i > 0, s"nonpositive number $i")
  i
}

positive3(12)
positive3(-12)


def positive4(i: Int): Try[Int] =
  if (i > 0) Success(0)
  else Failure(new AssertionError("assertion failed"))

// focus on happy path, and let Try capture errors

for {
  i1 <- positive4(5)
  i2 <- positive4(-1 * i1) // EPIC FAIL!
  i3 <- positive4(25 * i2)
  i4 <- positive4(-2 * i3) // EPIC FAIL!
} yield i1 + i2 + i3 + i4

