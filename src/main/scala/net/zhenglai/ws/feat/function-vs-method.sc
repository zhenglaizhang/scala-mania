/*
Methods in Scala are not values, but functions are. You can construct a function that delegates to a method via η-expansion (triggered by the trailing underscore thingy).

The definition I will be using here is that a method is something defined with def and a value is something you can assign to a val.

方法不是函数但可以转化成函数；可以手工转换或者由编译器（compiler）在适当的情况下自动转换。反向转换则不然；函数是无法转换到方法的。
 */

def m(xs: List[Int]): AnyRef = ???

val f = m _

// is equivalent to

val fe = new AnyRef with ((List[Int]) => AnyRef) {
  override def apply(x$1: List[Int]): AnyRef = this.m(x$1)
  def m(xs: List[Int]): AnyRef = ???
}

// When we define a method we see that we cannot assign it to a val.
def add1(n: Int): Int = n + 1
// type: (n: Int)Int, not a function type
//val a = add1
// follow this method with `_' if you want to treat it as a partially applied function

// by adding the η-expansion postfix operator (η is pronounced “eta”), we can turn the method into a function value.
val a = add1 _
a(12)

// The effect of _ is to perform the equivalent of the following: we construct a Function1 instance that delegates to our method.
val g = new Function[Int, Int] {
  def apply(n: Int): Int = add1(n)
}
g(12)

// Automatic Expansion
// In contexts where the compiler expects a function type, the desired expansion is inferred and the underscore is not needed:
Seq(1, 2, 3).map(add1 _)
Seq(1, 2, 3).map(add1)
// This applies in any position where a function type is expected, as is the case with a declared or ascribed type:

val z: Int => Int = add1
val y = add1: Int => Int

// Effect of Overloading
// In the presence of overloading you must provide enough type information to disambiguate:
//"foo".substring _

val sub = "foo".substring _: (Int => String)
sub(1)

// Scala actually has a lot of ways to specify parameters, but they all work with η-expansion. Let’s look at each case.

// Parameterless Methods
def x = println("hi")
val z1 = x _
// Methods with no parameter list follow the same pattern, but in this case the compiler can’t tell us about the missing _ because the invocation is legal on its own.

// the invocation itself is legal on its own
val z2 = x // assign the result of invocation of x()

// Note that unlike the method (which has no parameter list) the function value has an empty parameter list.
z1()
z1

/*
Multiple Parameters

Methods with multiple parameters expand to equivalent multi-parameter functions:
 */
def plus(a: Int, b: Int): Int = a + b
plus _

// Methods with multiple parameter lists become curried functions:
def plus2(a: Int)(b: Int): Int = a + b
val p = plus2 _

// Perhaps surprisingly, such methods also need explicit η-expansion when partially applied:
plus2(2) _

// However curried functions do not.
val foo = plus2 _
foo(1) // no underscore needed

/*
Type Parameters

Values in scala cannot have type parameters; when η-expanding a parameterized method all type arguments must be specified (or they will be inferred as non-useful types):
 */
def id[A](a: A): A = a
val x1 = id _
// x1: Nothing => Nothing = <function1>
val y1 = id[Int] _
y1(10)

/*
Implicit Parameters

Implicit parameters are passed at the point of expansion and do not appear in the type of the constructed function value:
 */
def bar[N: Numeric](n: N) = n
//bar[String] _
bar[Int] _

def baz[N](n: N)(implicit ev: Numeric[N]): N = n
baz[Int] _

/*
By-Name Parameters
The “by-nameness” of by-name parameters is preserved on expansion:
 */

def byName(a: => Unit): Int = {
  a
  42
}
byName(println("hi"))

val byNameFn = byName _
byNameFn(println("hi again"))

/*
Also note that η-expansion can capture a by-name argument and delay its evaluation:
 */

def meow(a: => Unit): () => Unit = a _

val xx = meow(println("hi meow"))

xx()

