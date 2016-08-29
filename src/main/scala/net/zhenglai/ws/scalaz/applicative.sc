/*
typeclass：Applicative－idomatic function application


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
*/