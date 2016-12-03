package net.zhenglai.minia.lib.bench

import org.scalatest.{ FunSuite, Matchers }

class package$Test extends FunSuite
    with Matchers {

  test("testTime") {
    val r = time { Thread.sleep(1000); "done" }
    r._1 should be > 1000L
    r._2 shouldBe "done"
  }

}