/*
Sequence Parameters

Sequence (“vararg”) parameters become Seq parameters on expansion
 */

def wow(as: Int*): Int = as.sum

def yy = wow _

//yy(1, 2, 3)
//<console>:10: error: too many arguments for method apply: (v1: Seq[Int])Int in trait Function1

yy(Seq(1, 2, 3))
wow(1, 2, 3)

/*
Default arguments are ignored for the purposes of η-expansion; it is not possible to use named arguments to simulate partial application.
 */
def mhm(n: Int = 3, s: String) = s * n

val zz = mhm _

//mhm(42) _

// Way to automatically convert class method to function taking explicit class argument in Scala?

case class A(x: Int) {
  def f(y: Int) = x * y
}

val ains = A(4)
val af = (_: A).f _
af(ains)(4)

// import ains._

// val af = ains.f _
// val af = A(4).f _

// aMethod 与 aFunction 在类型上是不同的。
def aMethod(x: Int) = x + 10
val aFunction = (x: Int) => x + 10

aFunction
/*
aMethod

Error:(178, 2) missing argument list for method aMethod in class A$A7
Unapplied methods are only converted to functions when a function type is expected.
You can make this conversion explicit by writing `aMethod _` or `aMethod(_)` instead of `aMethod`.
aMethod;}

引用方法必须提供完整的参数清单，引用函数则无须。把方法转换成函数呢？在参数位置用 _ 来进行转换
*/
val toFunction = aMethod(_)
val toFunction1 = aMethod _

/*
aMethod转换成函数toFunctions后具备了函数的特性。

我们称函数为“头等类值”（first class value），可以当作高阶函数的参数或返回值。但方法不是“头等类值”，不能当作参数。

Scala的编译器能针对需要函数的地方把方法转换成函数!!!
*/

/*
函数就是普通的对象
 */

val add = (a: Int, b: Int) => a + b

// compiler convert it to object below
val addThem = new Function2[Int, Int, Int] {
  override def apply(v1: Int, v2: Int): Int = v1 + v2
}

add(1, 2) == addThem.apply(1, 2)
add(1, 2) == addThem(1, 2)

// 多态函数
def findFirstInt(arr: Array[Int], target: Int): Int = {
  def loop(idx: Int): Int = idx match {
    case l if (l > arr.length)   => -1
    case i if (arr(i) == target) => idx
    case _                       => loop(idx + 1)
  }
  loop(0)
}

findFirstInt((1 to 10).toArray, 3)
findFirstInt((2 to 1000).toArray, 200)

def findFirstString(arr: Array[String], target: String): Int = {
  def loop(idx: Int): Int = idx match {
    case l if (l >= arr.length)  => -1 //indicate not found
    case i if (arr(i) == target) => idx
    case _                       => loop(idx + 1)
  }
  loop(0)
} //> findFirstString: (arr: Array[String], target: String)Int
findFirstString(Array("Hello", "My", "World"), "My")
//> res55: Int = 1
findFirstString(Array("Hello", "My", "World"), "Yours")
//> res56: Int = -1

// 可以通过多态函数把共通点抽象出来
def findFirstA[A](arr: Array[A], target: A)(eq: (A, A) => Boolean): Int = {
  def loop(idx: Int): Int = idx match {
    case l if l >= arr.length    => -1
    case i if eq(arr(i), target) => idx
    case _                       => loop(idx + 1)
  }

  loop(0)
}

findFirstA[Int](Array(2, 4, 3, 9, 0), 3)((x, y) => x == y)
//> res57: Int = 2
findFirstA[String](Array("Hello", "My", "World"), "My")((x, y) => x == y)
//> res58: Int = 1

/*
findFirstA是个多态函数。A是一个类型变量。我们可以说findFirstA是个针对类型变量A的多态函数。注意我们在findFirstA增加了一个参数清单- (equ: (A,A) => Boolean)。这是因为我们还无法确定A的类型。那么我们必须提供A类型的对比函数。
 */

