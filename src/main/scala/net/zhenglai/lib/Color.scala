package net.zhenglai.lib

/*
 our new type Color consists of three values Red, Green and Blue. Those are mapped to singleton objects (serving as the underlying value constructors),since every value is a unique instance of the type.
 Color is called 'sum type', since all values of that type are clearly expressed by the sum of all value constructors.


 */
sealed trait Color

case object Red extends Color

case object Green extends Color

case object Blue extends Color

//sealed abstract class RGBColor
/*
called a product type. In that case, we can’t sum up every single value of the given type, simply by enumerating value constructors (while each value constructor’s representing a single value). This time, all values of that type are expressed as the ‘product’  of all different combinations for those three intensity fields within the parameter list of our value constructor! So the constructors parameters can be seen as the single factors of that product.

Of course there are also some hybrid types possible. There, we speak of algebraic datatypes which feature more than one value constructor (sum), while some (or all) of those available value constructors may offer some fields (constructor parameters) for some other datatypes to wrap (product). In fact, the vast majority of algebraic datatypes are expressed as hybrid types and that’s why algebraic datatypes are often described as ‘sum of products types’ (hence algebraic). Those hybrid types will be the main topic for the next episode! So stay tuned …
 */
case class RGBColor(red: Int, green: Int, blue: Int) //extends RGBColor