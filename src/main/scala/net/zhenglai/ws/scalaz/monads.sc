/*
Monads are a natural extension applicative functors, and they provide a solution to the following problem: If we have a value with context, m a, how do we apply it to a function that takes a normal a and returns a value with a context.

The equivalent is called Monad in Scalaz. Here’s the typeclass contract:


trait Monad[F[_]] extends Applicative[F] with Bind[F] { self =>
  ////
}


trait Bind[F[_]] extends Apply[F] { self =>
  /** Equivalent to `join(map(fa)(f))`. */
  def bind[A, B](fa: F[A])(f: A => F[B]): F[B]
}



And here are the operators:

/** Wraps a value `self` and provides methods related to `Bind` */
trait BindOps[F[_],A] extends Ops[F[A]] {
  implicit def F: Bind[F]
  ////
  import Liskov.<~<

  def flatMap[B](f: A => F[B]) = F.bind(self)(f)
  def >>=[B](f: A => F[B]) = F.bind(self)(f)
  def ∗[B](f: A => F[B]) = F.bind(self)(f)
  def join[B](implicit ev: A <~< F[B]): F[B] = F.bind(self)(ev(_))
  def μ[B](implicit ev: A <~< F[B]): F[B] = F.bind(self)(ev(_))
  def >>[B](b: F[B]): F[B] = F.bind(self)(_ => b)
  def ifM[B](ifTrue: => F[B], ifFalse: => F[B])(implicit ev: A <~< Boolean): F[B] = {
    val value: F[Boolean] = Liskov.co[F, A, Boolean](ev)(self)
    F.ifM(value, ifTrue, ifFalse)
  }
  ////
}



It introduces flatMap operator and its symbolic aliases >>= and ∗. We’ll worry about the other operators later. We are use to flapMap from the standard library:


scala> 3.some flatMap { x => (x + 1).some }
res2: Option[Int] = Some(4)

scala> (none: Option[Int]) flatMap { x => (x + 1).some }
res3: Option[Int] = None



Back to Monad:

trait Monad[F[_]] extends Applicative[F] with Bind[F] { self =>
  ////
}
Unlike Haskell, Monad[F[_]] exntends Applicative[F[_]] so there’s no return vs pure issues. They both use point.
*/

import scalaz._
import Scalaz._

Monad[Option].point("WHAT")

9.some flatMap { x => Monad[Option].point(x * 10) }

(none: Option[Int]) flatMap { x => Monad[Option].point(x * 10) }
