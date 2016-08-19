import java.io.StringReader

/*
Structural Types are often described as "type-safe duck typing"

does it implement interface X? => "does it have a method with this signature?"
 */


type JavaClosable = java.io.Closeable


// type alias for structural type
type OpenerCloser = {
  def open(): Unit
  def close(): Unit
}

/*
 It basically says that the only thing we expect from the type is that it should have this method. It could have more methods - so it’s not an exact match but the minimal set of methods a type has to define in order to be valid.
 */
def closeQuietly(closable: {def close(): Unit}): Unit = {
  try {
    closable.close()
  } catch {
    case ex: Exception => // ignore
  }
}

closeQuietly(new StringReader("example"))
closeQuietly(new MyOwnClosable)

/*
Structural Typing is that it actually has huge (negative) runtime performance implications, as it is actually implemented using reflection

 :javap in the Scala REPL
 */

/*
By using a Type Alias (described in detail in another section) with a Structural Type, we’re able to separate the type definition from the method


 I’d highly recommend type aliasing bigger structural types. And one last warning, always check if you really need to reach for structural typing, and cannot do it in some other way, considering the negative performance impact.
 */

def on(it: OpenerCloser)(fun: OpenerCloser => Unit) = {
  it.open()
  fun(it)
  it.close()
}

class MyOwnClosable {
  def close(): Unit = ()
}