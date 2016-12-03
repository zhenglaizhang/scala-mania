package net.zhenglai.minia.lib.fun.yeller

import org.scalatest.{ FunSuite, Matchers }

class package$Test extends FunSuite
    with Matchers {

  test("testStringToYeller") {
    //    import net.zhenglai.minia.lib.fun.yeller._
    // should work
    // TODO: WHY not work?
    import net.zhenglai.minia.lib.fun.yeller.`package`._
    "hello!!!".yell shouldBe "HELLO!!!"
  }

}
