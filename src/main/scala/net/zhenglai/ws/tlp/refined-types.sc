
/*
Refinements are very easy to explain as "subclassing without naming the subclass".
 */

class Entity {
  def persistForReal() = ???
}


trait Persister {
  def doPersist(e: Entity) = {
    e.persistForReal()
  }
}


// our refined instance (and type):
val refinedMockPersister = new Persister {
  override def doPersist(e: Entity): Unit = {
    super.doPersist(e)
    // other stuff
    ()
  }
}