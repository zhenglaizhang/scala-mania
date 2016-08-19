
/*
Scala `object`s are implemented via classes (obviously - as it’s the basic building block on the JVM)
 */

object ExampleObj

def takeAnObject(obj: ExampleObj.type) = {}

takeAnObject(ExampleObj)