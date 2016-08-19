
/*

Path Dependent Types
Parameter Dependent Types
Type Projection

This Type allows us to type-check on a Type internal to another class.

"each Outer class instance has its own Inner class", so it’s a different Type - dependent on which path we use to get there.
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

