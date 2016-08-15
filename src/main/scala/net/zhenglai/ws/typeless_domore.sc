
// add return type explicitly
def mkList(strings: String*) = {
  if (strings.isEmpty) {
//    List(0)
//    Nil // or
    List.empty[String]
  } else {
    strings.toList
  }
}

val list: List[String] = mkList()



// procedure syntax
// side effect only, Unit return type inferred
def double(i: Int) { 2 * i }
println(double(12))


// auxiliary constructor
// keyword this



// type declaration
//  keyword type


// no break
// no continue


println(3e-5)
3E6


"""Programming\nScala"""
"""He exclaimed, "Scala is great!" """
"""First line\n
Second line\t
Fourth line
Five line"""




def hello(name: String) = s"""Welcome!
  Hello, $name!
  * (Gratuitous Star!!)
                              |We're glad you're here.
                    | Have some extra whitespace.""".stripMargin
hello("Programming Scala")

def goodbye(name: String) =
  s"""xxxGoodbye, ${name}yyy
  xxxCome again!yyy""".stripPrefix("xxx").stripSuffix("yyy")
goodbye("Programming Scala")


// symbol literals
//  interned strings

//scala.Symbol("id")

//`id


// Tony Hoare
// billion dollar mistake


1.+(2)*3
1 + 2 * 3


// Dropping dot from methods without parameters is DEPRECATED!

val Hello = "Hello"
val hello = "hello"
"Hello" match {
  case Hello => "Got Hello"
  case hello => "Got hello"
}

"Hello" match {
    // match anything and bound to hello
  case hello => "Got hello"
    // case `hello` => "Got hello"

    // unreachable code
  case Hello => "Got Hello"
}


// You put the cursor on a variable definition, hit Alt+Enter and IntelliJ will offer you to add a type annotation to the declaration


def isEven(n: Int) = (n % 2) == 0

List(1, 2, 3, 4) filter isEven foreach println
List(1, 2, 3, 4).filter(isEven).foreach(println)


val configFile = new java.io.File("somefile.txt")
val configFilePath = if (configFile.exists()) {
  configFile.getAbsolutePath
} else {
  configFile.createNewFile()
  configFile.getAbsolutePath
}


val xs = List("hello", "scala", "fighting")

for (x <- xs  // generator expression
  if x.contains("l")  // && !x.startsWith("s")
  if !x.contains("s")
) { // guard as filter
  println(x)
}

java.util.Calendar.getInstance()

var count = 10
while (count < 0) {
  println(s"count is $count, less than 10")
  count -= 1
}

