
/*
Composability naturally entails reusability.

 it’s a trait that is definitely true for pure functions, i.e. functions that do not have any side-effects and are referentially transparent.

 A higher-order function, as opposed to a first-order function, can have one of three forms:

  * One or more of its parameters is a function, and it returns some value.
  * It returns a function, but none of its parameters is a function.
  * Both of the above: One or more of its parameters is a function, and it returns a function.
 */

case class Email(subject: String, text: String, sender: String, recipient: String)


/*
we have a filtering function that makes use of a predicate, a function of type Email => Boolean to determine whether the email is to be blocked.
 */
type EmailFilter = Email => Boolean


/*
Note that we are using a type alias for our function, so that we can work with more meaningful names in our code.
 */
def newMailsForUser(mails: Seq[Email], f:EmailFilter) = mails.filter(f)


//in order to allow the user to configure their email filter, we can implement some factory functions that produce EmailFilter functions configured to the user’s liking

def sentByOneOf: Set[String] => EmailFilter =
  senders => ( email => senders.contains(email.sender) )
val notSentByAnyOf: Set[String] => EmailFilter =
  senders => email => !senders.contains(email.sender)
val minimumSize: Int => EmailFilter = n => email => email.text.size >= n
val maximumSize: Int => EmailFilter = n => email => email.text.size <= n
/*
Each of these four vals is a function that returns an EmailFilter, the first two taking as input a Set[String] representing senders, the other two an Int representing the length of the email body.
 */
val emailFilter: EmailFilter = notSentByAnyOf(Set("johndoe@example.com"))
val mails = Email(
                   subject = "It's me again, your stalker friend!",
                   text = "Hello my friend! How are you?",
                   sender = "johndoe@example.com",
                   recipient = "me@example.com") :: Nil
newMailsForUser(mails, emailFilter) // returns an empty list
/*
We can use our factory functions to create arbitrary EmailFilter functions, depending on the user’s requirements.
 */




// Reusing existing functions
// the composable nature of functions made it easy to stick to the DRY principle.
type SizeChecker = Int => Boolean
val sizeConstraint: SizeChecker => EmailFilter = f => email => f(email.text.size)
val minimumSize2: Int => EmailFilter = n => sizeConstraint(_ >= n)
val maximumSize2: Int => EmailFilter = n => sizeConstraint(_ <= n)




/*
Function composition
 */

def complement[A](predicate: A => Boolean) = (a: A) => !predicate(a)

/*
Given two functions f and g, f.compose(g) returns a new function that, when called, will first call g and then apply f on the result of it. Similarly, f.andThen(g) returns a new function that, when called, will apply g to the result of f.
 */

val notSentByOf2 = sentByOneOf andThen (g => complement(g))


//Using Scala’s placeholder syntax for anonymous functions, we could write this more concisely as:
val notSentByAnyOf3 = sentByOneOf andThen(complement(_))

/*
given a complement function, you could also implement the maximumSize predicate in terms of minimumSize instead of extracting a sizeConstraint function.
 */




// Composing predicates

def any[A](predicates: (A => Boolean)*): A => Boolean =
  a => predicates.exists(_(a))

def none[A](predicates: (A => Boolean)*) = complement(any(predicates: _*))

// every function works by checking that none of the complements to the predicates passed to it holds true.
def every[A](predicates: (A => Boolean)*) = none(predicates.view.map(complement(_)): _*)

val filter: EmailFilter = every(
  notSentByAnyOf3(Set("zhenglaizhang@hotmail.com")),
  minimumSize2(100),
  maximumSize2(100000))




// Composing a transformation pipeline
val addMissingSubject = (email: Email) =>
  if (email.subject.isEmpty) email.copy(subject = "No Subject")
  else email

val checkSpelling = (email: Email) =>
  email.copy(text = email.text.replaceAll("your", "you're"))

val removeInappropriateLanguage = (email: Email) =>
  email.copy(text = email.text.replaceAll("dynamic typing", "**CENSORED**"))

val addAdvertismentToFooter = (email: Email) =>
  email.copy(text = email.text + "\nThis mail sent via Super Awesome Free Mail")

val pipeline = Function.chain(Seq(
  addMissingSubject,
  checkSpelling,
  removeInappropriateLanguage,
  addAdvertismentToFooter
))



/*
chaining partial functions

partial functions can be used to create a nice alternative to the chain of responsibility pattern: The orElse method defined on the PartialFunction trait allows you to chain an arbitrary number of partial functions, creating a composite partial function. The first one, however, will only pass on to the next one if it isn’t defined for the given input.
 */

//val handler = fooHandler orElse barHandler orElse bazHandler

/*
Lifting partial functions

Also, sometimes a PartialFunction is not what you need. If you think about it, another way to represent the fact that a function is not defined for all input values is to have a standard function whose return type is an Option[A] – if the function is not defined for an input value, it will return None, otherwise a Some[A].

If that’s what you need in a certain context, given a PartialFunction named pf, you can call pf.lift to get the normal function returning an Option. If you have one of the latter and require a partial function, call Function.unlift(f)
 */