import scala.collection.mutable.ArrayBuffer

/*
We refer to a Scala’s typesystem as being "unified" because there is a "Top Type", Any. This is different than Java,

Scala takes on the idea of having one common Top Type for all Types by introducing Any. Any is a supertype of both AnyRef and AnyVal.

AnyRef is the "object world" of Java (and the JVM), it corresponds to java.lang.Object, and is the supertype of all objects. AnyVal on the other hand represents the "value world" of Java, such as int and other JVM primitives.
 */

/*
Thanks to this hierarchy, we’re able to define methods taking Any - thus being compatible with both scala.Int instances as well as java.lang.String:
 */
class Person

val allThings = ArrayBuffer[Any]()

val myInt = 42 // Int, kept as low-level `int` during runtime

/*
For the Typesystem it’s transparent, though on the JVM level once we get into ArrayBuffer[Any] our Int instances will have to be packed into objects

35: invokevirtual #47  // Method myInt:()I
38: invokestatic  #53  // Method scala/runtime/BoxesRunTime.boxToInteger:(I)Ljava/lang/Integer;
41: invokevirtual #57  // Method scala/collection/mutable/ArrayBuffer.$plus$eq:(Ljava/lang/Object;)Lscala/collection/mutable/ArrayBuffer;

This way, by having a smart compiler and treating everything as an object in this common hierarchy we’re able to get away from the "but primitives are different" edge-cases, at least at the level of our Scala source code - the compiler takes care of it for us. On JVM level, the distinction is still there of course, and scalac will do it’s best to keep using primitives wherever possible, as operations on them are faster, and take less memory
 */
allThings += myInt // Int (extends AnyVal)
// has to be boxed (!) -> becomes java.lang.Integer in the collection (!)

allThings += new Person() // Person (extends AnyRef), no magic here

def check(in: AnyVal) = ()

check(42) // Int -> AnyVal
check(13.37) // Double -> AnyVal

//check(new Object) // -> AnyRef = fails to compile