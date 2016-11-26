package net.zhenglai.numeric

object fibonacci {
  def apply(n: Int): Int = ???

  def fib(n: Int): Int = {
    /*
    尾递归是指一个递归函数最后一个语句独立引用了自己。在以上的例子里 go(cnt-1,cur,prev + cur)
     */
    @annotation.tailrec
    def go(count: Int, prev: Int, cur: Int): Int = count match {
      case m if (m < 0) => sys.error("Negative Number Not Allowed!")
      case 0 => prev
      case c => go(count - 1, cur, cur + prev)
    }

    go(n, 0, 1)
  }

}
