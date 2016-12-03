def sqrt(x: Long) = x * x

val l: Long = 10
val i = 10


val lng = 10L

sqrt(lng)


sqrt(i) // implicit widen conversion,

/*
Are these widening good?

It's fair point.
If we really want to enforces the constraints in the type system, we need to make new types like meter or length, and get back some type safety, and use some specific name or tags to mean something specific.
 */

