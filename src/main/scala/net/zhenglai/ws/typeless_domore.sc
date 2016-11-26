
val list: List[String] = mkList()
val Hello = "Hello"
val hello = "hello"
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
val configFile = new java.io.File("somefile.txt")
hello("Programming Scala")
val configFilePath = if (configFile.exists()) {
  configFile.getAbsolutePath
} else {
  configFile.createNewFile()
  configFile.getAbsolutePath
}
goodbye("Programming Scala")

// symbol literals
//  interned strings

//scala.Symbol("id")

//`id

// Tony Hoare
// billion dollar mistake

1.+(2) * 3
1 + 2 * 3

// Dropping dot from methods without parameters is DEPRECATED!
val xs = List("hello", "scala", "fighting")
val head1 +: head2 +: tail = Vector(1, 2, 3, 4)
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
val d = List(1, 2, 3, 4)

List(1, 2, 3, 4) filter isEven foreach println
List(1, 2, 3, 4).filter(isEven).foreach(println)
var count = 10

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

// procedure syntax
// side effect only, Unit return type inferred
def double(i: Int) {
  2 * i
}

for (
  x <- xs // generator expression
  if x.contains("l") // && !x.startsWith("s")
  if !x.contains("s")
) {
  // guard as filter
  println(x)
}

java.util.Calendar.getInstance()

def hello(name: String) =
  s"""Welcome!
  Hello, $name!
  * (Gratuitous Star!!)
      |We're glad you're here.
      | Have some extra whitespace.""".stripMargin
while (count < 0) {
  println(s"count is $count, less than 10")
  count -= 1
}

def goodbye(name: String) =
  s"""xxxGoodbye, ${name}yyy
  xxxCome again!yyy""".stripPrefix("xxx").stripSuffix("yyy")
head1
head2
tail

def isEven(n: Int) = (n % 2) == 0
d reduce (_ + _)
d.fold(10)(_ + _)
(d fold 10)(_ + _)

// partial application
val fold1 = (d fold 10) _
fold1(_ + _)

(List.empty[Int] fold 10)(_ + _)

//List.empty[Int] reduce (_ + _)
// unsupported exception reduceLeft

List.empty[Int] reduceOption (_ + _)

d map (2 * _)

d.foldRight(List.empty[Int]) {
  (x, list) => (x * 2) :: list
}

d.foldLeft(List.empty[Int]) {
  (list, x) =>
    {
      println(s"checking list: [$list]\t x: [${x * 2}]")
      list :+ x * 2
    }
}

d.foldLeft(List.empty[Int]) {
  (list, x) =>
    {
      println(s"checking list: [$list]\t x: [${x * 2}]")
      x * 2 +: list
    }
}

trait Foo {
  val x: Int
  def x(i: Int): Int
}
