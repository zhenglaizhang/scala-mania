
/*

Path Dependent Types
Parameter Dependent Types
Type Projection

This Type allows us to type-check on a Type internal to another class.

"each Outer class instance has its own Inner class", so it’s a different Type - dependent on which path we use to get there.


 Scala’s form of dependent types, in particular path-dependent types and dependent method types.

 sing path-dependent types is one powerful way to help the compiler prevent you from introducing bugs, as it places logic that is usually only available at runtime into types.
 */

// the path dependent type. The "path" is "inside out1".

type PathDep1 = out1.Innter
val out1 = new Outer

/*
. means that we can only refer the Innter instances that belong to a specif instance of Outer
 */
val out1in = new out1.Innter


val out2 = new Outer
val out2in = new out2.Innter
val ok: PathDep1 = out1in


class Outer {

  class Innter

}

/*
# means that we don’t refer to any specific instance, in this case Foo#Bar, every Bar inside every instance of Foo will be a valid instance
 */
val a: Outer#Innter = new out1.Innter
val b: Outer#Innter = new out2.Innter

//val err: PathDep1 = out2in

/*
Using this kind of typing is useful, we’re able to enforce getting the type from inside of a concrete parameter.
 */

class Parent {
  class Child
  def child: Child = new this.Child
}



class ChildrenContainer(val p: Parent) {
  /*
  Using the path dependent type we have now encoded in the type system, the logic, that this container should only contain children of this parent - and not "any parent"
   */
//  type ChildOfThisParent = p.Child

  /*
  Parameter Dependent Types are a form of Path Dependent Types, as we have seen before we can refer to a type nested in a specific instance with the . syntax

  Now we can use this technique inside a function parameters list
   */
  def add(c: p.Child): p.Child = p.child
}


/*
In computer science and logic, a dependent type is a type that depends on a value.

With Dependent Types we remove this separation between the two worlds(value and type), and we get two powerful features:

we have types that depend on values, which means that we can compute them in a similar way to values, this gives us more flexibility
we can define stronger constraints for the values

isSingleton : Bool -> Type
isSingleton True = Nat
isSingleton False = List Nat

this is a function that computes a type as result and not a value, we will be able then to use this in a “normal” function (from value to value), to compute the type of one value depending on another value.
 */


/*
Scala is not a fully dependently typed language and we have to forget some of the amazing things we can do with Idris
 */




object bad {
  object Franchise {
    case class Character(name: String)
  }
  class Franchise(name: String) {
    import Franchise.Character
    def createFanFiction(
      lovestruck: Character,
      objectOfDesire: Character): (Character, Character) = (lovestruck, objectOfDesire)
  }

  val starTrek = new Franchise("Star Trek")
  val starWars = new Franchise("Star Wars")

  val quark = Franchise.Character("Quark")
  val jadzia = Franchise.Character("Jadzia Dax")

  val luke = Franchise.Character("Luke Skywalker")
  val yoda = Franchise.Character("Yoda")

  // bad things happens
  starTrek.createFanFiction(lovestruck = jadzia, objectOfDesire = luke)
}

object normal {
  object Franchise {
    case class Character(name: String, franchise: Franchise)
  }
  class Franchise(name: String) {
    import Franchise.Character
    def createFanFiction(
      lovestruck: Character,
      objectOfDesire: Character): (Character, Character) = {
      // It’s the kind of fail-fast behaviour
      require(lovestruck.franchise == objectOfDesire.franchise)
      (lovestruck, objectOfDesire)
    }
  }
}

/*
there is a way to fail even faster – not at runtime, but at compile time. To achieve that, we need to encode the connection between a Character and its Franchise at the type level.

In Scala, a nested type is bound to a specific instance of the outer type, not to the outer type itself. This means that if you try to use an instance of the inner type outside of the instance of the enclosing type, you will face a compile error:
 */


class A {
  class B
  var b: Option[B] = None
}
val a1 = new A
val a2 = new A
val b1 = new a1.B
val b2 = new a2.B
a1.b = Some(b1)
//a2.b = Some(b1) // does not compile
/*
The dot syntax represents the path to the type, going along concrete instances of other types. Hence the name, path-dependent types.
 */

/*
We can put these to use in order to prevent characters from different franchises making out with each other:
 */
class Franchise(name: String) {
  /*
  Now, the type Character is nested in the type Franchise, which means that it is dependent on a specific enclosing instance of the Franchise type.
   */
  case class Character(name: String)
  def createFanFictionWith(
    lovestruck: Character,
    objectOfDesire: Character): (Character, Character) = (lovestruck, objectOfDesire)
}

val starTrek = new Franchise("Star Trek")
val starWars = new Franchise("Star Wars")

val quark = starTrek.Character("Quark")
val jadzia = starTrek.Character("Jadzia Dax")

val luke = starWars.Character("Luke Skywalker")
val yoda = starWars.Character("Yoda")
/*
You can already see in how our Character instances are created that their types are bound to a specific franchise.
 */
starTrek.createFanFictionWith(lovestruck = quark, objectOfDesire = jadzia)
starWars.createFanFictionWith(lovestruck = luke, objectOfDesire = yoda)
//starTrek.createFanFictionWith(lovestruck = jadzia, objectOfDesire = luke)


/*
This technique also works if our method is not defined on the Franchise class, but in some other module. In this case, we can make use of dependent method types, where the type of one parameter depends on a previous parameter:
 */
def createFanFiction(f: Franchise)(lovestruck: f.Character, objectOfDesire: f.Character) =
  (lovestruck, objectOfDesire)


//the type of the lovestruck and objectOfDesire parameters depends on the Franchise instance passed to the method. Note that this only works if the instance on which other types depend is in its own parameter list.

object AwesomeDB {
  abstract class Key(name: String) {
    type Value
  }
}
import AwesomeDB.Key
class AwesomeDB {
  import collection.mutable.Map
  val data = Map.empty[Key, Any]
  def get(key: Key): Option[key.Value] = data.get(key).asInstanceOf[Option[key.Value]]
  def set(key: Key)(value: key.Value): Unit = data.update(key, value)
}

trait IntValued extends Key {
  type Value = Int
}
trait StringValued extends Key {
  type Value = String
}
object Keys {
  val foo = new Key("foo") with IntValued
  val bar = new Key("bar") with StringValued
}

val dataStore = new AwesomeDB
/*
The methods on AwesomeDB refer to that type without ever knowing or caring about the specific manifestation of this abstract type.
 */
dataStore.set(Keys.foo)(23)
val i: Option[Int] = dataStore.get(Keys.foo)
//dataStore.set(Keys.foo)("23") // does not compile

/*
the cake pattern, which is a technique for composing your components and managing their dependencies, relying solely on features of the language.
 */

/*
In general, whenever you want to make sure that objects created or managed by a specific instance of another type cannot accidentally or purposely be interchanged or mixed, path-dependent types are the way to go.

Path-dependent types and dependent method types play a crucial role for attempts to encode information into types that is typically only known at runtime, for instance heterogenous lists, type-level representations of natural numbers and collections that carry their size in their type.
 */