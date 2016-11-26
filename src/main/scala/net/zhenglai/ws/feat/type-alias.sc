
class ConcreteClass {
  type DataType = List[Map[Int, (String, String)]]

  def foo(a: DataType): Unit = {

  }
}

object ConcreteClass {
  val DataType = List
}

// define my custom object with apply
//  In REPL, the companions must be defined together using the :paste mode; however, in the package object, that is not an issue.
object DataType {
  def apply(): ConcreteClass#DataType = Nil
}

val d = ConcreteClass.DataType.empty

type IntSeq = Seq[Int]

val IntSeq = Seq

val t = DataType()

IntSeq(1, 2, 3)