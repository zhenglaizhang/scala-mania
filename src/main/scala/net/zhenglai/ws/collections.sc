import java.net.URLEncoder

/*
Often you can write rather complex operations in just one line instead of many loops and tempory variables thanks to methods like filter, map, foldLeft, reduceLeft, etc.
 */


val params = Map("fantasy_book_1" -> "The Hobbit",
                  "fantasy_book_2" -> "The Lord of the Rings",
                  "science_book_1" -> "Tropical Ecology")

val birds = List("Golden Eagle", "Gyrfalcon", "American Robin",
                  "Mountain BlueBird", "Mountain-Hawk Eagle")

val words = List("one", "two", "one", "three", "four", "two", "one")

val queryString = params.filterKeys(_.startsWith("fantasy"))
                  .map(t => URLEncoder.encode(t._1, "UTF-8") -> URLEncoder.encode(t._2, "UTF-8"))
                  .foldLeft("?")((a, t) => a + (t._1 + "=" + t._2 + "&"))
                  .dropRight(1)

/*
Always try to make the code as easy to read as possible.

I prefer to add the return type to the method declaration. It makes the code easier to read and when you change it later and make a mistake and return a different data type the compiler will tell you immediately.
 */
def createFantasyBookQueryString(parameters: Map[String, String]): String = {
  params.filterKeys(_.startsWith("fantasy"))
  .map(t => URLEncoder.encode(t._1, "UTF-8") -> URLEncoder.encode(t._2, "UTF-8"))
  .foldLeft("?")((a, t) => a + (t._1 + "=" + t._2 + "&"))
  .dropRight(1)
}


val groupByFirstLetter = birds.groupBy(_.charAt(0))

val groupByLength = birds.groupBy(_.length)

// def groupBy [K] (f: (A) ⇒ K): Map[K, Traversable[A]]
val kinds = birds groupBy {
  case bird if bird.contains("Mountain") => "mountain"
  case bird if bird.contains("Eagle") => "eagle"
  case _ => "unknown"
}

val counts = words.groupBy(w => w).mapValues(_.size)