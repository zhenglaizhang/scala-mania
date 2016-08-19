
val songTitles = List("The White Hare", "Childe the Hunter", "Take no Rogues")
songTitles map {
  _.toLowerCase // anonymous function with Scala placeholder syntax
}

val wordFrequencies = ("habitual", 6) :: ("and", 56) :: ("consuetudinary", 2) ::
  ("additionally", 27) :: ("homely", 5) :: ("society", 13) :: Nil

def wordsWithoutOutliers(wordFrequencies: Seq[(String, Int)]): Seq[String] =
  wordFrequencies.filter(wf => wf._2 > 3 && wf._2 < 25).map(_._1)

wordsWithoutOutliers(wordFrequencies)

/*
Thankfully, Scala provides an alternative way of writing anonymous functions: A pattern matching anonymous function is an anonymous function that is defined as a block consisting of a sequence of cases, surrounded as usual by curly braces, but without a match keyword before the block.
 */
wordFrequencies.filter {
  /*
  we have only used a single case in each of our anonymous functions, because we know that this case always matches – we are simply decomposing a data structure whose type we already know at compile time, so nothing can go wrong here. This is a very common way of using pattern matching anonymous functions.
   */
  case (_, f) => f > 3 && f < 25
} map {
  case (w, _) => w
}


/*
Please note that you have to specify the type of the value here, the Scala compiler cannot infer it for pattern matching anonymous functions.

Make sure your cases cover all possible inputs. Otherwise, you will risk a MatchError at runtime.
 */
val predicate: ((String, Int)) => Boolean = {
  case (_, f) => f > 3 && f < 25
}

val transform: ((String, Int)) => String = {
  case (w, _) => w
}

wordFrequencies.filter(predicate).map(transform)