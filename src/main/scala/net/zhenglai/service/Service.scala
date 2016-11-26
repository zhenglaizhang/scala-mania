package net.zhenglai.service

trait Service

/*
when instantiating objects with mixins as the scala compiler creates singleton types for these.
 */
object Service {
  def make() = new Service {
    def getId = 123
  }

  /*
  the author is free to mix in more traits without changing the public type of make, making it easier to manage backwards compatibility.
   */
  def makeGood(): Service = new Service {}
}
