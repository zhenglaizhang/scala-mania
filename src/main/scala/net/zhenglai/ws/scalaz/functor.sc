/*
And now, we’re going to take a look at the Functor typeclass, which is basically for things that can be mapped over.


trait GenericFunctor[->>[_, _], ->>>[_, _], F[_]] {
  def fmap[A, B](f: A ->> B): F[A] ->>> F[B]
}

trait Functor[F[_]] extends GenericFunctor[Function, Function, F] {

  final def fmap[A, B](as: F[A])(f: A => B): F[B] = fmap(f)(as)
}


For the sake of simplicity we will stick to the more specific Functor definition throughout the rest of this post. Such a functor is an endofunctor, because its source and target are the same (the category of Scala types and Scala functions). Maybe you remember that such a functor can be regarded as a provider of a computational context: The function f: A => B you give to fmap is lifted into the functor’s context which means that it is executed (maybe once, maybe several times or maybe even not at all) under the control of the functor.


 how the OptionFunctor defined in the previous blog post is working: If we give fmap a Some the given function will be invoked, if we give it a None it won’t be invoked.

 So far, so good. Using functors we can lift functions of arity-1 into a computational context.


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

Here are the injected operators it enables:

trait FunctorOps[F[_],A] extends Ops[F[A]] {
  implicit def F: Functor[F]
  ////
  import Leibniz.===

  final def map[B](f: A => B): F[B] = F.map(self)(f)

  ...
}

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
F.lift((s: String) => s.length)(Item3("Morning", "Noon", "Night"))

/*
虽然函数升格（function lifting (A => B) => (F[A] => F[B])是Functor的主要功能，但我们说过：一旦能够获取Item3类型的Functor实例我们就能免费使用所有的注入方法：

scalaz提供了Function1的Functor实例。Function1 Functor的map就是 andThen 也就是操作方调换的compose
 */

val f1 = (_: Int) + 1
val f1a = f1 map (_ * 3)
f1a(2) // andThen

(((_: Int) + 1) map ((k: Int) => k * 3))(2)

(((_: Int) + 1) map ((_: Int) * 3))(2)

(((_: Int) + 1) andThen ((_: Int) * 3))(2)

(((_: Int) * 3) compose ((_: Int) + 1))(2)

/*
我们也可以对Functor进行compose：

*/

val f = Functor[List] compose F
val item3 = Item3("Morning", "Noon", "Night")

f.map(List(item3, item3))(_.length)

val rf = F compose Functor[List]
rf.map(Item3(List("1"), List("22"), List("333")))(_.length)

/*
我们再试着在Item3类型上调用那些免费的注入方法：
Functor also enables some operators that overrides the values in the data structure like >|, as, fpair, strengthL, strengthR, and void:

scala> List(1, 2, 3) >| "x"
res47: List[String] = List(x, x, x)

scala> List(1, 2, 3) as "x"
res48: List[String] = List(x, x, x)

scala> List(1, 2, 3).fpair
res49: List[(Int, Int)] = List((1,1), (2,2), (3,3))

scala> List(1, 2, 3).strengthL("x")
res50: List[(String, Int)] = List((x,1), (x,2), (x,3))

scala> List(1, 2, 3).strengthR("x")
res51: List[(Int, String)] = List((1,x), (2,x), (3,x))

scala> List(1, 2, 3).void
res52: List[Unit] = List((), (), ())
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

/*
scalaz提供了许多现成的Functor实例。我们先看看一些简单直接的实例：
*/
Functor[List].map(List(1, 2, 3))(_ + 3)
Functor[Option].map(Some(3))(_ + 3)
Functor[java.util.concurrent.Callable]
Functor[Stream]
Functor[Vector]

/*
对那些多个类型变量的类型我们可以采用部分施用方式：即type lambda来表示。一个典型的类型：Either[E,A]，我们可以把Left[E]固定下来: Either[String, A]，我们可以用type lambda来这样表述：
*/

Functor[({ type l[x] = Either[String, x] })#l].map(Right(3))(_ + 3)
/*
如此这般我可以对Either类型进行map操作了。

函数类型的Functor是针对返回类型的：
*/

Functor[({ type l[x] = String => x })#l].map((s: String) => s + "!")(_.length)("Hello")

Functor[({ type l[x] = (String, Int) => x })#l].map((s: String, i: Int) => s.length + i)(_ * 10)("Hello", 5)

Functor[({ type l[x] = (String, Int, Boolean) => x })#l].map((s: String, i: Int, b: Boolean) => s + i.toString + b.toString)(_.toUpperCase)("Hello", 3, true)

/*
tuple类型的Functor是针对最后一个元素类型的：

So this defines map method, which accepts a function A => B and returns F[B]. We are quite familiar with map method for collections:

scala> List(1, 2, 3) map {_ + 1}
res15: List[Int] = List(2, 3, 4)
Scalaz defines Functor instances for Tuples.

scala> (1, 2, 3) map {_ + 1}
res28: (Int, Int, Int) = (1,2,4)
Note that the operation is only applied to the last value in the Tuple,
 */
Functor[({ type l[x] = (String, x) })#l].map(("a", 1))(_ + 2)

Functor[({ type l[x] = (String, Int, x) })#l].map(("a", 1, "b"))(_.toUpperCase)

Functor[({ type l[x] = (String, Int, Boolean, x) })#l].map(("a", 1, true, Item3("a", "b", "c")))(i => i.map(_.toUpperCase))

(1, 2, 3) map { _ + 1 }

List(1, 2, 3) map { _ + 1 }

/*
Function as Functors

Scalaz also defines Functor instance for Function1.


map => andThen


How are functions functors? …

What does the type fmap :: (a -> b) -> (r -> a) -> (r -> b) for this instance tell us? Well, we see that it takes a function from a to b and a function from r to a and returns a function from r to b. Does this remind you of anything? Yes! Function composition!
*/

val ff = ((x: Int) => x + 1) map { _ * 7 }
ff(3)

val fff = ((x: Int) => x + 1) ∘ { _ * 7 }
fff(4)

/*
This is interesting. Basically map gives us a way to compose functions, except the order is in reverse from f compose g. No wonder Scalaz provides ∘ as an alias of map. Another way of looking at Function1 is that it’s an infinite map from the domain to the range. Now let’s skip the input and output stuff and go to
 */

/*
In Haskell, the fmap seems to be working as the same order as f compose g. Let’s check in Scala using the same numbers:
 */

(((_: Int) * 3) map { _ + 100 })(1)

/*
 Scalaz:

final def map[B](f: A => B): F[B] = F.map(self)(f)
So the order is completely different. Since map here’s an injected method of F[A], the data structure to be mapped over comes first, then the function comes next.
 */

// TODO how is this working?
List(1, 2, 3) map { 4* }
List(1, 2, 3) map { 4 * _ }

// (a => b) map (b => c) ===> (a => c)
val f4 = ((x: Int) => s"hello $x") map { _.length }
f4(1)
f4(1000)

/*
[We can think of fmap as] a function that takes a function and returns a new function that’s just like the old one, only it takes a functor as a parameter and returns a functor as the result. It takes an a -> b function and returns a function f a -> f b. This is called lifting a function.
*/

val f5 = Functor[List].lift { (_: Int) * 2 }
f5(List(1, 2, 3, 4))
