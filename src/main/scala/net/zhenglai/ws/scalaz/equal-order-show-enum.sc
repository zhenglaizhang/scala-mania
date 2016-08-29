import scalaz._

/*
Scalaz是由一堆的typeclass组成。每一个typeclass具备自己特殊的功能。用户可以通过随意多态（ad-hoc polymorphism）把这些功能施用在自己定义的类型上。scala这个编程语言借鉴了纯函数编程语言Haskell的许多概念。typeclass这个名字就是从Haskell里引用过来的。只不过在Haskell里用的名称是type class两个分开的字。因为scala是个OOP和FP多范畴语言，为了避免与OOP里的type和class发生混扰，所以就用了typeclass一个字。实际上scalaz就是Haskell基本库里大量typeclass的scala实现。

在这篇讨论里我们可以通过介绍scalaz的一些比较简单的typeclass来了解scalaz typeclass的实现、应用方法以及scalaz函数库的内部结构。


我们知道，scalaz typeclass的几个重要元素就是：

1、特质 trait

2、隐式实例 implicit instances

3、方法注入 method injection

Equal Trait 在 core/.../scalaz/Equal.scala里，比较简单：


Eq is used for types that support equality testing. The functions its members implement are == and /=.
Scalaz equivalent for the Eq typeclass is called Equal:


Instead of the standard ==, Equal enables ===, =/=, and assert_=== syntax by declaring equal method. The main difference is that === would fail compilation if you tried to compare Int and String.

Note: I originally had /== instead of =/=, but Eiríkr Åsheim pointed out to me:

@eed3si9n hey, was reading your scalaz tutorials. you should encourage people to use =/= and not /== since the latter has bad precedence.
*/

object BuiltinEqual {
  // not type safe!!
  override def equals(obj: scala.Any): Boolean = super.equals(obj)
}


trait MyEqual[F] {self =>
  def equal(a1: F, a2: F): Boolean

  /*
从函数名称来看它是个逆变(contra)。把函数款式概括化如下：

def contramap[G](f: G => F): Equal[F] => Equal[G]

它的意思是说：如果提供G => F转换关系，就可以把Equal[F]转成Equal[G]。与正常的转换函数map比较：

def map[G](f: F => G): Equal[F] => Equal[G]

函数f是反方向的，因而称之逆变contramap。
   */
  def contramap[G](f: G => F): MyEqual[G] = new MyEqual[G] {
    override def equal(a1: G, a2: G): Boolean = self.equal(f(a1), f(a2))
  }

  /** @return true, if `equal(f1, f2)` is known to be equivalent to `f1 == f2` */
  def equalIsNatural: Boolean = false
}
/*
只要实现equal(a1,a2)这个抽象函数就可以了。Equal typeclass主要的功能就是对两个相同类型的元素进行等比。那和标准的 == 符号什么区别呢？Equal typeclass提供的是类型安全（type safe）的等比，在编译时由compiler发现错误
 */
2 == 2.0
//2 === 2.0
1 === 1
//1 === "foo" // error: type mismatch

1.some =/= 1.some

//1 assert_=== 2
/*
java.lang.RuntimeException: 1 ≠ 2
at scala.sys.package$.error(equal-order-show-enum.sc:23)
at scalaz.syntax.EqualOps.assert_$eq$eq$eq(equal-order-show-enum.sc:12)
at #worksheet#.#worksheet#(equal-order-show-enum.sc:33)
*/



/*
Normally comparison operators like != have lower higher precedence than &&, all letters, etc. Due to special precedence rule /== is recognized as an assignment operator because it ends with = and does not start with =, which drops to the bottom of the precedence:
 */

1 != 2 && false
//1 /==2 && false
/*
scala> 1 /== 2 && false
<console>:14: error: value && is not a member of Int
              1 /== 2 && false
 */
1 =/= 2 && false

/*
以上的 === 是Equal typeclass的符号方法（symbolic method），就是这个equal(a1,a2)，是通过方法注入加入到Equal typeclass里的。我们可以看到equal对两个比对对象的类型要求是非常严格的，否则无法通过编译（除非在隐式作用域implicit scode内定义Double到Int的隐式转换implicit conversion）。

但是，在Equal Trait里的equal是个抽象函数（abstract function），没有实现。那么肯定在隐式作用域（implicit scope）里存在着隐式Equal实例。比如以上的例子我们应该试着找找Equal的Int实例。

我在scalaz.std/AnyVal.scala里发现了这段代码：
 */
