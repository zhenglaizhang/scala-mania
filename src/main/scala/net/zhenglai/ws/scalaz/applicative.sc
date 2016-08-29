import scalaz._
import Scalaz._

/*
Refs =>

https://softwaremill.com/applicative-functor/

 */

/*
typeclass：Applicative－idomatic function application


/**
 * Applicative Functor, described in [[http://www.soi.city.ac.uk/~ross/papers/Applicative.html Applicative Programming with Effects]]
 *
 * Whereas a [[scalaz.Functor]] allows application of a pure function to a value in a context, an Applicative
 * also allows application of a function in a context to a value in a context (`ap`).
 *
 * It follows that a pure function can be applied to arguments in a context. (See `apply2`, `apply3`, ... )
 *
 * Applicative instances come in a few flavours:
 *  - All [[scalaz.Monad]]s are also `Applicative`
 *  - Any [[scalaz.Monoid]] can be treated as an Applicative (see [[scalaz.Monoid]]#applicative)
 *  - Zipping together corresponding elements of Naperian data structures (those of of a fixed, possibly infinite shape)
 *
 *  @see [[scalaz.Applicative.ApplicativeLaw]]
 */
////


Applicative，正如它的名称所示，就是FP模式的函数施用（function application）。我们在前面的讨论中不断提到FP模式的操作一般都在管道里进行的，因为FP的变量表达形式是这样的：F[A]，即变量A是包嵌在F结构里的。Scalaz的Applicative typeclass提供了各种类型的函数施用(function application)和升格（lifting）方法。与其它scalaz typeclass使用方式一样，我们只需要实现了针对自定义类型的Applicative实例就可以使用这些方法了。以下是Applicative trait的部分定义：scalaz/Applicative.scala

trait Applicative[F[_]] extends Apply[F] { self =>
  ////
  def point[A](a: => A): F[A]

