
/*
Self Types are used in order to "require" that, if another class uses this trait, it should also provide implementation of whatever it is that you’re requiring.
 */

trait Module {
  lazy val serviceInModule = new ServiceInModule
}

trait Service {
  /* The second line can be read as "I’m a Module". It might seem yield the exactly same But how does this differ from extending Module right away?

  which means that someone will have to give us this Module at instantiation time:

  In fact, you can use any identifier (not just this or self) and then refer to it from your class.

  self: Module =>
  self: MongoModule with APIModule =>
  */
  this: Module =>

  def doTheThings() = serviceInModule.doTheThings()
}

trait TestingModule extends Module {

}

class ServiceInModule {
  def doTheThings() = ???
}

new Service with TestingModule

// class Service cannot be instantiated because it does not conform to its self-type Service with Module
//new Service{}

