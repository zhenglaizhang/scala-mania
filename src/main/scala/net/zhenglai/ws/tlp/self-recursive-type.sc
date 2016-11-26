

/*
Self-recursive Types are referred to as F-Bounded Types in most literature

the subtype constraint itself is parametrized by one of the binders occurring on the left-hand side

recursive type
 */

object NotGood {

  val apple = new Apple
  val orange = new Orange

  /*
  the trait Fruit has no clue about the types extending it, so it’s not possible to restrict the compareTo signature to only allow "the same subclass as `this`" in the parameter
   */
  trait Fruit {
    final def compareTo(other: Fruit) = true
  }

  class Apple extends Fruit

  class Orange extends Fruit

  apple compareTo orange //  compiles, but we want to make this NOT compile!
}

// I take some T, that T must be a Fruit[T]
trait Fruit[T <: Fruit[T]] {

  // apples can only be compared to apples
  final def compareTo(other: Fruit[T]) = true
}

class Apple extends Fruit[Apple]
class Orange extends Fruit[Orange]

val apple = new Apple
val orange = new Orange

//orange compareTo apple

object `りんご` extends Apple
object Jabłuszko extends Apple

`りんご` compareTo Jabłuszko
// true

/*
	You could get the same type-safety using more fancy tricks, like path dependent types or implicit parameters and type classes. But the simplest thing that does-the-job here would be this.
 */

/*
F-Bounded Type Polymorphism
 */

trait Account[T <: Account[T]] {
  def addFunds(amount: BigDecimal): T
}

case class BrokerageAccount(total: BigDecimal) extends Account[BrokerageAccount] {
  def addFunds(amount: BigDecimal) = new BrokerageAccount(total + amount)
}
case class SavingsAccount(total: BigDecimal) extends Account[SavingsAccount] {
  def addFunds(amount: BigDecimal) = new SavingsAccount(total + amount)
}

/*
This sort of self-referential type constraint is known formally as F-bounded type polymorphism and is usually attempted when someone is
trying to solve a common problem of abstraction in object-oriented languages; how to define a polymorphic
function that, though defined in terms of a supertype, will when passed a value of some subtype will always
return a value of the same subtype as its argument.
 */

object Account {
  val feePercentage = BigDecimal("0.03")
  val feeThreshold = BigDecimal("10000.00")

  /*
   the type bound is enforced via polymorphism at the call site. You'll notice that the
type ascribed to the "account" argument is T, and not Account[T] - the bound on T gives us all the constraints
that we want.
   */
  def deposit[T <: Account[T]](amount: BigDecimal, account: T): T = {
    if (amount < feeThreshold) account.addFunds(amount - (amount * feePercentage))
    else account.addFunds(amount)
  }

  /*
 the type of individual members of the list are existentially bounded, rather than the list being existentially bounded as a whole. This is important, because it means that the type of elements may vary, rather than something like "List[T] forSome { type T <: Account[T] }"
which states that the values of the list are of some consistent subtype of T.

The existential types clutter up our codebase and sometimes give the type inferencer headaches, but it's not intractable. The ability to state these existential type bounds does
   */
  def debitAll(amount: BigDecimal, accounts: List[T forSome { type T <: Account[T] }]): List[T forSome { type T <: Account[T] }] = {
    accounts map { _.addFunds(-amount) }
  }

  /*
this says that for debitAll2, all the members of the list must be of the *same* subtype of Account. This becomes apparent when we actually try to use the method with a list where the subtype varies.
   */
  def debitAll2[T <: Account[T]](amount: BigDecimal, accounts: List[T]): List[T] = {
    accounts map { _.addFunds(-amount) }
  }

}

Account.deposit(BigDecimal("10000000"), BrokerageAccount(BigDecimal("999")))
Account.deposit(BigDecimal("1000"), SavingsAccount(BigDecimal("999")))

/*
The most subtle point about F-bounded types that is important to grasp is that the type bound is *not*
as tight as one would ideally want it to be; instead of stating that a subtype must be eventually
parameterized by itself, it simply states that a subtype must be parameterized by some (potentially
other) subtype.
 */

case class MaligantAccount extends Account[SavingsAccount] {
  def addFunds(amount: BigDecimal) = new SavingsAccount(-amount)
}
/*
This will compile without error, and presents a bit of a pitfall. Fortunately, the type bounds that we
were required to declare at the use sites will prevent many of the failure scenarios that we might be
concerned about:
 */
