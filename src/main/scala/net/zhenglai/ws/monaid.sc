

/*
Monoid(幺半群)是数学范畴理论（category theory）中的一个特殊范畴（category）。不过我并没有打算花时间从范畴理论的角度去介绍Monoid，而是希望从一个程序员的角度去分析Monoid以及它在泛函编程里的作用。从这个思路出发我们很自然得出Monoid就是一种数据类型，或者是一种在泛函编程过程中经常会遇到的数据类型：当我们针对List或者loop进行一个数值的积累操作时我们就会使用到Monoid。实际上Monoid就是List[A] => A的抽象模型。


Monoid由以下条件组成：

1、一个抽象类型A
2、一个二元结合性函数（binary associative function），对传入的两个A类参数进行操作后产生一个A类型结果
3、一个恒等值（identity）

由于Monoid是一个数学类型，它的二元操作函数必须遵循一些定律：

1、结合性（associativity）：op(a,op(b,c)) = op(op(a,b),c)：这个定律是函数组合（function composition）不可缺的条件
2、二元函数参数中如果有一个是恒等值时操作结果为另一个参数：op(identity,v) = v
 */


// 我们用scala的特质（trait）描述了Monoid。它就是一个抽象的数据类型。
trait Monoid[A] { //被封装的类型A
  val zero: A  //恒等值identity

  def op(a1: A, a2: A): A //二元函数
}

/*
既然Monoid trait是个抽象类型，那么我们可以试着创建几个基础类型的Monoid实例：
 */

val stringConcatMonid = new Monoid[String] {
  override val zero: String = ""

  override def op(a1: String, a2: String): String = a1 + a2
}

assert(stringConcatMonid.op(stringConcatMonid.zero, "s2") == "s2")

val intAdditionMonoid = new Monoid[Int] {
  override val zero: Int = 0

  override def op(a1: Int, a2: Int): Int = a1 + a2
}

val intMultiplictionMethod = new Monoid[Int] {
  override val zero: Int = 1

  override def op(a1: Int, a2: Int): Int = a1 * a2
}
/*
可以看出，这几个Monoid实例都符合Monoid定律。那我们可以先试着用用。上面提到Monoid最适合一串值的累加操作List[A] => A，我们可以对List[A]进行操作示范：

Monoid m是个抽象类型，m.zero和m.op()的具体意义要看Monoid的实例了:
 */

def reduce[A](xs: List[A])(m: Monoid[A]): A = xs match {
  case Nil => m.zero
  case h :: t => m.op(h, reduce(t)(m))
}

reduce(List(1, 2, 3))(intAdditionMonoid)
reduce(List(1, 2, 3))(intMultiplictionMethod)
reduce(List("11", "22", "33"))(stringConcatMonid)

/*
对List[A]的具体累加处理是按照intAdditionMonoid和stringConcatMonoid的二元函数功能进行的。看来Monoid特别适用于List类型的循环操作。
 */

def reduceGeneric[A](xs: List[A])(zero: A)(op: (A, A) => A): A = xs match {
  case Nil => zero
  case h :: t => op(h, reduceGeneric(t)(zero)(op))
}

reduceGeneric(List(1, 2, 3))(0)(_ + _)

/*
这个类型款式跟折叠算法的类型款式非常相似：
1   def foldRight[A,B](as: List[A])(z: B)(f: (A,B) => B): B
2   如果类型B=类型A
3   def foldRight[A](as: List[A])(z: A)(f: (A,A) => A): A
 */

// 实际上我们可以直接用上面的Monoid实例运算折叠算法：
List("this is ", "the string ", "monoid").foldLeft(stringConcatMonid.zero)(stringConcatMonid.op)
List("this is ", "the string ", "monoid").foldRight(stringConcatMonid.zero)(stringConcatMonid.op)


def optionMonoid[A] = new Monoid[Option[A]] {
  def op(o1: Option[A], o2: Option[A]): Option[A] = o1 orElse o2
  val zero = None  // op(zero, o1)= None orElse o2 = o2
}                                               //> optionMonoid: [A]=> ch10.ex1.Monoid[Option[A]]{val zero: None.type}
def listConcatMonoid[A] = new Monoid[List[A]] {
  def op(l1: List[A], l2: List[A]) = l1 ++ l2
  val zero = Nil
}                                               //> listConcatMonoid: [A]=> ch10.ex1.Monoid[List[A]]{val zero: scala.collection.
//| immutable.Nil.type}
val booleanOrMonoid = new Monoid[Boolean] {
    def op(b1: Boolean, b2: Boolean) = b1 || b2
    val zero = false
  }                                         //> booleanOrMonoid  : ch10.ex1.Monoid[Boolean] = ch10.ex1$$anonfun$main$1$$anon
