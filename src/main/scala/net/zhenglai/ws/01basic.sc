

val bi = BigInt("111111111111111111111111111111")

upper("test")


'a' to 'g' by 3

1.1 to 10.4 by 2.1


BigInt(1) to BigInt(10) by 4
val bi2 = BigInt("111111111111111111111111111111")

def upper(strings: String*): Seq[String] = {
  println(strings.getClass)
  strings.map(_.toUpperCase)
}
bi + bi2



// partial function

val pf: PartialFunction[Any, String] = {
  case s: String => "A String"
}

val pf2: PartialFunction[Any, String] = {
  case d: Double => "A Double"
}

pf2.andThen(pf)(12.0d)

pf.isDefinedAt(12.0)
pf2.isDefinedAt(12.0d)


def tryPF(x: Any, pf: PartialFunction[Any, String]) = {
  try {
    pf(x).toString
  } catch {
    case _: MatchError => "Match error"
  }
}

tryPF(12, pf)



def add(a: Int)(b: Int) = a + b
add(12)(3)
add(12){3}
add{12}{3}
add({12})({3})


def m1[A](a: A, f: A => String) = f(a)
def m2[A](a: A)(f: A => String) = f(a)
//m1(100, i => s"$i + $i") // missing parameter type
m2(100){i => s"$i + $i"}


// use last argument list for implicit arguments
