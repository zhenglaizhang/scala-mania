/*
I like to prefix the typeclass name with Can borrowing from CanBuildFrom, and name its method as verb + s, borrowing from sjson/sbinary. Since yesno doesn’t make much sense, let’s call ours truthy. Eventual goal is to get 1.truthy to return true. The downside is that the extra s gets appended if we want to use typeclass instances as functions like CanTruthy[Int].truthys(1).
 */

trait CanTruthy[A] { self =>

  /* @return true, if `a` is truthy. */
  def truthys(a: A): Boolean
}

object CanTruthy {
  def apply[A](implicit ev: CanTruthy[A]): CanTruthy[A] = ev

  def truthys[A](f: A => Boolean): CanTruthy[A] = new CanTruthy[A] {
    /* @return true, if `a` is truthy. */
    override def truthys(a: A): Boolean = f(a)
  }
}


trait CanTruthyOps[A] {
  def self: A
  implicit def F: CanTruthy[A]
  final def truthy: Boolean = F.truthys(self)
}


object ToCanTruthyOps {
  implicit def toCanTruthyOps[A](v: A)(implicit ev: CanTruthy[A]) = new CanTruthyOps[A] {
    def self = v
    implicit def F: CanTruthy[A] = ev
  }
}

implicit val intCanTruthy: CanTruthy[Int] = CanTruthy.truthys({
  case 0 => false
  case _ => true
})

import ToCanTruthyOps._

10.truthy


implicit def listCanTruthy[A]: CanTruthy[List[A]] = CanTruthy.truthys({
  case Nil => false
  case _   => true
})

List("foo").truthy

/*
Nil.truthy
<console>:23: error: could not find implicit value for parameter ev: CanTruthy[scala.collection.immutable.Nil.type]
              Nil.truthy

It looks like we need to treat Nil specially because of the nonvariance.
*/

implicit val nilCanTruthy: CanTruthy[scala.collection.immutable.Nil.type] = CanTruthy.truthys(_ => false)

Nil.truthy


// And for Boolean using identity:
implicit val booleanCanTruthy: CanTruthy[Boolean] = CanTruthy.truthys(identity)

false.truthy


/*
Using CanTruthy typeclass, let’s define truthyIf like LYAHFGG:

Now let’s make a function that mimics the if statement, but that works with YesNo values.

To delay the evaluation of the passed arguments, we can use pass-by-name:
 */

// TODO bugfix
def truthyIf[A: CanTruthy, B, C](cond: A)(ifyes: => B)(ifno: => C) =
  if (cond.truthy) ifyes
  else ifno

truthyIf (Nil) {"This is the Nil"} {"This is not Nil"}

truthyIf (2 :: 3 :: 4 :: Nil) {"YEAH!"} {"NO!"}

truthyIf (true) {"YEAH!"} {"NO!"}