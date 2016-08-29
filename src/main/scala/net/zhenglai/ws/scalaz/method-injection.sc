
import scalaz.Scalaz._
import scalaz._

/*
Method injection (enrich my library)

If we were to write a function that sums two types using the Monoid, we need to call it like this.
*/

def plus[A: Monoid](a1: A, a2: A): A = implicitly[Monoid[A]].append(a1, a2)

plus(3, 4)

/*
We would like to provide an operator. But we don’t want to enrich just one type, but enrich all types that has an instance for Monoid. Let me do this in Scalaz 7 style.
*/

trait MonoidOp[A] {
  val F    : Monoid[A]
  val value: A

  def |+|(a2: A) = F.append(value, a2)
}

implicit def toMonoidOp[A: Monoid](a: A): MonoidOp[A] = new MonoidOp[A] {
  val F: Monoid[A] = implicitly[Monoid[A]]
  val value = a
}

3 |+| 4
"a" |+| "b"

/*
We were able to inject |+| to both Int and String with just one definition.
*/



/*
Standard type syntax

Using the same technique, Scalaz also provides method injections for standard library types like Option and Boolean:
*/

1.some | 2
Some(1).getOrElse(2)

(1 > 10)? 1 | 2

if (1 > 10) 1 else 2
