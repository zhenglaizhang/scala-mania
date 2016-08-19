
/*
 pattern matching expression
 */

case class Player(name: String, score: Int)

/*
 return type of Unit, its sole purpose is to perform a side effect, namely printing a message.
 */
def printMessage(player: Player): Unit = player match {
    /*
     Their return value is what is returned by the block belonging to the first matched pattern.
     */
  case Player(_, score) if score > 10000 => println("Got a job, dude!")
  case Player(name, _) => println(s"Hey $name, nice to see you again!")
}

/*
pure function, whose return type is String
 */
def message(player: Player) = player match {
  case Player(_, score) if score > 10000 => "Got a job, dude!"
  case Player(name, _) => s"Hey $name, nice to see you again"
}

def printMessage2(player: Player) = println(message(player))

printMessage(Player("Zhenglai", 1000000))
printMessage2(Player("Zhenglai", 1000000))

/*
pattern in value definition

Another place in which a pattern can occur in Scala is in the left side of a value definition (and in a variable definition, for that matter, but we want write our Scala code in a functional style, so you won’t see a lot of usage of variables in this series).
 */

def currentPlayer = Player("Zhenglai", 2000)

/*
You can do this with any pattern, but generally, it is a good idea to make sure that your pattern always matches. Otherwise, you will be the witness of an exception at runtime.
 */
val Player(name, _) = currentPlayer
doSomethingWithTheName(name)

def doSomethingWithTheName(name: String) = ()


def scores: List[Int] = List(12)

val best :: rest = scores
println(s"The score of our champion is $best")
// might throw MatchError if score is empty

//A safe and very handy way of using patterns in this way is for destructuring case classes whose type you know at compile time.

def gameResult: (String, Int) = ("Zhenglai", 2000)
val (gamerName, score) = gameResult
println(s"$gamerName got $score scores")





/*
Patterns in for comprehensions

for comprehension can also contain value definitions. And everything you learnt about the usage of patterns in the left side of value definitions holds true for value definitions in for comprehensions.
 */

def gameResults(): Seq[(String, Int)] =
  ("Daniel", 3500) +: ("Melissa", 13000) +: ("John", 7000) +: Nil

def hallOfFame = for {
  result <- gameResults()
  (name, score) = result
  if (score > 5000)
} yield name

hallOfFame

def hallOfFame2 = for {
  /*
   in for comprehensions, the left side of a generator is also a pattern.

   patterns in the left side of generators can already be used for filtering purposes – if a pattern on the left side of a generator does not match, the respective element is filtered out.
   */
  (name, score) <- gameResults()

  // guard clause
  if (score > 5000)
} yield name


val lists = List(1, 2, 3) :: List.empty :: List(5, 3) :: Nil
for {
  /*
  The pattern on the left side of the generator does not match for empty lists. This will not throw a MatchError, but result in any empty list being removed.
   */
  list @ head :: _ <- lists
} yield list.size
