
/*
def collect[B](pf: PartialFunction[A, B])

This method returns a new sequence by applying the given partial function to all of its elements – the partial function both filters and maps the sequence.

For each element in the sequence, it first checks if the partial function is defined for it by calling isDefinedAt on the partial function. If this returns false, the element is ignored. Otherwise, the result of applying the partial function to the element is added to the result sequence.


Partial Function =>
  In short, it’s a unary function that is known to be defined only for certain input values and that allows clients to check whether it is defined for a specific input value.

the PartialFunction trait provides an isDefinedAt method. As a matter of fact, the PartialFunction[-A, +B] type extends the type (A) => B (which can also be written as Function1[A, B]), and a pattern matching anonymous function is always of type PartialFunction.

Due to this inheritance hierarchy, passing a pattern matching anonymous function to a method that expects a Function1, like map or filter, is perfectly fine, as long as that function is defined for all input values, i.e. there is always a matching case.
 */
val wordFrequencies = ("habitual", 6) :: ("and", 56) :: ("consuetudinary", 2) ::
  ("additionally", 27) :: ("homely", 5) :: ("society", 13) :: Nil

val pf: PartialFunction[(String, Int), String] = {
  /*
  We added a guard clause to our case, so that this function will not be defined for word/frequency pairs whose frequency is not within the required range.
   */
  case (word, freq) if freq > 3 && freq < 25 => word
}


val pf2 = new PartialFunction[(String, Int), String] {
  override
  def apply(wordFrequency: (String, Int)): String = wordFrequency match {
    case (word, freq) if freq > 3 && freq < 25 => word
  }

  override
  def isDefinedAt(x: (String, Int)): Boolean = x match {
    case (word, freq) if freq > 3 && freq < 25 => true
    case _                                     => false
  }
}


/*
map

Map over the collection, transforming each element that matches a pattern. Throw an exception if any element does not match.
 */
//wordFrequencies.map(pf2)
// MatchError

/*
collect

Map over the collection, transforming each element that matches a pattern. Silently discard elements that do not match
 */
wordFrequencies.collect(pf2)

wordFrequencies.collect {
  case (word, freq) if freq > 3 && freq < 25 => word
}

wordFrequencies.collectFirst {
  case (word, freq) if freq > 3 && freq < 25 => word
}.getOrElse("Not found")

/*
map safely

Map over the collection, safely destructuring and then transforming each element.

safe destructuring semantics, the most concise solution is to explicitly type your collection to prevent unintended widening and use explicit pattern matching
 */
val xs = List((1,2)->3,(4,5)->6,(7,8)->9)

xs map { case ((x, y), u) => ((y, x), u) }
(xs: List[((Int, Int), Int)]) map {
  case ((x, y), u) => ((y, x), u)
}


/*
Partial function provide the means to be chained, allowing for a neat functional alternative to the chain of responsibility pattern known from object-oriented programming.

The way an Akka actor processes messages sent to it is defined in terms of a partial function.
 */

/*
a cleaner way to pattern-match in Scala anonymous functions
 */
val b = List(1, 2)
b map {
  case 1 => "one"
  case 2 => "two"
}

b map {x =>
  x match {
    case 1 => "one"
    case 2 => "two"
  }
}

xs map { case (a,b) => (a.swap, b) }
xs map { case ((a, b), c) => (b, a) -> c}


val matchMe = "Foo"

/*
Use anonymous function with foreach
 */
Seq("Hello", "Foo") foreach {
  case `matchMe` => s"Do something special with $`matchMe`"
  case nonSpecialKey => s"Do something with non special $nonSpecialKey"
}



/*
he expression { case (a, b) => a + b } is interpreted differently based on the expected type. In your definition of f1 it created a PartialFunction[(Int, Int), Int] which was cast to a Function1[(Int, Int), Int], i.e. ((Int, Int)) => Int whereas in the definition of f2 it created a Function2[Int, Int, Int], i.e. (Int, Int) => Int
 */
val f1: ((Int, Int)) => Int = { case (a, b) => a + b }
val f2: (Int, Int) => Int = { case (a, b) => a + b }