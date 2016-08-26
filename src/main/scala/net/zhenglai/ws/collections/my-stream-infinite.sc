/*
Stream和List的主要分别是在于Stream的“延后计算“（lazy evaluation）特性。我们还讨论过在处理大规模排列数据集时，Stream可以一个一个把数据元素搬进内存并且可以逐个元素地进行处理操作。这让我不禁联想到我们常用的数据搜索读取方式了：大量的数据存放在数据库里，就好像无穷的数据源头。我们把数据读取方式（那些数据库读写API函数）嵌入Stream的操作函数内，把数据搜索条件传入Stream构造器（constructor）中形成一个对数据搜索操作的描述。这个产生的Stream只有在我们调用符合搜索条件的数据库记录时才会逐个从数据库中读取出来。这可是一个非常熟悉的场景，但我们常常会思考它的原理。


无穷数据流（infinite stream），以直接或一些算法方式有规则地重复产生数据。无穷数据流被定义为“反递归”（corecursive）的：递归的特性是从复杂到简单并最后终止，而无穷数据流的特性却是从简单到复杂并永远不会终结。
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
def constByUnfold[A](x: A): Stream[A] = Stream.unfold(x)(_ => Some(x, x))
constByUnfold(2).take(5).toList