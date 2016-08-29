/*
Functor是范畴学（Category theory）里的概念。不过无须担心，我们在scala FP编程里并不需要先掌握范畴学知识的。在scalaz里，Functor就是一个普通的typeclass，具备map over特性。我的理解中，Functor的主要用途是在FP过程中更新包嵌在容器（高阶类）F[T]中元素T值。典型例子如：List[String], Option[Int]等。我们曾经介绍过FP与OOP的其中一项典型区别在于FP会尽量避免中间变量（temp variables）。FP的变量V是以F[V]这种形式存在的，如：List[Int]里一个Int变量是包嵌在容器List里的。所以FP需要特殊的方式来更新变量V，这就是Functor map over的意思。scalaz提供了Functor typeclass不但使用户能map over自定义的高阶类型F[T]，并且用户通过提供自定义类型的Functor实例就可以免费使用scalaz Functor typeclass提供的一系列组件函数（combinator functions）。


* Functors, covariant by nature if not by Scala type.  Their key
 * operation is `map`, whose behavior is constrained only by type and
 * the functor laws.
 *
 * Many useful functors also have natural [[scalaz.Apply]] or
 * [[scalaz.Bind]] operations.  Many also support
 * [[scalaz.Traverse]].

trait Functor[F[_]] extends InvariantFunctor[F] { self =>
  ////
  import Liskov.<~<

  /** Lift `f` into `F` and apply to `F[A]`. */
  def map[A, B](fa: F[A])(f: A => B): F[B]

...


任何类型的实例只需要实现这个抽象函数map就可以使用scalaz Functor typeclass的这些注入方法了：scalaz/syntax/FunctorSyntax.scala

final class FunctorOps[F[_],A] private[syntax](val self: F[A])(implicit val F: Functor[F]) extends Ops[F[A]] {
  ////
  import Leibniz.===
  import Liskov.<~<

  final def map[B](f: A => B): F[B] = F.map(self)(f)
  final def distribute[G[_], B](f: A => G[B])(implicit D: Distributive[G]): G[F[B]] = D.distribute(self)(f)
  final def cosequence[G[_], B](implicit ev: A === G[B], D: Distributive[G]): G[F[B]] = D.distribute(self)(ev(_))
  final def cotraverse[G[_], B, C](f: F[B] => C)(implicit ev: A === G[B], D: Distributive[G]): G[C] = D.map(cosequence)(f)
  final def ∘[B](f: A => B): F[B] = F.map(self)(f)
  final def strengthL[B](b: B): F[(B, A)] = F.strengthL(b, self)
  final def strengthR[B](b: B): F[(A, B)] = F.strengthR(self, b)
  final def fpair: F[(A, A)] = F.fpair(self)
  final def fproduct[B](f: A => B): F[(A, B)] = F.fproduct(self)(f)
  final def void: F[Unit] = F.void(self)
  final def fpoint[G[_]: Applicative]: F[G[A]] = F.map(self)(a => Applicative[G].point(a))
  final def >|[B](b: => B): F[B] = F.map(self)(_ => b)
  final def as[B](b: => B): F[B] = F.map(self)(_ => b)
  final def widen[B](implicit ev: A <~< B): F[B] = F.widen(self)
  ////
}
*/


/*
Functor必须遵循一些定律：
  1、map(fa)(x => x) === fa
  2、map(map(fa)(f1))(f2) === map(fa)(f2 compose f1)

trait FunctorLaw extends InvariantFunctorLaw {
    /** The identity function, lifted, is a no-op. */
    def identity[A](fa: F[A])(implicit FA: Equal[F[A]]): Boolean = FA.equal(map(fa)(x => x), fa)

    /**
     * A series of maps may be freely rewritten as a single map on a
     * composed function.
     */
  def composite[A, B, C](fa: F[A], f1: A => B, f2: B => C)(implicit FC: Equal[F[C]]): Boolean = FC.equal(map(map(fa)(f1))(f2), map(fa)(f2 compose f1))
}
*/

import scalaz.Functor
import scalaz.Scalaz._


// identity law => map(fa)(x => x) === fa
assert(List(1, 2, 3).map(x => x) === List(1, 2, 3))

assert(List(1, 2, 3).map(identity) === List(1, 2, 3))

// composibility
// map(map(fa)(f1))(f2) === map(fa)(f2 compose f1)

