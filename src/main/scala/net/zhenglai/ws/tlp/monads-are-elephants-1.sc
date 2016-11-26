
/*

## Monads are Container Types

Option is always either Some(value) or None.

It might not be clear how List and Option are related, but if you consider an Option as a stunted List that can only have 0 or 1 elements, it helps. Trees and Sets can also be monads.

But remember that monads are elephants, so with some monads you may have to squint a bit to see them as containers.


Monads are parameterized. e.g. List[Int], List[String]


## Monads Support Higher Order Functions

Monads are containers which have several higher order functions defined. Or, since we're talking about Scala, monads have several higher order methods.

One such method is map. Map does not change the kind of monad, but may change its parameterized type


## Monads are Combinable

This possible sophistication is why explanations of monads will often use "join" instead of "flatten." "Join" neatly indicates that some aspect of the outer monad may be combined (joined) with some aspect of the inner monad.

Now, Scala does not require you to write flatten explicitly. But it does require that each monad have a method called flatMap (map + flatten)

many papers on monads use the word "bind" instead of "flatMap" and Haskell uses the ">>=" operator.


## Monads Can Be Built In Different Ways
So we've seen how the flatMap method can be built using map. It's possible to go the other way: start with flatMap and create map based on it.

In most papers on monads the concept is called "unit," in Haskell it's called "return." Scala is an object oriented language so the same concept might be called a single argument "constructor" or "factory."

unit takes one value of type A and turns it into a monad of type M[A]. For List, unit(x) == List(x) and for Option, unit(x) == Some(x).

Scala does not require a separate "unit" function or method, and whether you write it or not is a matter of taste.



## Conculsion
Scala monads must have map and flatMap methods. Map can be implemented via flatMap and a constructor or flatMap can be implemented via map and flatten.

flatMap is the heart of our elephantine beast. When you're new to monads, it may help to build at least the first version of a flatMap in terms of map and flatten. Map is usually pretty straight forward. Figuring out what makes sense for flatten is the hard part.

As you move into monads that aren't collections you may find that flatMap should be implemented first and map should be implemented based on it and unit.

I'm using a bit of shorthand here. Scala doesn't "require" any particular method names to make a monad. You can call your methods "germufaBitz" or "frizzleMuck". However, if you stick with map and flatMap then you'll be able to use Scala's "for comprehensions"
 */

val one = Some(1)
val oneString = one map (_.toString)
assert(oneString == Some("1"))

def flatten[A](outer: Option[Option[A]]): Option[A] =
  outer match {
    case None        => None
    case Some(inner) => inner
  }

// A => M[A] constructor as unit
class M[A](value: A) {
  def map[B](f: A => B): M[B] = ???

  def flatMap[B](f: A => M[B]): M[B] = flatten(map(f))

  private def flatten[B](x: M[M[B]]): M[B] = ???
}

class M2[A](value: A) {
  private def unit[B](value: B) = new M2(value)

  // first f is applied to x, then unit is applied to the result.
  def map[B](f: A => B): M2[B] = flatMap(x => unit(f(x)))
  def flatMap[B](f: A => M2[B]): M2[B] = ???

}