/*
 implicit val intInstance: Monoid[Int] with Enum[Int] with Show[Int] = new Monoid[Int] with Enum[Int] with Show[Int] {
    override def shows(f: Int) = f.toString

    def append(f1: Int, f2: => Int) = f1 + f2

    def zero: Int = 0

    def order(x: Int, y: Int) = if (x < y) Ordering.LT else if (x == y) Ordering.EQ else Ordering.GT

    def succ(b: Int) = b + 1
    def pred(b: Int) = b - 1
    override def succn(a: Int, b: Int) = b + a
    override def predn(a: Int, b: Int) = b - a
    override def min = Some(Int.MinValue)
    override def max = Some(Int.MaxValue)

    override def equalIsNatural: Boolean = true
  }

  这是个Int实例。但好像没有继承Equal trait，因而也没有发现equal函数的实现。但是它继承了Enum。那么在scalaz/Enum.scala中的Enum trait是这样的：

 trait Enum[F] extends Order[F] { self =>


 trait Order[F] extends Equal[F] { self =>

 原来Order就是Equal，所以Enum就是Equal。equal(a1,a2)是在Order trait里用order(a1,a2)实现的，而order(a1,a2)是在Int隐式实例intInstance里实现了。




scalaz一般把字符方法（symbolic method）放在scalaz/syntax目录下。也就是 ===, =/=这两个操作符号，对应的是 ==, !=这两个标准操作符。注意这个符号方法容器类EqualOps需要一个隐式参数（implicit parameter）F: Equal[F]，因为具体的equal(a1,a2)是在Equal[F]的实例里实现的。具体的方法注入黏贴还是通过隐式解析实现的：


 /** Wraps a value `self` and provides methods related to `Equal` */
final class EqualOps[F] private[syntax](val self: F)(implicit val F: Equal[F]) extends Ops[F] {
  ////

  final def ===(other: F): Boolean = F.equal(self, other)
  final def /==(other: F): Boolean = !F.equal(self, other)
  final def =/=(other: F): Boolean = /==(other)
  final def ≟(other: F): Boolean = F.equal(self, other)
  final def ≠(other: F): Boolean = !F.equal(self, other)

  /** Raises an exception unless self === other. */
  final def assert_===[B](other: B)(implicit S: Show[F], ev: B <:< F) =
      if (/==(other)) sys.error(S.shows(self) + " ≠ " + S.shows(ev(other)))

  ////
}

trait ToEqualOps  {
  implicit def ToEqualOps[F](v: F)(implicit F0: Equal[F]) =
    new EqualOps[F](v)

  ////

  ////
}


但是这个隐式转换ToEqualOps为什么是在trait里？隐式作用域必须是在某个object里的。我们再看看scalaz/syntax/syntax.scala里的这一段代码；

trait ToTypeClassOps
  extends ToSemigroupOps with ToMonoidOps with ToEqualOps with ToShowOps
  with ToOrderOps with ToEnumOps with ToPlusEmptyOps
  with ToFunctorOps with ToContravariantOps with ToApplyOps
  with ToApplicativeOps with ToBindOps with ToMonadOps with ToComonadOps
  with ToBifoldableOps with ToCozipOps
  with ToPlusOps with ToApplicativePlusOps with ToMonadPlusOps with ToTraverseOps with ToBifunctorOps with ToAssociativeOps
  with ToBitraverseOps with ToComposeOps with ToCategoryOps
  with ToArrowOps with ToFoldableOps with ToChoiceOps with ToSplitOps with ToZipOps with ToUnzipOps with ToMonadTellOps with ToMonadListenOps with ToMonadErrorOps
  with ToFoldable1Ops with ToTraverse1Ops with ToOptionalOps with ToCatchableOps with ToAlignOps


trait ToTypeClassOps继承了ToEqualOps。然后在scalaz/Scalaz.scala里：
object Scalaz
  extends StateFunctions        // Functions related to the state monad
  with syntax.ToTypeClassOps    // syntax associated with type classes
  with syntax.ToDataOps         // syntax associated with Scalaz data structures
  with std.AllInstances         // Type class instances for the standard library types
  with std.AllFunctions         // Functions related to standard library types
  with syntax.std.ToAllStdOps   // syntax associated with standard library types
  with IdInstances              // Identity type and instances


object Scalaz继承了ToTypeClassOps。这样ToEqualOps的隐式作用域就在object Scalaz里了。


为了方便使用，Equal typeclass提供了构建函数：

def equal[A](f: (A, A) => Boolean): Equal[A] = new Equal[A] {
   def equal(a1: A, a2: A) = f(a1, a2)
}
*/

import scalaz.Scalaz._

case class Person(name: String, age: Int)

implicit val personEqual: scalaz.Equal[Person] = scalaz.Equal.equal[Person]{(a, b) => a.name == b.name && a.age == b.age }

Person("Zhenglai", 23) === Person("Zhenglai", 23)
Person("Jone",23) === Person("Jone",22)


