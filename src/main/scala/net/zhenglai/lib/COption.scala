package net.zhenglai.lib

import org.scalacheck.{ Arbitrary, Gen }

import scalaz.Scalaz._
import scalaz._
import scalaz.scalacheck.ScalaCheckBinding._

/*
Breaking the law
 */
sealed trait COption[+A]

case class CSome[A](counter: Int, a: A) extends COption[A]

case object CNone extends COption[Nothing]

object COption {
  implicit val coptionFunctor = new Functor[COption] {
    def map[A, B](fa: COption[A])(f: A => B): COption[B] = fa match {
      case CNone       => CNone
      case CSome(c, a) => CSome(c + 1, f(a))
    }
  }

  implicit def coptionEqual[A]: Equal[COption[A]] = Equal.equalA

  // TODO
  // test:console
  // functor.laws[net.zhenglai.lib.COption].check()
  implicit def COptionArbiterary[A](implicit a: Arbitrary[A]): Arbitrary[COption[A]] =
    a map { a => (CSome(0, a): COption[A]) }
}

