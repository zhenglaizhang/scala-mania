package net.zhenglai.minia.lib.json

import org.scalatest.FunSuite

class JsonTest extends FunSuite {

  test("json show produces str") {
    val json = JObj(Map(
      "firstName" -> JStr("Zhenglai"),
      "lastName" -> JStr("Zhang"),
      "address" -> JObj(Map(
        "streetAddress" -> JStr("some street address"),
        "state" -> JStr("Jiangsu"),
        "postalCode" -> JNum(211525)
      )),
      "phoneNumbers" -> JSeq(List(
        JObj(Map(
          "type" -> JStr("home"), "number" -> JStr("031 123232141")
        )),
        JObj(Map(
          "type" -> JStr("work"), "number" -> JStr("021 1841234184")
        ))
      ))
    ))

    println(show(json))
  }

}
