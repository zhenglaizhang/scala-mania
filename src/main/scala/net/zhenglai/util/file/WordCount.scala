package net.zhenglai.util.file

// TODO http://www.cnblogs.com/tiger-xc/p/4447548.html
trait WordCount

/*
好，我们下面找个例子来示范高阶类型Monoid实例和并行运算应用：用Monoid来实现对字串（List[String]）的文字数统计。由于我们打算采用并行计算，对字串进行分半时会可能会出现一字分成两半的情况，所以需要自定义复杂一点的数据类型
 */

// 记录了未完整文字的字符
case class Stub(chars: String) extends WordCount

//lStub=左边文字结尾, words=完整字数，rStub＝右边文字开头
case class Part(lStub: String, words: Int, rStub: String) extends WordCount

object WordCount {
  //  def wcMonoid: Monoid[WordCount] = new Monoid[WordCount] {
  //
  //    override def op(a1: WordCount, a2: WordCount): WordCount = ???
  //
  //    override val zero: WordCount = Stub("")
  //  }
}
