/*
Methods in Scala are not values, but functions are. You can construct a function that delegates to a method via η-expansion (triggered by the trailing underscore thingy).

The definition I will be using here is that a method is something defined with def and a value is something you can assign to a val.

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