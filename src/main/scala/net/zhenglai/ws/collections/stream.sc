/*
A Scala Stream is like a List, except that its elements are computed lazily, in a manner similar to how a view creates a lazy version of a collection. Because Stream elements are computed lazily, a Stream can be long ... infinitely long. Like a view, only the elements that are accessed are computed. Other than this behavior, a Stream behaves similar to a List.

The ? symbol is the way a lazy collection shows that the end of the collection hasn’t been evaluated yet.


A Stream is a lazy evaluated list. This means that elements in a Stream gets only evaluated when they are needed. Therefore Streams can be infinite while strict collections cannot. A Stream can also be seen as an immutable Iterator.
 */
val stream = 1 #:: 2 #:: 3 #:: Stream.empty

val s2 = (1 to 10000000).toStream
s2.head
s2.tail
s2.take(3)
s2.filter(_ < 100)

/*
transformer methods are computed lazily, so when transformers are called, you see the familiar ? character that indicates the end of the stream hasn’t been evaluated yet

Transformer methods are collection methods that convert a given input collection to a new output collection, based on an algorithm you provide to transform the data. This includes methods like map, filter, and reverse. When using these methods, you’re transforming the input collection to a new output collection. Methods like max, size, and sum don’t fit that definition
 */
s2.map {
  _ * 2
}.filter(_ < 100)

/*
be careful with methods that aren’t transformers. Calls to the following strict methods are evaluated immediately and can easily cause java.lang.OutOfMemoryError errors:

  stream.max
  stream.size
  stream.sum
 */

// we call Stream.cons(...) we call the apply method. Written out in full it would be Stream.cons.apply(...)
def from(start: Int): Stream[Int] = {
  println(s"===> start => $start")
  Stream.cons(start, from(start + 1))
}

def from2(start: Int): Stream[Int] = start #:: from(start + 1)

val nn = from(0)
//  how many times the from function will be called? Right 9 times,
nn.take(10).mkString(",")

/*
A Stream can be used like every other collection because it is of type LinearSeq. Operations like filter and map are lazy evalutated. They simply return another Stream and perform the operation when required. I think we can say that all operations that return another Stream can be considered as lazy operations (like filter, map). Of course, operations like foldLeft, foldRight, find and exists are not lazy.
 */

// Streams can be used in pattern matching
nn match {
  case 0 #:: 1 #:: _ => println("matched")
}