/*当然我们也可以通过实现抽象函数equal(a1,a2)函数的方式来构建Equal实例*/
/*
implicit val personEqual2 = new Equal[Person] {
  override def equal(a1: Person, a2: Person): Boolean = a1.name == a2.name && a1.age == a2.age
}
*/



case class MoneyCents(cents: Int)

def moneyToInt(m: MoneyCents): Int = m.cents * 100

/*
equalBy的意思是：假如已经有了Equal[B]实例，如果能提供A => B得转换，就可以通过equalBy构建Equal[A]实例。
*/
implicit val moneyEqual: Equal[MoneyCents] = Equal.equalBy(moneyToInt)

MoneyCents(100) === MoneyCents(120)
MoneyCents(100) === MoneyCents(100)
/*
我们知道如何等比Int，我们又可以提供MoneyCents和Int之间的转换关系，那么我们就可以构建Equal[MoneyCents]实例。
*/








/*
Ord is for types that have an ordering. Ord covers all the standard comparing functions such as >, <, >= and <=.

Order enables ?|? syntax which returns Ordering: LT, GT, and EQ. It also enables lt, gt, lte, gte, min, and max operators by declaring order method. Similar to Equal, comparing Int and Doubl fails compilation.

Scalaz的Order tyeclass提供了一组操作符号：在scalaz/syntax/OrderSyntax.scala里

我们再看看Order typeclass：

Scalaz的Order tyeclass提供了一组操作符号：在scalaz/syntax/OrderSyntax.scala里

复制代码
 1 /** Wraps a value `self` and provides methods related to `Order` */
 2 final class OrderOps[F] private[syntax](val self: F)(implicit val F: Order[F]) extends Ops[F] {
 3   ////
 4   final def <(other: F): Boolean = F.lessThan(self, other)
 5   final def <=(other: F): Boolean = F.lessThanOrEqual(self, other)
 6   final def >(other: F): Boolean = F.greaterThan(self, other)
 7   final def >=(other: F): Boolean = F.greaterThanOrEqual(self, other)
 8   final def max(other: F): F = F.max(self, other)
 9   final def min(other: F): F = F.min(self, other)
10   final def cmp(other: F): Ordering = F.order(self, other)
11   final def ?|?(other: F): Ordering = F.order(self, other)
12   final def lte(other: F): Boolean = F.lessThanOrEqual(self, other)
13   final def gte(other: F): Boolean = F.greaterThanOrEqual(self, other)
14   final def lt(other: F): Boolean = F.lessThan(self, other)
15   final def gt(other: F): Boolean = F.greaterThan(self, other)
16   ////
17 }
复制代码
其中cmp(?|?)方法使用了Ordering类型。Ordering是另外一个typeclass: scalaz/Ordering.scala

1 object Ordering extends OrderingInstances with OrderingFunctions {
2   case object LT extends Ordering(-1, "LT") { def complement = GT }
3   case object EQ extends Ordering(0,  "EQ") { def complement = EQ }
4   case object GT extends Ordering(1,  "GT") { def complement = LT }
5 }
主要定义了LT,EQ,GT三个状态。

我们应该尽量使用lt,lte,gt,gte来确保类型安全（让compiler来发现错误）：

scala> 1 lt 1.0
 5 <console>:21: error: type mismatch;
 6  found   : Double(1.0)
 7  required: Int
 8               1 lt 1.0
 9                    ^
10
11 scala> 1 ?|? 1.0
12 <console>:21: error: type mismatch;
13  found   : Double(1.0)
14  required: Int
15               1 ?|? 1.0
 */

1 < 1.0

//1 lt 1.0

//1 ?|? 1.0

1 ?|? 2

1 ?|? 1

2 ?|? 1

1 lt 2


/*
与Equal typeclass 同样，如果我们需要在自定义的类型T上使用Order typeclass的话，有几种方法可以构建Order[T]:

1、实现Order trait抽象函数order(a1,a2)，在scalaz/std/AnyValue.scala中的Int实例intInstance中是这样实现order(a1,a2)函数的：

     def order(x: Int, y: Int) = if (x < y) Ordering.LT else if (x == y) Ordering.EQ else Ordering.GT

2、用object Order里的构建函数order[A](f: (A,A) => Ordering): Order[A]

scala> case class Meat(cat: String, weight: Int)
defined class Meat
scala> implicit val meatWeightOrder: Order[Meat] = Order.order(_.weight ?|? _.weight)
meatWeightOrder: scalaz.Order[Meat] = scalaz.Order$$anon$11@7401c09f

scala> Meat("Pork",13) lt Meat("Pork",14)
res14: Boolean = true

scala> Meat("Beef",13) gt Meat("Pork",14)
res15: Boolean = false

3、逆变构建函数orderBy:

scala> case class Money(amount: Int)
defined class Money

scala> val  moneyToInt: Money => Int = money => money.amount
moneyToInt: Money => Int = <function1>

scala> implicit val moneyOrder: Order[Money] = Order.orderBy(moneyToInt)
moneyOrder: scalaz.Order[Money] = scalaz.Order$$anon$7@3e3975d0

scala> Money(20) lt Money(21)
res16: Boolean = true

scala> Money(20) ?|? Money(12)
res17: scalaz.Ordering = GT

在使用逆变构建函数时我们不需要再考虑如何实现对两个对象值的对比来获取这个Ordering返回值，我们只知道Order[Int]实现了两个Int的对比就行了。
*/

