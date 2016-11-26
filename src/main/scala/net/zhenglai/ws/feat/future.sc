import scala.util.Try
/*
Semantics of Future

Scala’s Future[T], residing in the scala.concurrent package, is a container type, representing a computation that is supposed to eventually result in a value of type T. Alas, the computation might go wrong or time out, so when the future is completed, it may not have been successful after all, in which case it contains an exception instead.

Future is a write-once container – after a future has been completed, it is effectively immutable. Also, the Future type only provides an interface for reading the value to be computed. The task of writing the computed value is achieved via a Promise. Hence, there is a clear separation of concerns in the API design.

We need to rewrite all of the functions that can be executed concurrently so that they immediately return a Future instead of computing their result in a blocking way

he fact that it’s just another container type that can be composed and used in a functional way makes working with it very pleasant.

Making blocking code concurrent can be pretty easy by wrapping it in a call to future. However, it’s better to be non-blocking in the first place. To achieve this, one has to make a Promise to complete a Future.
 */

// Some type aliases, just for getting more meaningful method signatures:
type CoffeeBeans = String
type GroundCoffee = String
case class Water(temperature: Int)
type Milk = String
type FrothedMilk = String
type Espresso = String
type Cappuccino = String

// some exceptions for things that might go wrong in the individual steps
// (we'll need some of them later, use the others when experimenting
// with the code):
case class GrindingException(msg: String) extends Exception(msg)
case class FrothingException(msg: String) extends Exception(msg)
case class WaterBoilingException(msg: String) extends Exception(msg)
case class BrewingException(msg: String) extends Exception(msg)
def combine(espresso: Espresso, frothedMilk: FrothedMilk): Cappuccino = "cappuccino"

object NoFuture {
  // dummy implementations of the individual steps:
  def grind(beans: CoffeeBeans): GroundCoffee = s"ground coffee of $beans"
  def heatWater(water: Water): Water = water.copy(temperature = 85)
  def frothMilk(milk: Milk): FrothedMilk = s"frothed $milk"
  def brew(coffee: GroundCoffee, heatedWater: Water): Espresso = "espresso"
  def combine(espresso: Espresso, frothedMilk: FrothedMilk): Cappuccino = "cappuccino"

  // going through these steps sequentially:

  /*
   You get a very readable step-by-step instruction of what to do. Moreover, you will likely not get confused while preparing the cappuccino this way, since you are avoiding context switches.
   */
  def prepareCappuccino(): Try[Cappuccino] = for {
    ground <- Try(grind("arabica beans"))
    water <- Try(heatWater(Water(25)))
    espresso <- Try(brew(ground, water))
    foam <- Try(frothMilk("milk"))
  } yield combine(espresso, foam)
}

import scala.concurrent.future
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.Random

/*
 Single-argument lists can be enclosed with curly braces instead of parentheses.
 */
def grind(beans: CoffeeBeans): Future[GroundCoffee] = Future {
  println("start grinding...")
  Thread.sleep(Random.nextInt(2000))
  if (beans == "baked beans") throw GrindingException("are you joking?")
  println("finished grinding...")
  s"ground coffee of $beans"
}

def heatWater(water: Water): Future[Water] = Future {
  println("heating the water now")
  Thread.sleep(Random.nextInt(2000))
  println("hot, it's hot!")
  water.copy(temperature = 85)
}

def frothMilk(milk: Milk): Future[FrothedMilk] = Future {
  println("milk frothing system engaged!")
  Thread.sleep(Random.nextInt(2000))
  println("shutting down milk frothing system")
  s"frothed $milk"
}

def brew(coffee: GroundCoffee, heatedWater: Water): Future[Espresso] = Future {
  println("happy brewing :)")
  Thread.sleep(Random.nextInt(2000))
  println("it's brewed!")
  "espresso"
}

object FutureExplanation {
  import scala.concurrent.ExecutionContext.Implicits.global
  object Future {
    /*
    The computation to be computed asynchronously is passed in as the body by-name parameter.

    An ExecutionContext is something that can execute our future, and you can think of it as something like a thread pool.

    The ExecutionContext is an implicit parameter for virtually all of the Future API.
     */
    def apply[T](body: => T)(implicit execctx: ExecutionContext): Future[T] = ???
  }
}

