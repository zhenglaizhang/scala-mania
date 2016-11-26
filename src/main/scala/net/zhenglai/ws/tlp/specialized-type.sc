
/*
Type specialization is actualy more of an performance technique than plain "type system stuff"
 */

val pi = Parcel(1) // will use `int` specialized methods

/*
@specialized annotation to the type parameter A, thus instructing the compiler to generate all specialized variants of this class - that is: ByteParcel, IntParcel, LongParcel, FloatParcel, DoubleParcel, BooleanParcel, CharParcel, ShortParcel, CharParcel and even VoidParcel (not actual names of the implementors, but you get the idea). Applying the "right" version is also taken up by the compiler, so we can write our code without caring if a class is specialized or not, and the compiler will do it’s best to use the specialized version (if available):
 */
val pl = Parcel(1L) // will use `long` specialized methods
val pb = Parcel(false) // will use `boolean` specialized methods
val po = Parcel("pi")

/*
Well, as A can be anything, it will be represented as an Java object, even if we’d only ever put Int into boxes. So the above class would cause us to box and unbox primitive values, because the container is working on objects
 */
case class Parcel[@specialized A](value: A)

// will use `Object` methods

//val i: Int = Int.unbox(Parcel.apply(Int.box(12)))

/*
Sadly, it comes at a high price: the generated code quickly becomes huge when used with multiple parameters like this
 */

// the second style of applying specialization
// the code would generate 8 * 8 = 64 (sic!) implementations
//  In fact the number of generated classes is around 2 * 10^(nr_of_type_specializations), which easily reaches thousands of classes for already 3 type parameters!
class Thing[A, B](@specialized a: A, @specialized b: B)

// There are ways to limit this exponential explosion, for example by limiting the specialization target types.
case class Parcel1[@specialized(Int, Long) A](value: A)

/*
// Parcel, specialized for Int and Long
public class Parcel extends java.lang.Object implements scala.Product,scala.Serializable{
    public java.lang.Object value(); // generic version, "catch all"
    public int value$mcI$sp();       // int specialized version
    public long value$mcJ$sp();}     // long specialized version

    public boolean specInstance$();  // method to check if we're a specialized class impl.
}
 */

/*
HotSpot an boolean is represented as int, so it takes 4 bytes of space. It’s cousin java.lang.Boolean on the other hand has 8 bytes of object header, as does any Java object, then it stores the boolean inside (another 4 bytes), and due to the Java Object Layout alignment rules, the space taked up by this object will be aligned to 16 bytes (8 for object header, 4 for the value, 4 bytes of padding). That’s yet another reason why we want to avoid boxing so badly.
 */ 