val x = Functor[List].map(List(1, 2, 3).map(i => i + 1))(i2 => i2 * 3)
val y = List(1, 2, 3).map(((i2: Int) => i2 * 3) compose ((i: Int) => i + 1))

assert(x === y)

val z = List(1, 2, 3).map(((i: Int) => i + 1) compose ((i: Int) => i * 3))

//assert(x === z)


/*
针对我们自定义的类型，我们只要实现map函数就可以得到这个类型的Functor实例。一旦实现了这个类型的Functor实例，我们就可以使用以上scalaz提供的所有Functor组件函数了。
*/

case class Item3[A](a: A, b: A, c: A)
implicit val item3Functor = new Functor[Item3] {
  def map[A, B](ia: Item3[A])(f: A => B): Item3[B] = Item3(f(ia.a), f(ia.b), f(ia.c))
}

/*
scalaz同时在scalaz-tests下提供了一套scalacheck测试库。我们可以对Item3的Functor实例进行测试：
*/

// TODO
//item3Functor.laws[Item3].check
/*
1 scala> functor.laws[Item3].check
2 <console>:27: error: could not find implicit value for parameter af: org.scalacheck.Arbitrary[Item3[Int]]
3               functor.laws[Item3].check
4                           ^
 */
// 看来我们需要提供自定义类型Item3的随意产生器（Generator）：

/*
scala> implicit def item3Arbi[A](implicit a: Arbitrary[A]): Arbitrary[Item3[A]] = Arbitrary {
     | def genItem3: Gen[Item3[A]]  = for {
     | b <- Arbitrary.arbitrary[A]
     | c <- Arbitrary.arbitrary[A]
     | d <- Arbitrary.arbitrary[A]
     | } yield Item3(b,c,d)
     | genItem3
     | }
item3Arbi: [A](implicit a: org.scalacheck.Arbitrary[A])org.scalacheck.Arbitrary[Item3[A]]

scala> functor.laws[Item3].check
+ functor.invariantFunctor.identity: OK, passed 100 tests.
+ functor.invariantFunctor.composite: OK, passed 100 tests.
+ functor.identity: OK, passed 100 tests.
+ functor.composite: OK, passed 100 tests.
 */



/*
Item3的Functor实例是合理的。

实际上map就是(A => B) => (F[A] => F[B])，就是把(A => B)升格（lift）成（F[A] => F[B]）:
*/

val F = item3Functor

F.map(Item3("Morning", "Noon", "Night"))(_.length)
/** Alias for `map`. */
F.apply(Item3("Morning", "Noon", "Night"))(_.length)
F(Item3("Morning", "Noon", "Night"))(_.length)
/** Lift `f` into `F`. */
F.lift((s: String) => s.length)(Item3("Morning","Noon","Night"))

/*
虽然函数升格（function lifting (A => B) => (F[A] => F[B])是Functor的主要功能，但我们说过：一旦能够获取Item3类型的Functor实例我们就能免费使用所有的注入方法：

scalaz提供了Function1的Functor实例。Function1 Functor的map就是 andThen 也就是操作方调换的compose
 */

val f1 = (_: Int) + 1
val f1a = f1 map (_ * 3)
f1a(2) // andThen

(((_: Int) + 1) map((k: Int) => k * 3))(2)

(((_: Int) + 1) map((_: Int) * 3))(2)

(((_: Int) + 1) andThen ((_: Int) * 3))(2)

(((_: Int) * 3) compose ((_: Int) + 1))(2)




/*
我们也可以对Functor进行compose：
*/

val f = Functor[List] compose F
val item3 = Item3("Morning","Noon","Night")

f.map(List(item3, item3))(_.length)


val rf = F compose Functor[List]
rf.map(Item3(List("1"),List("22"),List("333")))(_.length)


/*
我们再试着在Item3类型上调用那些免费的注入方法：
*/
item3.fpair
item3.strengthL(3)
/** Inject `b` to the right of `A`s in `f`. */
item3.strengthR(3)
/** Pair all `A`s in `fa` with the result of function application. */
item3.fproduct(_.length)
item3 as "Day"
item3 >| "Day"
item3.void

/*
我现在还没有想到这些函数的具体用处。不过从运算结果来看，用这些函数来产生一些数据模型用在游戏或者测试的模拟（simulation）倒是可能的。
*/
