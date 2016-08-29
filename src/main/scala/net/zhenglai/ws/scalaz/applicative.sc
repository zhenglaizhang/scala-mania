import scalaz._

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