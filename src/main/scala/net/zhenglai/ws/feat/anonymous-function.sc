
(x: Int) => x + 1

new Function[Int, Int] {
  def apply(x: Int): Int = x + 1
}

(x: Int, y: Int) => s"($x, $y)"

() -> {
  val props = System.getProperties.keys()
  while (props.hasMoreElements) {
    val prop = props.nextElement()
    println(s"$prop \t=>\t ${System.getProperty(prop.toString)}")
  }
}

//Int => Int

//(Int, Int) => String
//() => String

// are shorthand for following types
//Function1[Int, Int]
//Function2[Int, Int, String]
//Function0[String]

// Function1[(Int, Int), String]
(x: (Int, Int), y: String) => x._1.toString + y
