package net.zhenglai.minia.lib

package object json {
  def show(json: JSON): String = json match {
    case JSeq(elems) =>
      s"[${elems.map(show(_)).mkString(", ")}]"
    case JObj(bindings) =>
      s"{ ${
        bindings.map { case (key, value) =>
          key + ": " + show(value)
        }.mkString(", ")
      } }"
    case JNum(num) =>
      s"$num"
    case JBool(bool) =>
      bool.toString
    case JStr(str) =>
      s""""$str""""
    case JNull =>
      "null"
  }
}