implicit val personAgeOrder = new scalaz.Order[Person] {
  override def order(a1: Person, a2: Person): Ordering = if (a1.age < a2.age) Ordering.LT else if (a1.age > a2.age) Ordering.GT else Ordering.EQ
}

Person("Zhenglai", 21) ?|? Person("test", 21)
Person("Zhenglai", 21) lt Person("test", 21)
Person("Zhenglai", 21) gt Person("test", 21)






/*
Members of Show can be presented as strings.

Show =>  * A typeclass for conversion to textual representation, done via * [[scalaz.Cord]] for efficiency.

Cord apparently is a purely functional data structure for potentially long Strings.

Show 是一个简单的typeclass。我们用Shows(T)来实现对类型T的字符描述:
在scalaz/Syntax/ShowSyntax.scala里的注入方法：

final class ShowOps[F] private[syntax](val self: F)(implicit val F: Show[F]) extends Ops[F] {
  ////
  final def show: Cord = F.show(self)
  final def shows: String = F.shows(self)
  final def print: Unit = Console.print(shows)
  final def println: Unit = Console.println(shows)
  ////
}
*/

implicit val personShow: Show[Person] = Show.show { p => s"${p.name}, ${p.age} years old" }

Person("Zhenglai", 24).show
Person("Zhenglai", 24).println


3.show
3.shows
"hello".print
"hello".println


/*
Enum members are sequentially ordered types — they can be enumerated. The main advantage of the Enum typeclass is that we can use its types in list ranges. They also have defined successors and predecessors, which you can get with the succ and pred functions.

Enum => An [[scalaz.Order]]able with discrete values.
Enum typeclass 提供了下面这些方法：

final class EnumOps[F] private[syntax](val self: F)(implicit val F: Enum[F]) extends Ops[F] {
  ////
  final def succ: F =
    F succ self

  final def -+-(n: Int): F =
    F.succn(n, self)

  final def succx: Option[F] =
    F.succx.apply(self)

  final def pred: F =
    F pred self

  final def ---(n: Int): F =
    F.predn(n, self)

  final def predx: Option[F] =
    F.predx.apply(self)

  final def from: EphemeralStream[F] =
    F.from(self)

  final def fromStep(step: Int): EphemeralStream[F] =
    F.fromStep(step, self)

  final def |=>(to: F): EphemeralStream[F] =
    F.fromTo(self, to)

  final def |->(to: F): List[F] =
    F.fromToL(self, to)

  final def |==>(step: Int, to: F): EphemeralStream[F] =
    F.fromStepTo(step, self, to)

  final def |-->(step: Int, to: F): List[F] =
    F.fromStepToL(step, self, to)

  ////
}


Instead of the standard to, Enum enables |-> that returns a List by declaring pred and succ method on top of Order typeclass. There are a bunch of other operations it enables like -+-, ---, from, fromStep, pred, predx, succ, succx, |-->, |->, |==>, and |=>. It seems like these are all about stepping forward or backward, and returning ranges.
 */

'a' to 'e'

'a' |-> 'e'

'a' |=> 'e'

'a'.succ

'b'.pred

'a' -+- 2

'd' --- 2


/*
Enum实例需要实现抽象函数succ,pred。下面是char裂隙Enum实例Enum[Char]的实现：在scalaz/std/AnyVal.scala里的char object
     def succ(b: Char) = (b + 1).toChar
     def pred(b: Char) = (b - 1).toChar
     override def succn(a: Int, b: Char) = (b + a).toChar
     override def predn(a: Int, b: Char) = (b - a).toChar
再仔细看看Enum trait如下；

trait Enum[F] extends Order[F] { self =>
  ////

  def succ(a: F): F
  def pred(a: F): F
Enum实例必须实现抽象函数succ,pred。除此之外由于Enum继承了Order，所以还必须实现Order trait的抽象函数order(a1,a2)。
*/

