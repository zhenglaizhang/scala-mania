/*
Parametric polymorphism

In this function head, it takes a list of A’s, and returns an A. And it doesn’t matter what the A is: It could be Ints, Strings, Oranges, Cars, whatever. Any A would work, and the function is defined for every A that there can be.


Parametric polymorphism refers to when the type of a value contains one or more (unconstrained) type variables, so that the value may adopt any type that results from substituting those variables with concrete types.


 */

def head[A](xs: List[A]): A = xs.head
head(1 :: 2 :: 3 :: Nil)



/*
Subtype polymorphism
 */

object h11 {
  trait Plus[A] {
    def plus(a2: A): A
  }

  def plus[A <: Plus[A]](a1: A, a2: A): A = a1.plus(a2)
}
/*
We can at least provide different definitions of plus for A. But, this is not flexible since trait Plus needs to be mixed in at the time of defining the datatype. So it can’t work for Int and String.
 */





/*
Ad-hoc polymorphism
The third approach in Scala is to provide an implicit conversion or implicit parameters for the trait.

This is truely ad-hoc in the sense that

1. we can provide separate function definitions for different types of A
2. we can provide function definitions to types (like Int) without access to its source code
3. the function definitions can be enabled or disabled in different scopes

The last point makes Scala’s ad-hoc polymorphism more powerful than that of Haskell.
*/

trait Plus[A] {
  def plus(a1: A, a2: A): A
}

def plus[A: Plus](a1: A, a2: A): A = implicitly[Plus[A]].plus(a1, a2)