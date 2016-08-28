/*
Monad就是泛函编程中最概括通用的数据模型（高阶数据类型）。它不但涵盖了所有基础类型（primitive types）的泛函行为及操作，而且任何高阶类或者自定义类一旦具备Monad特性就可以与任何类型的Monad实例一样在泛函编程中共同提供一套通用的泛函编程方式。所以有人把泛函编程视作Monadic Programming也不为过之。那么，具体什么是Monad呢？


Monoid，我们说过它是一个特殊的范畴（Category），所有数据类型的Monoid实例都共同拥有一套Monoid特有的操作及遵循一套Monoid行为定律。这样我们可以把Monoid视为一个抽象数据模型，在泛函算法中使用特殊的Monoid实例就可以达到预期的效果而不需要修改算法。那么可以说Monad就是一个比Monoid更概括、更抽象、覆盖范畴更广的高阶数据类型了。


实际上在设计泛函库组件（combinator）时，我们会尽量避免重复编码，实现方式就是把通用或共性的操作抽取出来形成一些新的高阶类型（higher types)，也就是新的抽象类型（Abstraction）。这样我们可以在不同的组件库中对同类操作共同使用这些通用的类型了。让我们先看看以下的一个抽象过程：
 */



// 我们在前面讨论过一些数据类型。它们都有一个共同的函数：map
/*
def map[A,B](la: List[A])(f: A => B): List[B]
def map[A,B](oa: Option[A])(f: A => B): Option[B]
def map[A,B](pa: Par[A])(f: A => B): Par[B]
def map[A,B](sa: State[S,A])(f: A => B): State[S,B]
*/

/*
这几个函数都具有高度相似的款式（signature），不同的是它们施用的具体数据类型。那么我们应该可以把这个map抽象出来：通过增加一个高阶类型Functor，用它来概括实现map
 */
trait Functor[F[_]] {
  def map[A, B](a: F[A])(f: A => B): F[B]
}
/*
注意在上面的map例子里的施用类型都是高阶类型；List[A]、Option[A]、Par[A] ...都是F[A]这种形式。所以Functor的类参数是F[_]，即: Functor[List], Functor[Option], Functor[Par] ...,这里面F[_]就是F[A]，A可以是任何类型。
 */

object ListFunctor extends Functor[List] {
  def map[A, B](xs: List[A])(f: A => B): List[B] = xs map f
}

object OptionFunctor extends Functor[Option] {
  def map[A, B](xs: Option[A])(f: A => B): Option[B] = xs map f
}

object StreamFunctor extends Functor[Stream] {
  def map[A, B](xs: Stream[A])(f: A => B): Stream[B] = xs map f
}

/*
我们只需要对不同类型的操作使用对应的Functor实例就可以了：
 */
ListFunctor.map(List(1, 2, 3))(_ + 10)
OptionFunctor.map(Some(1))(_ + 10)