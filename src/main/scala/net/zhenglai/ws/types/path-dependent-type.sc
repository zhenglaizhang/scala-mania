
/*
This Type allows us to type-check on a Type internal to another class.


"each Outer class instance has its own Inner class", so it’s a different Type - dependent on which path we use to get there.
 */

// the path dependent type. The "path" is "inside out1".

type PathDep1 = out1.Innter
val out1 = new Outer
val out1in = new out1.Innter


val out2 = new Outer
val out2in = new out2.Innter
val ok: PathDep1 = out1in

class Outer {

  class Innter

}

//val err: PathDep1 = out2in

/*
Using this kind of typing is useful, we’re able to enforce getting the type from inside of a concrete parameter.
 */

class Parent {
  class Child
}

class ChildrenContainer(p: Parent) {
  /*
  Using the path dependent type we have now encoded in the type system, the logic, that this container should only contain children of this parent - and not "any parent"
   */
  type ChildOfThisParent = p.Child
  def add(c: ChildOfThisParent) = ???
}