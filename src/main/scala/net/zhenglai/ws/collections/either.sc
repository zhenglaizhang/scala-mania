
import net.zhenglai.ds._

case class Employee(name: String, age: Int, salary: Double)

for {
  age <- Right(42)
  name <- Left("Invalid Name!")
  salary <- Right(10000.00)
} yield Employee(name, age, salary) //> res0: ch4.either.Either[String,ch4.either.Employee] = Left(Invalid Name!)
for {
  age <- Right(42)
  name <- Right("Jonny Cash!")
  salary <- Right(10000.00)
} yield Employee(name, age, salary) //> res1: ch4.either.Either[Nothing,ch4.either.Employee] = Right(Employee(Jonny
//|  Cash!,42,10000.0))