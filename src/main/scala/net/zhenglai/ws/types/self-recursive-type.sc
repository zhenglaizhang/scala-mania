

/*
Self-recursive Types are referred to as F-Bounded Types in most literature

the subtype constraint itself is parametrized by one of the binders occurring on the left-hand side
 */


object NotGood {

  val apple  = new Apple
  val orange = new Orange

  /*
  the trait Fruit has no clue about the types extending it, so it’s not possible to restrict the compareTo signature to only allow "the same subclass as `this`" in the parameter
   */
  trait Fruit {
    final def compareTo(other: Fruit) = true
  }

  class Apple extends Fruit

  class Orange extends Fruit

  apple compareTo orange //  compiles, but we want to make this NOT compile!
}

// I take some T, that T must be a Fruit[T]
trait Fruit[T <: Fruit[T]] {

  // apples can only be compared to apples
  final def compareTo(other: Fruit[T]) = true
}

class Apple extends Fruit[Apple]
class Orange extends Fruit[Orange]

val apple = new Apple
val orange = new Orange

//orange compareTo apple

object `りんご`  extends Apple
object Jabłuszko extends Apple

`りんご` compareTo Jabłuszko
// true

/*
	You could get the same type-safety using more fancy tricks, like path dependent types or implicit parameters and type classes. But the simplest thing that does-the-job here would be this.
 */