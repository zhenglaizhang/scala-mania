
/*
"this.type" is the type that is inhabited by only ONE object, the object referenced to by "this". It's obviously not the return type you want for a clone method.

Generally, x.type is the type of any stable identifier x.

 */


val x = "x"
val y = x

//y: x.type
/*
The static type of y was inferred as String, not as x.type.
:22: error: type mismatch;
 */
y: y.type


val c: x.type = x // explicitly type c with x.type








/*
Composable Builder Pattern using this.type in Scala
 */

trait Buildable[T] {
  def build: T
}


trait HeadBuilder extends Buildable[String] {
  var eyeColor = "brown"

  var hairColor = "red"

  def withEyeColor(color: String): this.type = {
    eyeColor = color
    this
  }

  def withHairColor(color: String): this.type = {
    hairColor = color
    this
  }

  def build = s"eyes: $eyeColor, hair: $hairColor"
}

trait BodyBuilder extends Buildable[String] {
  var limbCount = 4

  def withNumLibs(count: Int): this.type = {
    limbCount = count
    this
  }

  def build = s"limb: $limbCount"
}


class PersonBuilder extends BodyBuilder with HeadBuilder with Buildable[String] {
  override def build: String =
    Seq(super[BodyBuilder].build, super[HeadBuilder].build).mkString(", ")
}

val person = new PersonBuilder().withHairColor("blue").withNumLibs(3).withEyeColor("black").build
println(person)