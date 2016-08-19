/*
 We replace the respective type bounds with an implicit parameter of type S =:= State.Caffeinated or S =:= State.Decaffeinated:
 */
object Service {
  /*
sealed makes sure that all classes that extend a class or trait are defined in the same compilation unit.

Note that making the ServiceState trait sealed assures that no-one can suddenly add another state to our system. We also define the leaf types here to be final, so no-one can extend them, and add other states to the system
 */
  sealed trait ServiceState

  /*
  Note that sealed applies directly to the Type the keyword was applied to, not to it’s subtypes.
   */
  sealed trait Started extends ServiceState

  sealed trait Stopped extends ServiceState

  def create() = new Service[Stopped]
}


/*
As you see, Phantom Types are yet another great facility to make our code even more type-safe (or shall I say "state-safe"!?).
 */
class Service[State <: Service.ServiceState] private() {
  import Service._
  /*
  This makes the compiler look for an implicit value of type =:= parameterized with the proper types.
   */
  def start(msg: String)(implicit ev: State =:= Stopped): Service[Started]= {
    println(s"Starting $msg ...")
    this.asInstanceOf[Service[Started]]
  }

  def stop(implicit ev: State =:= Started) = {
    println("Stopping ...")
    this.asInstanceOf[Service[Stopped]]
  }
}

Service.create.start("hello").stop.start("world").stop

// Error:(40, 17) Cannot prove that A$A34.this.Service.Stopped =:= A$A34.this.Service.Started.
//Service.create.stop

/*
we get a much better error message than before than using phantom types
 */