  // alias for point
  final def pure[A](a: => A): F[A] = point(a)
。。。



我们首先需要实现抽象函数point，然后由于Applicative继承了Apply，我们看看Apply trait有什么抽象函数需要实现的；scalaz/Apply.scala

1 trait Apply[F[_]] extends Functor[F] { self =>
2   ////
3   def ap[A,B](fa: => F[A])(f: => F[A => B]): F[B]
4 。。。


我们还需要实现抽象函数ap。注意Apply又继承了Functor，所以我们还需要实现map，一旦实现了Applicative实例就能同时获取了Functor实例。



Configure[+A]是个典型的FP类型。通过实现特殊命名apply的函数作为类型构建器，我们可以这样构建实例：Configure("some string")。现在我们按照scalaz隐式解析（implicit resolution）惯例在伴生对象（companion object）里定义隐式Applicative实例：
*/

trait Configure[+A] {
  def get: A
}

object Configure {
  implicit val configFunctor = new Functor[Configure] {
    override def map[A, B](fa: Configure[A])(f: (A) => B): Configure[B] = Configure(f(fa.get))
  }

  implicit val configApplicative = new Applicative[Configure] {
    def point[A](a: => A) = Configure(a)

    /*
  由于Apply继承了Functor，我们必须先获取Configure的Functor实例。现在我们可以针对Configure类型使用Applicative typeclass的功能函数了。
     */
//    def ap[A, B](ca: => Configure[A])(cfab: => Configure[A => B]): Configure[B] = cfab map { fab => fab(ca.get) }
    override def ap[A, B](fa: => Configure[A])(f: => Configure[(A) => B]): Configure[B] = ???
  }

  def apply[A](data: => A): Configure[A] = new Configure[A] {
    override def get: A = data
  }

}



/*
So far, when we were mapping functions over functors, we usually mapped functions that take only one parameter. But what happens when we map a function like *, which takes two parameters, over a functor?



Given a function that takes multiple arguments A,B,… that returns a Result, how can that function by applied to arguments M[A], M[B],… and get M[Result]?
where M is an Option, List, etc.


First of all, applicative functor forms a typeclass which can be implemented in Scala. It allows applying a wrapped function to a wrapped value.


We can think of applicative as a type which wraps a value. Having two such wrapped values, we can apply a two-argument function to these values and preserve the outer context (wrapping). If we call this “application” map2, then we can get something like this:

boxed(3), boxed(2) ===> map2(_ + _)  ===> +3, 2 ===> boxed(5)
// boxed(+3)

Having two wrapped values is much more familiar than having a "function wrapped in context”.


INDEPENDENT CALCULATIONS

Monads impose certain structure to the flow. We apply a function to the wrapped value and we receive a new wrapped value which becomes "flattened" with the outer wrapper. This means that getting subsequent wrapped values depends on results of previous calculations.

for {
   user <- getUserFuture()
   photo <- getProfilePhoto(user)
} yield Result(user, photo)
The getProfilePhoto() function depends on user, so calling Future[Photo] is possible only after we fully resolve the previous step, Future[User].


However, we often find cases like this:

val res: Future[Result] = for {
  user <- getUserFuture()
  data <- getAdditionalDataFuture()
}
  yield Result(user, data)
In this example, we deal with independent Future[User] and Future[Data] which we want to uwrap and pass to Result.apply. Why would we need monadic flow, which forces us to view this code as a sequence of steps? Here’s a good case for applicatives. All monads are also applicatives, we can just work with Future, Option and many other well known types. There are even more applicatives than monads

import cats._
import cats.std.future._

val wrappedFunction = getDataFuture().map(data => { (user: User) => Result.apply(user, data)})
Applicative[Future].ap(wrappedFunction)(getUserFuture())

Bear with me, this is an intermediate step. I’m showing this example only to present the apply operation (known as <*> in Haskell). The ap() function allows us to get a wrapped function and fuse it with a wrapped value,

In order to look at applicatives as a way to call a function on two wrapped values, we can use a tool called cartesian builder. It allows expressing our intent as orthogonal composition:

import cats.std.future._
import cats.syntax.cartesian._

(getUserFuture() |@| getDataFuture()).map(Result.apply)







scala> List(1, 2, 3, 4) map {(_: Int) * (_:Int)}
<console>:14: error: type mismatch;
 found   : (Int, Int) => Int
 required: Int => ?
              List(1, 2, 3, 4) map {(_: Int) * (_:Int)}
*/

//List(1, 2, 3, 4) map { (_: Int) * (_: Int)}

val f1 = List(1, 2, 3, 4) map {(_:Int) * (_:Int)}.curried
f1 map {_(9)}


/*
Meet the Applicative typeclass. It lies in the Control.Applicative module and it defines two methods, pure and <*>.

Let’s see the contract for Scalaz’s Applicative:

trait Applicative[F[_]] extends Apply[F] { self =>
  def point[A](a: => A): F[A]

  /** alias for `point` */
  def pure[A](a: => A): F[A] = point(a)

  ...
}


So Applicative extends another typeclass Apply, and introduces point and its alias pure.

LYAHFGG:

pure should take a value of any type and return an applicative value with that value inside it. … A better way of thinking about pure would be to say that it takes a value and puts it in some sort of default (or pure) context—a minimal context that still yields that value.


Scalaz likes the name point instead of pure, and it seems like it’s basically a constructor that takes value A and returns F[A]. It doesn’t introduce an operator, but it introduces point method and its symbolic alias η to all data types.
*/

1.point[List]
1.point[Option]
1.point[Option] map { _ + 2 }
1.point[List] map { _ + 2 }





/*
Apply

You can think of <*> as a sort of a beefed-up(被强化的) fmap. Whereas fmap takes a function and a functor and applies the function inside the functor value, <*> takes a functor that has a function in it and another functor and extracts that function from the first functor and then maps it over the second one.

trait Apply[F[_]] extends Functor[F] { self =>
  def ap[A,B](fa: => F[A])(f: => F[A => B]): F[B]
}

Using ap, Apply enables <*>, *>, and <* operator.
*/

9.some <*> { (_: Int) + 3 }.some

// As expected. *> and <* are variations that returns only the rhs or lhs.

1.some <* 2.some
none <* 2.some

1.some *> 2.some

none *> 2.some