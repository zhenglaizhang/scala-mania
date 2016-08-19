
/*
you can decompose various kinds of data structures using pattern matching, among them

* lists,
* streams,
* any instances of case classes


Case classes are special because Scala automatically creates a companion object for them, mimic ctor apply and extractor unapply auto generated

destructure an instance of a case class in a pattern, and how to write your own extractors, allowing you to destructure any types of objects in any way you desire.
 */

case class User(firstName: String, lastName: String, score: Int)

def advance(xs: List[User]) = xs match {
  case User(_, _, score1) :: User(_,_, score2) :: _ => score1 - score2
  case _ => 0
}

object Twice {
  def apply(x: Int): Int = x * 2

  /*
  an extractor has the opposite role of a constructor:
   */
  def unapply(z: Int): Option[Int] = if (z%2 == 0) Some(z/2) else None
}

val x = Twice(21)
x match { case Twice(n) => Console.println(n) } // prints 21
/*
The pattern case Twice(n) will cause an invocation of Twice.unapply, aka. extractor

The return type of an unapply should be chosen as follows:
If it is just a test, return a Boolean. For instance case even()
If it returns a single sub-value of type T, return a Option[T]
If you want to return several sub-values T1,...,Tn, group them in an optional tuple Option[(T1,...,Tn)].
Sometimes, the number of sub-values is fixed and we would like to return a sequence. For this reason, you can also define patterns through unapplySeq. The last sub-value type Tn has to be Seq[S]. This mechanism is used for instance in pattern case List(x1, ..., xn).
 */

object h1 {
  trait User {
    def name: String
  }
  class FreeUser(val name: String) extends User
  class PremiumUser(val name: String) extends User

  object FreeUser {
    def unapply(arg: FreeUser): Option[String] = Some(arg.name)
  }

  object PremiumUser {
    def unapply(arg: PremiumUser): Option[String] = Some(arg.name)
  }
}

h1.FreeUser.unapply(new h1.FreeUser("Daniel"))
/*
But you wouldn’t usually call unapply method directly. Scala calls an extractor’s unapply method if the extractor is used as an extractor pattern.

If the result of calling unapply is Some[T], this means that the pattern matches, and the extracted value is bound to the variable declared in the pattern. If it is None, this means that the pattern doesn’t match and the next case statement is tested.
 */

val user: h1.User = new h1.PremiumUser("Zhenglai")
user match {
  case h1.FreeUser(name) => s"Hello $name"
    /*
    the user value is now passed to the unapply method of the PremiumUser companion object, as that extractor is used in the second pattern. This pattern will match, and the returned value is bound to the name parameter.
     */
  case h1.PremiumUser(name) => s"Welcome back, der $name"
}

object h2 {

  trait User {
    def name: String
    def score: Int
  }
  class FreeUser(val name: String, val score: Int, val upgradeProbability: Double)
    extends User
  class PremiumUser(val name: String, val score: Int) extends User

  object FreeUser {
    def unapply(arg: FreeUser): Option[(String, Int, Double)] =
      Some(arg.name, arg.score, arg.upgradeProbability)
  }

  object PremiumUser {
    def unapply(arg: PremiumUser): Option[(String, Int)] =
      Some(arg.name, arg.score)
  }

  object premiumCandicate {
    def unapply(arg: FreeUser): Boolean = arg.upgradeProbability > 0.75
  }

  def app = {
    val user: User = new FreeUser("Daniel", 3000, 0.7d)

    user match {
      case FreeUser(name, _, p) =>
        if (p > 0.75) s"$name, what can we do for you today?" else s"Hello $name"
//      case PremiumUser(name, _) => s"Welcome back ,dear $name"
        // infix operation pattern
      case name PremiumUser _ => s"Welcome back, dear $name"
    }

    val user2: User = new FreeUser("Daniel", 2500, 0.8d)
    user2 match {
        /*
a boolean extractor is used by just passing it an empty parameter list, which makes sense because it doesn’t really extract any parameters to be bound to variables.


Scala’s pattern matching allows to bind the value that is matched to a variable, too, using the type that the used extractor expects.
This is done using the @ operator. Since our premiumCandidate extractor expects an instance of FreeUser, we have therefore bound the matched value to a variable freeUser of type FreeUser.
         */
      case freeUser @ premiumCandicate() => s"initing spam program for $freeUser"
      case _ => "Sending regular news letter to $user2"
    }
  }
}

h2.app



object infixOperationPattern {
  val xs = 44 #:: 12 #:: 39 #::100 #::11 #:: Stream.empty

  def app = {
    xs match {
        /*
        Scala also allows extractors to be used in an infix notation. So, instead of writing e(p1, p2), where e is the extractor and p1 and p2 are the parameters to be extracted from a given data structure, it’s always possible to write p1 e p2

        infix operation pattern head #:: tail could also be written as #::(head, tail)

        Usage of infix operation patterns is only recommended for extractors that indeed are supposed to read like operators, which is true for the cons operators of List and Stream, but certainly not for our PremiumUser extractor.


        case head #:: tail will match for a stream of one or more elements. If it has only one element, tail will be bound to the empty stream.
         */
//      case #::(first, #::(second, _)) => first - second
      case first #:: second #:: _ => first - second
      case _ => -1
    }
  }
}

infixOperationPattern.app

/*
While some people point out that using case classes and pattern matching against them breaks encapsulation, coupling the way you match against data with its concrete representation, this criticism usually stems from an object-oriented point of view.
It’s a good idea, if you want to do functional programming in Scala, to use case classes as algebraic data types (ADTs) that contain pure data and no behaviour whatsoever.
 */

/*
think about how you would implement and use a URLExtractor that takes String representations of URLs.
 */

object extractingSequence {
  val xs = 3 :: 6 :: 12 :: Nil

  def app = {
    val r = xs match {
      case List(a, b) => a * b
      case List(a, b, c) => a + b + c
      case _ => 0
    }
    println(r)

    val r1 = xs match {
      case List(a, b, _*) => a * b
      case _ => 0
    }
    println(r1)


    println(greetWithFirstName("Zhenglai"))
    println(greetWithFirstName("Zhenglai Zhang"))
    println(greetWithFirstName(""))
    println("-" * 20)
    println(greet(""))
    println(greet("Zhenglai"))
    println(greet("Zhenglai Zhang"))
    println(greet("Zhenglai Midname Zhang"))
  }


  /*
  implement and use extractors that return variable-length sequences of extracted values. Extractors are a pretty powerful mechanism. They can often be re-used in flexible ways and provide a powerful way to extend the kinds of patterns you can match against.
   */
  object GivenNames {
    def unapplySeq(name: String): Option[Seq[String]] = {
      val names = name.trim.split(" ")
      if (names.forall(_.isEmpty)) None else Some(names)
    }
  }

  def greetWithFirstName(name: String) = name match {
    case GivenNames(firstName, _*) => s"Good morning, $firstName!"
    case _ => "Welcom! please make sure to fill in your name!"
  }

  def greet(fullName: String) = fullName match {
    case Names(first, last, _*) => s"Good morning, $first $last!"
    case _ => "Welcome! please make sure to fill in your name!"
  }

  /*
  unapplySeq can also return an Option of a TupleN, where the last element of the tuple must be the sequence containing the variable parts of the extracted values.
   */
  object Names {
    def unapplySeq(name: String): Option[(String, String, Seq[String])] = {
      val names = name.trim.split(" ").toSeq
      if (names.size < 2) None
      else Some((names.last, names.head, names.drop(1).dropRight(1)))
    }
  }
}

extractingSequence.app


/*
def unapplySeq(object: S): Option[Seq[T]]
 */





