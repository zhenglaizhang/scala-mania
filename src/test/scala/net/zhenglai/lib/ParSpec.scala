package net.zhenglai.lib

import java.util.concurrent.Executors

import org.scalatest.{FlatSpec, Matchers}


class ParSpec extends FlatSpec with Matchers {

  import par._

  "A Par" should "execute successfully and return correct result"  in {
    val a = par.unit(4 + 7)

    val b = async(2 + 1)

    val es = Executors.newCachedThreadPool()

    par.run(es)(a).get should be (11)
    par.run(es)(b).get should be (3)
  }
}
