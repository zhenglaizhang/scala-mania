package net.zhenglai.minia.lib

package object json {
  def show(json: JSON): String = json match {
    case JSeq(elems) =>
      "[" + (elems map show mkString ", ") + "]"
    case JObj(bindings) =>
      val assocs = bindings map {
        case (key, value) => "\"" + key + "\":" + show(value)
      }

      "{" + (assocs mkString ", ") + "}"
    case JNum(num) => num.toString
    case JBool(bool) => bool.toString
    case JStr(str) => "\"" + str + "\""
    case JNull => "null"
  }
}
