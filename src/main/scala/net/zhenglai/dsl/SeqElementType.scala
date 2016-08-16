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

object OverloadViaImplicit {
  implicit object IntMarker
  implicit object StringMarker


  def apply(seq: Seq[Int])(implicit i: IntMarker.type) = "Int"

  def apply(seq: Seq[String])(implicit s: StringMarker.type ) = "String"


//  apply(Seq(1, 2))
//  apply(Seq("1"))
}

