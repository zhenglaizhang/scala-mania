import org.scalacheck.Prop.forAll
import org.scalacheck.{Gen, Properties}

object ListProps extends Properties("List") {

  /*
  A Gen object generates a variety of different objects to pass to a Boolean expression, searching for one that will make it false.
  This gives greater confidence in the code
   */
  val initList = Gen.listOf(Gen.choose(0, 100))

  /*
  When we invoke prop.check, ScalaCheck will randomly generate List[Int] values to try to find a case that falsifies the predicates that we’ve supplied. The output indicates that ScalaCheck has generated 100 test cases
   */
  property("reverse") = forAll(initList) {
    ns => ns.reverse.reverse == ns
  } && forAll(initList) {
    nx => nx.headOption == nx.reverse.lastOption
  }

  property("size") = forAll(Gen.listOfN(10, Gen.choose(0, 100))) {nx => nx.size == 10 }

  //  property("failing") = forAll(initList)(nx => nx.reverse == nx)
}
