
/*
Partially applied functions

apply a function partially. What this means is that, when applying the function, you do not pass in arguments for all of the parameters defined by the function, but only for some of them, leaving the remaining ones blank.

Do not confuse partially applied functions with partially defined functions, which are represented by the PartialFunction type in Scala.
 */


// predicate Email => Boolean, which we aliased to the type EmailFilter
type EmailFilter = Email => Boolean

case class Email(
  subject: String,
  text   : String,
  sender : String,
  recipient: String)



type IntPairPredicate = (Int, Int) => Boolean

def sizeConstraint(predicate: IntPairPredicate, n: Int, email: Email) =
  predicate(email.text.size, n)

val gt: IntPairPredicate = _ > _
val ge: IntPairPredicate = _ >= _
val lt: IntPairPredicate = _ < _
val le: IntPairPredicate = _ <= _
val eq: IntPairPredicate = _ == _


/*
As you can see, you have to use the placeholder _ for all parameters not bound to an argument value. Unfortunately, you also have to specify the type of those arguments, which makes partial function application in Scala a bit tedious.

The reason is that the Scala compiler cannot infer these types, at least not in all cases – think of overloaded methods where it’s impossible for the compiler to know which of them you are referring to.
 */
val minimumSize: (Int, Email) => Boolean = sizeConstraint(ge, _: Int, _: Email)
val maximumSize: (Int, Email) => Boolean = sizeConstraint(le, _: Int, _: Email)


val constr20: (IntPairPredicate, Email) => Boolean = sizeConstraint(_: IntPairPredicate, 20, _: Email)
val constr30: (IntPairPredicate, Email) => Boolean = sizeConstraint(_: IntPairPredicate, 30, _: Email)



/*
From methods to function objects

When doing partial application on a method, you can also decide to not bind any parameters whatsoever. The parameter list of the returned function object will be the same as for the method. You have effectively turned a method into a function that can be assigned to a val or passed
 */

val sizeConstraintFn: (IntPairPredicate, Int, Email) => Boolean = sizeConstraint _


val min20: EmailFilter = minimumSize(20, _: Email)
val max20: EmailFilter = maximumSize(20, _: Email)

val min20_1: EmailFilter = constr20(ge, _: Email)
val max20_2: EmailFilter = constr20(le, _: Email)

