import scala.collection.immutable.BitSet

/*
In Scala BitSet comes in two versions: scala.collection.immutable.BitSet and scala.collection.mutable.BitSet. They are almost identical but the mutable version changes the bits in place. This is the same behaviour as in the Java java.util.BitSet class and is slightly faster than the immutable one (no copying required).

I prefer the immutable one when performance is not an issue (make sure to profile to see if you really need the mutable one) because immutable data structures are much better for concurrency.
 */
val primeBits = BitSet(2, 3, 5, 7, 11)
val evenBits = BitSet(0, 2, 4, 6, 8, 10)

val evenSet = Set(0, 2, 4, 6, 8, 10)

primeBits & evenBits

primeBits & evenSet

// add single integers to the lsit

primeBits + 13 + 17

primeBits ++ Seq(19, 23)

primeBits + 11 + 2

// remove 11
primeBits - 11

primeBits -- Seq(2, 7)
primeBits -- evenBits

primeBits &~ evenBits

/*
In Scala the BitSet classes are part of the Scala collection framework and give all the great many methods available for other collections
 */
primeBits filter (_ % 2 == 0)

primeBits filterNot (_ % 2 == 0)

primeBits map (_ * 3)