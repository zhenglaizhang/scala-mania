/*
Phantom Types are very true to it’s name even if it’s a weird one, and can be explained as "Types that are not instantiate, ever". Instead of using them directly, we use them to even more strictly enforce some logic, using our types.

We have shown that phantom types can be used to encode type constraints.
 */

/*
Let’s start with preparing our "marker traits", which don’t contain any logic - we will only use them in order to express the state of an service in it’s type
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
// TODO bug fix
class Service[State <: Service.ServiceState] private () {
  import Service._
  def start[T >: State <: Stopped]() = {
    println("Starting ...")
    this.asInstanceOf[Service[Started]]
  }

  def stop[T >: State <: Started]() = {
    println("Stopping ...")
    this.asInstanceOf[Service[Stopped]]
  }
}

Service.create().start().stop().start().stop()

/*
Phantom types are called this way, because they never get instantiated.

Simply to encode type constraints, i.e. prevent some code from being compiled in certain situations.
 */

class Hacker[S <: Hacker.State] private () {
  import Hacker._

  // When the compiler tries to infer a type for T, it has to find one that is a supertype of S which represents the hacker’s state and at the same time a subtype of State.Caffeinated.
  def hackOn[T >: S <: State.Caffeinated]: Hacker[State.Decaffeinated] = {
    println("Hacking, hacking...")
    new Hacker
  }

  def drinkCoffee[T >: S <: State.Decaffeinated]: Hacker[State.Caffeinated] = {
    println("Slurp...")
    new Hacker
  }
}

object Hacker {
  sealed trait State

  object State {
    sealed trait Caffeinated extends State
    sealed trait Decaffeinated extends State
  }

  def caffeinated: Hacker[State.Caffeinated] = new Hacker
  def decaffeinated: Hacker[State.Decaffeinated] = new Hacker
}

/*
In the spirit of functional programming and immutable objects, both methods return a new instance of Hacker.
 */

Hacker.decaffeinated.drinkCoffee.hackOn.drinkCoffee.hackOn.drinkCoffee
Hacker.caffeinated.hackOn.drinkCoffee.hackOn

// Door open and close demo

trait Status
trait Opened extends Status
trait Closed extends Status

class Door[State <: Status] {
}

object Door {
  def apply[State <: Status] = new Door[State]

  def open[State <: Closed](d: Door[State]) = {
    println("Opening...")
    new Door[Opened]
  }

  def close[State <: Opened](d: Door[State]) = {
    println("Closing...")
    new Door[Closed]
  }
}

val closed = Door[Closed]
val opened = Door[Opened]
Door.open(closed)
Door.close(opened)
//Door.close(closed)

