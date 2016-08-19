
/*
It’s not really another kind of type, but a trick we can use to make our code more readable:


Note that when you create an alias for a class, you do not alias its companion object in tandem. For example, assuming you’ve got case class Person(name: String) and an alias type User = Person, calling User("John") will result in an error, as Person("John") (what we could expect to be effectively called here) implicitly invokes apply method from Person companion object that is not aliased in this case.
 */

type User = String
type Age = Int

val data: Map[User, Age] = Map.empty