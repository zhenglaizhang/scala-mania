package net.zhenglai.app

/**
 * Created by zhenglai on 8/16/16.
 */
object ProductApp {

  // generic way of accessing fields
  def main(args: Array[String]): Unit = {
    val p: Product = Person("Zhenglai", "Zhang")
    println(p.productArity)
    println(p.productElement(0))
    println(p.productElement(1))
    println(p.productElement(1).getClass)

    p.productIterator foreach println
  }

  case class Person(first: String, last: String)

}
