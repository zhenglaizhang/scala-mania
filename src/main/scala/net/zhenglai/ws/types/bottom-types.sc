

var test = false
object h {
  val thing: Int =
    if (test)
      42
    else
      throw new Exception("Whoops!")    // Nothing
}
/*
the type of the if block is Int (easy), the type of the else block is Nothing (interesting). The inferencer was able to infer that the thing value, will only ever be of type Int. This is because of the Bottom Type property of Nothing.

A very nice intuition about how bottom types work is: "Nothing extends everything."


Type inference always looks for the "common type" of both branches in an if stamement

    Nothing -> [Int] -> ... -> AnyVal -> Any

The same reasoning can be applied to the second Bottom Type in Scala - Null.
 */


/*
  Null -> [String] -> AnyRef -> Any


  scala> :type if (false) 23 else null
  Any


  scala> :type -v 12
// Type signature
Int

// Internal Type structure
TypeRef(TypeSymbol(final abstract class Int extends AnyVal))


abstract class AnyVal extends Any with NotNull
 */
val thing: String =
  if (test)
    "Yay!"
  else
    null  // :Null

val x =   // :Any
  if (test)
    23
  else
    null


/*
Null extends all AnyRefs whereas Nothing extends anything

Int  -> NotNull -> AnyVal -> [Any]
Null            -> AnyRef -> [Any]
 */