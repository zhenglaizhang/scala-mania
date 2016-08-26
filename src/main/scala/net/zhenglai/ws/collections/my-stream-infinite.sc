/*
Stream和List的主要分别是在于Stream的“延后计算“（lazy evaluation）特性。我们还讨论过在处理大规模排列数据集时，Stream可以一个一个把数据元素搬进内存并且可以逐个元素地进行处理操作。这让我不禁联想到我们常用的数据搜索读取方式了：大量的数据存放在数据库里，就好像无穷的数据源头。我们把数据读取方式（那些数据库读写API函数）嵌入Stream的操作函数内，把数据搜索条件传入Stream构造器（constructor）中形成一个对数据搜索操作的描述。这个产生的Stream只有在我们调用符合搜索条件的数据库记录时才会逐个从数据库中读取出来。这可是一个非常熟悉的场景，但我们常常会思考它的原理。


无穷数据流（infinite stream），以直接或一些算法方式有规则地重复产生数据。无穷数据流被定义为“反递归”（corecursive）的：递归的特性是从复杂到简单并最后终止，而无穷数据流的特性却是从简单到复杂并永远不会终结。

无穷数据流到底能干什么呢？作为数据库搜索的数据源吗，这个可以用普通的Stream来实现。由于无穷数据流是根据一些算法有规则的不停顿产生数据，那么用来搭建测试数据源或者什么数学统计模式环境道是是可以的。想到不断产生数据，那么用来画些动态的东西也行吧，那在游戏软件中使用也有可能了。
 */

import net.zhenglai.ds._

def ones: Stream[Int] = Stream.cons(1, ones)
ones.take(100).toList
// ones函数可以产生一个无穷的数据流。每个元素都是常数1。从这个简单的例子我们可以稍微领略反递归的味道：cons(1,ones),通过重复不断运算cons来产生无穷数据。

def constant[A](x: A): Stream[A] = Stream.cons(x, constant(x))

constant(5).take(5).toList

def fibs: Stream[Int] = {
  // 从以上这些例子可以看出：我们不断重复的在cons。而cons的参数则是算法的实现结果。
  def go(prev: Int, cur: Int): Stream[Int] = {
    Stream.cons(prev, go(cur, prev + cur))
  }
  go(0, 1)
}
fibs.take(5).toList

/*
constantByUnfold产生一个无穷的常数：a同时代表了元素类型和状态。_ => Some((a,a))意思是无论输入任何状态，元素值和状态都不转变，所以unfold会产生同一个数字。另外f的结果永远不可能是None，所以这是一个无穷数据流（infinite stream）。
 */
def constByUnfold[A](x: A): Stream[A] = Stream.unfold(x)(_ => scala.Some(x, x))
constByUnfold(2).take(5).toList

def fromByUnfold(s: Int): Stream[Int] = Stream.unfold(s)(x => scala.Some(x, x + 1))
fromByUnfold(5).take(5).toList

def fromByUnfold_2(s: Int): Stream[Int] = Stream.unfold(s)(s => scala.Some(s, s + 2))
fromByUnfold_2(5).take(5).toList


def fibByUnfold: Stream[Int] = Stream.unfold((0, 1)) {
  case (a, s) => scala.Some((a, (s, a + s)))
}

/*
S类型为tuple，起始值(0,1)，元素类型A是Int。函数f: Int => Option[(Int, Int)]。f函数返回新A=a1, 新状态S (a2, a1+a2)。由于状态是个tuple类型，(a1,a2)是个模式匹配操作，所以必须加上case。S=(0,1) >>> (A,S)=(0,(1,0+1)) >>>(1,(1,1+1))>>>(1,(2,2+1))>>>(2,(3,2+3))>>>(3,(5,3+5))>>>(5,(8,5+8))>>>(8,(13,8+13))从以上推断我们可以得出A>>>0,1,1,2,3,5,8,13，而状态S>>>(0,1),(1,1),(1,2),(2,3),(3,5),(5,8)...不断变化
 */
fibByUnfold.take(10).toList

fibByUnfold.mapByUnfoldInfinite(_ + 10).take(10).toList

(fromByUnfold(1).mapByUnfoldInfinite {_ + 10}).take(5).toList
//> res9: List[Int] = List(11, 12, 13, 14, 15)
(fromByUnfold(1).mapByUnfoldInfinite {_ + 10}).takeByUnfold(5).toList
//> res10: List[Int] = List(11, 12, 13, 14, 15)
(fromByUnfold(1).mapByUnfoldInfinite {_ + 10}).takeWhileByUnfold(_ < 15).toList
//> res11: List[Int] = List(11, 12, 13, 14)
(fromByUnfold(1).mapByUnfoldInfinite {_ + 10}).filterByUnfold(_ < 15).toList
//> res12: List[Int] = List(11, 12, 13, 14)
(fromByUnfold(5) zip fromByUnfold(1).mapByUnfoldInfinite {_ + 10}).take(5).toList
//> res13: List[(Int, Int)] = List((5,11), (6,12), (7,13), (8,14), (9,15))