package net.zhenglai.util.str

/**
  * Created by zhenglai on 8/15/16.
  */
object StringWrapper {

  def seqToString[T](seq: Seq[T]): String = seq match {
    case head +: tail => s"$head +: ${seqToString(tail)}"
    case Nil => "Nil"
  }

  def reverseSeqToString[T](seq: Seq[T]): String = seq match {
    case prefix :+ end => reverseSeqToString(prefix) + s" :+ $end"
    case Nil => "Nil"
  }


  def seqProcess[T](seq: Seq[T])(process: T => Unit): Unit = seq match {
    case +:(head, tail) =>
      process(head)
      seqProcess(tail)(process)
    case Nil =>
  }

  1 :: 2 :: Nil
  seqToString(Seq("test", 12, "hello", Vector(1, 2, 3, 4)))
  seqToString(Seq(Map("One" -> 1), "Two", Map("Three" -> 3)))
  seqToString(Nil)
  seqToString(List.empty)

  seqProcess(Seq(1, 2, 3)) {
    println
  }

  reverseSeqToString(Seq(1, 2, 3, 4))

  // sliding is lazy
  // size of last window might be less than 3
  (1 to 20).sliding(3, 2).toList

  (1 to 20).sliding(3, 2).toList.filter(_.size == 3)
  Seq(1, 2, 3, 4).sliding(3, 2).toList
}
