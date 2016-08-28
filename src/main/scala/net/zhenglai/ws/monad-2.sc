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

  /*
我们在设计unzip时是针对F的。在trait Functor里我们可以肯定F[(A,B)]支持map，所以我们才可以完成unzip函数的实现。这就是抽象的作用。当我们使用unzip时只要确定传入的参数fab是Functor就行了
   */
  def unzip[A, B](fab: F[(A, B)]): (F[A], F[B]) = {
    (map(fab) {_._1}, map(fab) {_._2})
  }
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

操作模式是一致相同的。不过讲实在话，上面的这些实例都没什么意义，因为施用的具体类型本身就支持map。也就是说List，Option等本身就是Functor。换句话讲就是：它们都可以map，所以都是Functor。
 */
ListFunctor.map(List(1, 2, 3))(_ + 10)
OptionFunctor.map(Some(1))(_ + 10)



ListFunctor.unzip(List(1 -> 10, 2 -> 20, 3 -> 30))
OptionFunctor.unzip(Some((1, 2)))


/*
def map2[A,B,C](la: List[A], lb: List[B])(f: (A,B) => C): List[C] = {
      la flatMap {a => lb map { b => f(a,b) }}
  }
  def map2[A,B,C](oa: Option[A], ob: Option[B])(f: (A,B) => C): Option[C] = {
      oa flatMap{a => ob map { b => f(a,b) }}
  }
  def map2[A,B,C](pa: Par[A], pb: Par[B])(f: (A,B) => C): Par[C] = {
      pa flatMap{a => pb map { b => f(a,b) }}
  }

看看这些map2函数：不但款式相同，实现方法也是相同的。不同的还是具体施用受体的数据类型。看来我们还是因为各种数据类型的不同而重复编写了map2组件。我们应该想办法一次实现map2后让所有数据类型实例都可以使用，从而彻底避免重复编码。可以肯定的是这些办法一定跟共性抽象有关。

在前面那些章节的讨论中我们一直针对某些数据类型的特性设计最基本的操作函数或组件。因为各种数据类型的不同我们重复编写了map2组件。现在我们看到map2是可以用flatMap和map来实现的。那么flatMap和map就是最基本最通用的组件了吗？事实上map可以用flatMap和unit来实现：

def map[A,B](pa: Par[A])(f: A => B): Par[B] = {
 flatMap(pa) { a => unit(f(a)) }
}
 */


/*
在这个trait里unit和flatMap是抽象的。这意味着各类型的Monad实例必须实现unit和flatMap，并且会自动获取map和map2两个组件。
 */
trait Monad[M[_]] extends Functor[M] {

  def unit[A](a: A): M[A]

  def flatMap[A, B](ma: M[A])(f: A => M[B]): M[B]

  def map[A, B](ma: M[A])(f: A => B): M[B] = {
    flatMap(ma)(x => unit(f(x)))
  }

  def map2[A, B, C](ma: M[A], mb: M[B])(f: (A, B) => C): M[C] = {
    flatMap(ma) {
      a => map(mb) { b => f(a, b) }
    }
  }

  def sequence[A](lm: List[M[A]]): M[List[A]] = {
    lm.foldRight(unit(Nil: List[A])){(a,b) => map2(a,b){_ :: _} }
  }
  def travers[A,B](la: List[A])(f: A => M[B]): M[List[B]] = {
    la.foldRight(unit(Nil: List[B])){(a,b) => map2(f(a),b){_ :: _}}
  }
  def replicateM[A](n: Int, ma: M[A]): M[List[A]] = {
    if (n == 0) unit(Nil)
    else map2(ma,replicateM(n-1,ma)) {_ :: _}
  }
  def factor[A,B](ma: M[A], mb: M[B]): M[(A,B)] = {
    map2(ma,mb){(a,b) => (a,b)}
  }
  def cofactor[A,B](e: Either[M[A],M[B]]): M[Either[A,B]] = {
    e match {
      case Right(b) => map(b){x => Right(x)}
      case Left(a) => map(a){x => Left(x)}
    }
  }

  /*
可以看出，我们新增加的组件都是以unit + flatMap这两个基础组件实现的，都是更高阶的组件。所以是不是可以说Monadic programming 就是 flatMap Programming呢？
   */
}


val listMonad = new Monad[List] {
  override def unit[A](a: A): List[A] = List(a)

  override def flatMap[A, B](ma: List[A])(f: (A) => List[B]): List[B] = ma flatMap f
}

listMonad.map(List(1, 2, 3))(_ + 10)
listMonad.map2(List(1, 2), List(3, 4)){ (a, b) => List(a, b)}
// 的确我们从listMonad中自动获得了可用的map和map2.



val optionMonad = new Monad[Option] {
  override def unit[A](a: A): Option[A] = Some(a)

  override def flatMap[A, B](ma: Option[A])(f: (A) => Option[B]): Option[B] = ma flatMap f
}

optionMonad.map(Some(1)){ _ + 10}
optionMonad.map2(Some(1), Some(2)){(a, b) => a + b }

/*
现在我们似乎可以说任何可以flatMap（具备flatMap函数）的数据类型都是Monad。

我们可以再丰富一下现在的Monad组件库，增加多些共用组件，使Monad抽象模型能更概括实用些：
 */