/*
NullPointerException

A value of null is often abused to represent an absent optional value.

Groovy has the null-safe operator for accessing properties, so that foo?.bar?.baz will not throw an exception if either foo or its bar property is null, instead directly returning null


Scala tries to solve the problem by getting rid of null values altogether and providing its own type for representing optional values, i.e. values that may be present or not: the Option[A] trait.

Option[A] is a container for an optional value of type A. If the value of type A is present, the Option[A] is an instance of Some[A], containing the present value of type A. If the value is absent, the Option[A] is the object None.

By stating that a value may or may not be present on the TYPE LEVEL, you and any other developers who work with your code are forced by the compiler to deal with this possibility. There is no way you may accidentally rely on the presence of a value that is really optional.

Option is mandatory! Do not use null to denote that an optional value is absent.
 */

val greeting: Option[String] = Some("Hello world")

val greeting2: Option[String] = None

// None.type
val greeting3: None.type = None
val greeting4: Option[Nothing] = None



/*
you will need to interoperate with Java libraries or code in other JVM languages that happily make use of null to denote absent values

 For this reason, the Option companion object provides a factory method that creates None if the given parameter is null, otherwise the parameter wrapped in a Some:
 */
val absentGreeting: Option[String] = Option(null) // absentGreeting will be None
val presentGreeting: Option[String] = Option("Hello!") // presentGreeting will be Some("Hello!")


case class User(
                 id: Int,
                 firstName: String,
                 lastName: String,
                 age: Int,
                 gender: Option[String])

object UserRepository {
  private val users = Map(1 -> User(1, "John", "Doe", 32, Some("male")),
                           2 -> User(2, "Johanna", "Doe", 30, None))
  def findById(id: Int): Option[User] = users.get(id)
  def findAll = users.values
}

val user = UserRepository.findById(1)
if (user.isDefined) {
  println(user.get.firstName)
}
/*
. More importantly, if you use get, you might forget about checking with isDefined before, leading to an exception at runtime, so you haven’t gained a lot over using null.

You should stay away from this way of accessing options whenever possible!
 */

/*
 the default value you can specify as a parameter to the getOrElse method is a by-name parameter, which means that it is only evaluated if the option on which you invoke getOrElse is indeed None.
 */
val u = User(2, "Zhenglai", "Zhang", 20, None)
User(2, "Zhenglai", "Zhang", 20, None).gender.getOrElse("Not specified")

val gender = u.gender match {
  case Some(gender) => gender
  case None => "not specified"
}
println(s"Gender: $gender")

/*
pattern matching on an Option instance is rather verbose, which is also why it is usually not idiomatic to process options this way.
 */



/*
Options can be viewed as collections

Option[A] is a container for a value of type A

Even though on the type level, Option is not a collection type in Scala, options come with all the goodness you have come to appreciate about Scala collections
 */

/*
The function passed to foreach will be called exactly once, if the Option is a Some, or never, if it is None.
Performing a side-effect if a value is present
 */
UserRepository.findById(2).foreach(x => println(x.firstName))

/*
You can map an Option[A] to an Option[B]. This means that if your instance of Option[A] is defined, i.e. it is Some[A], the result is Some[B], otherwise it is None.

If you compare Option to List, None is the equivalent of an empty list
 */

val age = UserRepository.findById(1).map(_.age)

val gender2 = UserRepository.findById(1).map(_.gender)



/*
Just like you can flatMap a List[List[A]] to a List[B], you can do the same for an Option[Option[A]]
 */
val gender4 = UserRepository.findById(1).flatMap(_.gender) // gender is Some("male")
val gender5 = UserRepository.findById(2).flatMap(_.gender) // gender is None
val gender6 = UserRepository.findById(3).flatMap(_.gender) // gender is None


val names: List[Option[String]] = List(Some("Johanna"), None, Some("Daniel"))
names.map(_.map(_.toUpperCase)) // List(Some("JOHANNA"), None, Some("DANIEL"))

/*
The one element of any Some[String] in the original list is unwrapped and put into the result list, whereas any None value in the original list does not contain any element to be unwrapped. Hence, None values are effectively filtered out.
 */
names.flatMap(xs => xs.map(_.toUpperCase)) // List("JOHANNA", "DANIEL")



/*
If the instance of Option[A] is defined, i.e. it is a Some[A], and the predicate passed to filter returns true for the wrapped value of type A, the Some instance is returned. If the Option instance is already None or the predicate returns false for the value inside the Some, the result is None
 */

UserRepository.findById(1).filter(_.age > 30) // Some(user), because age is > 30
UserRepository.findById(2).filter(_.age > 30) // None, because age is <= 30
UserRepository.findById(3).filter(_.age > 30) // None, because user is already None


/*
this is equivalent to nested invocations of flatMap. If the UserRepository already returns None or the Gender is None, the result of the for comprehension is None.
 */
for {
  user <- UserRepository.findById(1)
  gender <- user.gender
} yield gender

for {
  // the left side of a generator in a for comprehension is a pattern.
  User(_, _, _, _, Some(gender)) <- UserRepository.findAll
  /*
  Using a Some pattern in the left side of a generator has the effect of removing all elements from the result collection for which the respective value is None
   */
} yield gender


// chain options
case class Resource(content: String)
val resourceFromConfigDir: Option[Resource] = None
val resourceFromClasspath: Option[Resource] = Some(Resource("I was found on the classpath"))
val resource = resourceFromConfigDir orElse resourceFromClasspath getOrElse Resource("default resource")

