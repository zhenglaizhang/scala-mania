package net.zhenglai.dsl

/**
  * Created by zhenglai on 8/15/16.
  */
object SeqElementType {


  def apply[T](seq: Seq[T]): String = seq match {
    case Nil => "Nothing"
    case head +: _ => head match {
      case _: Double => "Double"
      case _: String => "String"
      case _ => "Other types"
    }
  }

}

