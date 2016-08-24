import scala.annotation.tailrec
/*
Defensive copying is not needed, because the list is immutable.


 */


def append[A](a1: List[A], a2: List[A]): List[A] = a1 match {
  case Nil          => a2
  case head +: tail => head +: append(tail, a2)
}

append(List(1, 2, 3), List(4, 5, 6))


@tailrec
def drop[A](l: List[A], n: Int): List[A] = {
  if (n == 0) l
  else l match {
    case Nil          => Nil
    case head +: tail => drop(tail, n - 1)
  }
}

drop(List(1, 2, 3), 1)
drop(List(1, 2, 3), 3)
drop(List(1, 2, 3), 10)



def dropWhile[A](xs: Seq[A])(p: A => Boolean): Seq[A] = xs match {
  case head +: tail if (p(head)) => dropWhile(tail)(p)
  case _ => xs
}

dropWhile(Seq(1, 2, 3))(_ < 3)



def foldRight[A, B](xs: Seq[A], z: B)(f: (A, B) => B): B = xs match {
  case Nil => z
  case head +: tail => f(head, foldRight(tail, z)(f))
}


Seq(1, 2, 3).foldLeft(2)(_ + _)



def map[A, B](xs: Seq[A])(f: A => B): Seq[B] = xs match {
  case Nil => Nil
  case head +: tail => f(head) +: map(tail)(f)
}

map(Seq(1, 2, 3, 4))(_ * 2)