//| $6@5b464ce8
val booleanAndMonoid = new Monoid[Boolean] {
    def op(b1: Boolean, b2: Boolean) = b1 && b2
    val zero = true
  }                                         //> booleanAndMonoid  : ch10.ex1.Monoid[Boolean] = ch10.ex1$$anonfun$main$1$$an
//| on$7@57829d67

/*
以上几个增加的Monoid实例中endoComposeMonoid和endoAndThenMonoid可能比较陌生。它们是针对函数组合的Monoid。
 */
def endoComposeMonoid[A] = new Monoid[A => A] {
  def op(f: A => A, g: A => A) = f compose g
  val zero = (a: A) => a    // op(zero, g: A => A) = zero compose g = g
}                                         //> endoComposeMonoid: [A]=> ch10.ex1.Monoid[A => A]
def endoAndThenMonoid[A] = new Monoid[A => A] {
  def op(f: A => A, g: A => A) = f andThen g
  val zero = (a: A) => a   // op(zero, g: A => A) = zero andThen g = g
}                                         //> endoAndThenMonoid: [A]=> ch10.ex1.Monoid[A => A]
//计算m的镜像Monoid
def dual[A](m: Monoid[A]) = new Monoid[A] {
  def op(x: A, y: A) = m.op(y,x)    //镜像op即时二元参数位置互换
  val zero = m.zero
}                                         //> dual: [A](m: ch10.ex1.Monoid[A])ch10.ex1.Monoid[A]
def firstOfDualOptionMonoid[A] = optionMonoid[A]
//> firstOfDualOptionMonoid: [A]=> ch10.ex1.Monoid[Option[A]]{val zero: None.ty
//| pe}
def secondOfDualOptionMonoid[A] = dual(firstOfDualOptionMonoid[A])
//> secondOfDualOptionMonoid: [A]=> ch10.ex1.Monoid[Option[A]]


/*
下面这个函数用Monoid对List[A]元素A进行累加操作
 */
def concatenate[A](xs: List[A], m: Monoid[A]): A = {
  xs.foldRight(m.zero)((a, b) => m.op(a, b))
}

concatenate(List(1, 2, 3, 4), intAdditionMonoid)

/*
那么如果没有List[A]元素A类型Monoid实例怎么办？我们可以加一个函数：
如果我们有一个函数可以把A类转成B类 A => B，那我们就可以使用Monoid[B]了：
 */

def foldMap[A, B](xs: List[A])(m: Monoid[B])(f: A => B): B = {
  xs.foldRight(m.zero)((a, b) => m.op(f(a), b))
}
/*
oldRight的类型款式：foldRight[A,B](as: List[A])(z: B)(g: (A,B) => B): B。其中(A,B) => B >>> (f(A),B) => B >>> (B,B) => B 就可以使用 Monoid[B].op(B,B)=B了。我们也可以用foldLeft来实现foldMap。实际上我们同样可以用foldMap来实现foldRight和foldLeft:

foldRight和foldLeft的f函数是(A,B) => B，如果用curry表达：A => (B => B)，如果能把 A => ? 转成 B => B，那么我们就可以使用endoComposeMonoid[B].op(f: B => B, g: B => B): B。
 */
def foldRight[A,B](as: List[A])(z: B)(f: (A,B) => B): B = {
  foldMap(as)(endoComposeMonoid[B])(a => b => f(a,b))(z)
}

def foldLeft[A,B](as: List[A])(z: B)(f: (A,B) => B): B = {
  foldMap(as)(dual(endoComposeMonoid[B]))(a => b => f(a,b))(z)
}


def foldMapV[A, B](xs: IndexedSeq[A])(m: Monoid[B])(f: A => B): B = xs.length match {
  case 0 => m.zero
  case 1 => f(xs.head)
  case _ =>
    val (l, r) = xs.splitAt(xs.length / 2)
    m.op(foldMapV(l)(m)(f), foldMapV(r)(m)(f))
}


/*
def foldMapVP[A, B](xs: IndexedSeq[A])(m: Monoid[B])(f: A => B) = xs.length match {
  case 0 => m.zero
  case 1 => f(xs.head)
  case _ =>
    val (l, r) = xs.splitAt(xs.length / 2)
    m.op(Par.async(foldMapVP(l)(m)(f)), Par.async(foldMapVP(r)(m)(f)))
}
*/

