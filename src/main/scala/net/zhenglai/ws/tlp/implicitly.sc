
/*
comparing types that will be used to check the results of type-level computations later.
 */

def myImplicitly[T](implicit e: T): T = e
// This is useful for capturing an implicit value that is in scope and has type T.

// It is commonly used to check if an implicit value of type T is available and return it if so is the case.
/*
The method doesn't exactly check; it seems to cause a compile error if there isn't an implicit value available and, if there is, seems to retrieve it
 */

implicit val a = "test"

// search for an implicit value of type String and assign it to b
val b = implicitly[String]

// search for an implicit value of type Int and assign it to c
// failed
//val c = implicitly[Int]


// retrieve an implicit parameter introduced by a Context Bound
def foo[A: Ordering](a1: A, a2: A) = implicitly[Ordering[A]].compare(a1, a2)


implicitly[Ordering[(Int, String)]].compare( (1, "b"), (1, "a") )