/*
The computation of the value to be returned by a Future will start at some non-deterministic time after that Future instance has been created, by some thread assigned to it by the ExecutionContext.
 */

/*
Callbacks

Callbacks for futures are partial functions.
 */

grind("arabica beans").onSuccess {
  case ground => println(s"okay, got my $ground")
}

grind("arabica beans").onFailure {
  case ex => println("This grinder needs a replacement, seriously!")
}

import scala.util.{ Success, Failure }

grind("baked beans").onComplete {
  case Success(ground) => println(s"got my $ground")
  case Failure(ex)     => println("This grinder needs a replacement, seriously!")
}

/*
The real power of the Scala futures is that they are composable.
Future is a container type
 */

/*
mapping your Future[Water] to a Future[Boolean]

The Future[Boolean] assigned to temperatureOkay will eventually contain the successfully computed boolean value.
If this future is completed with an exception then the new
   *  future will also contain this exception.

When you are writing the function you pass to map, you’re in the future, or rather in a possible future. That mapping function gets executed as soon as your Future[Water] instance has completed successfully. However, the timeline in which that happens might not be the one you live in. If your instance of Future[Water] fails, what’s taking place in the function you passed to map will never happen. Instead, the result of calling map will be a Future[Boolean] containing a Failure.
 */
val temperatureOkay: Future[Boolean] = heatWater(Water(25)).map { water =>
  println("we're in the future!")
  (80 to 85).contains(water.temperature)
}

// let’s assume that the process of actually measuring the temperature takes a while
def temperatureOk(water: Water): Future[Boolean] = Future {
  (80 to 85).contains(water.temperature)
}

/*
Again, the mapping function is only executed after (and if) the Future[Water] instance has been completed successfully
 */
val nestedFuture: Future[Future[Boolean]] = heatWater(Water(25)).map {
  water => temperatureOk(water)
}
val flatFuture: Future[Boolean] = heatWater(Water(25)).flatMap {
  water => temperatureOk(water)
}

"-" * 20

val acceptable: Future[Boolean] = for {
  heatedWater <- heatWater(Water(25))
  okay <- temperatureOk(heatedWater)
} yield okay

"*" * 30

/*
If you have multiple computations that can be computed in parallel, you need to take care that you already create the corresponding Future instances outside of the for comprehension.

This reads nicely, but since a for comprehension is just another representation for nested flatMap calls, this means that the Future[Water] created in heatWater is only really instantiated after the Future[GroundCoffee] has completed successfully. You can check this by watching the sequential console output coming from the functions we implemented above.
 */
def prepareCappuccinoSequentially(): Future[Cappuccino] = {
  for {
    ground <- grind("arabica beans")
    water <- heatWater(Water(20))
    foam <- frothMilk("milk")
    espresso <- brew(ground, water)
  } yield combine(espresso, foam)
}

prepareCappuccinoSequentially

/*
Hence, make sure to instantiate all your independent futures before the for comprehension:

Now, the three futures we create before the for comprehension start being completed immediately and execute concurrently. If you watch the console output, you will see that it’s non-deterministic. The only thing that’s certain is that the "happy brewing" output will come last. Since the method in which it is called requires the values coming from two other futures, it is only created inside our for comprehension, i.e. after those futures have completed successfully.
 */
def prepareCappuccino(): Future[Cappuccino] = {
  val groundCoffee = grind("arabica beans")
  val heatedWater = heatWater(Water(20))
  val frothedMilk = frothMilk("milk")
  for {
    ground <- groundCoffee
    water <- heatedWater
    foam <- frothedMilk
    espresso <- brew(ground, water)
  } yield combine(espresso, foam)
}

/*
 Future[T] is success-biased, allowing you to use map, flatMap, filter etc. under the assumption that it will complete successfully.

  By calling the failed method on an instance of Future[T], you get a failure projection of it, which is a Future[Throwable]. Now you can map that Future[Throwable], for example, and your mapping function will only be executed if the original Future[T] has completed with a failure.
 */

Thread.sleep(3000)
