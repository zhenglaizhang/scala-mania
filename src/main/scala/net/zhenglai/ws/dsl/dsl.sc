
/*
DSL
  modern, cool, common
  care about werewolves



Programming
  problem-level abstractions -(mapping)-> Implementation-level abstractions


Two Complexities

  * Essential complexity
  * Accidential Complexity

Two Complexities • Essential Complexity - i.e. complexity that is intrinsic to the problem you are trying to solve. • Accidental Complexity - i.e. complexity that is caused by the approach you have chosen to solve that

18. Hypothesis: DSLs can help to separate essential complexity from accidental complexity

Domain-Speciﬁc Language A computer programming language of limited expressiveness focused on a particular domain.

A language offering expressive power focused on a particular problem domain, such as a speciﬁc class of applications or application aspect.



External DSLs involve parsing of syntax foreign to the native language - hence the ease of developing external DSLs depends a lot on parsing and parse tree manipulation capabilities available in existing libraries and frameworks.



The DSL looks meaningful enough for the business analysts as well, since it uses the domain language and does not contain much of the accidental complexities that we get in languages like Java. The language provides easy options to plug in default strategies


Here are some of the niceties in the syntax of Scala that makes it a DSL friendly language ..

  Implicits
  Higher order functions
  Optional dots, semi-colons and parentheses
  Operators like methods
  Currying
 */

case class Stock(name: String)
case class Bond(name: String)

class PimpedInt(qty: Int) {
  def sharesOf(name: String) = (qty, Stock(name))

  def bondsOf(name: String) = (qty, Bond(name))

}

/*
sell(200 bondsOf "Sun") as valid Scala code

And the best part is that the entire extension of the class Int is lexically scoped and will only be available within the scope of the implicit definition function pimpInt.
 */
implicit def pimpInt(i: Int) = new PimpedInt(i)

object TradeDSL {

  abstract class Instrument(name: String) { def stype: String }
  case class Stock(name: String) extends Instrument(name) {
    override val stype = "equity"
  }
  case class Bond(name: String) extends Instrument(name) {
    override val stype = "bond"
  }

  abstract class TransactionType { def value: String }
  case class buyT() extends TransactionType {
    override val value = "bought"
  }
  case class sellT() extends TransactionType {
    override val value = "sold"
  }

  class PimpedInt(qty: Int) {
    def sharesOf(name: String) = {
      (qty, Stock(name))
    }

    def bondsOf(name: String) = {
      (qty, Bond(name))
    }
  }

  implicit def pimpInt(i: Int) = new PimpedInt(i)

  class Order {
    var price = 0
    var ins: Instrument = null
    var qty = 0;
    var totalValue = 0
    var trn: TransactionType = null
    var account: String = null

    def to(i: Tuple3[Instrument, Int, TransactionType]) = {
      ins = i._1
      qty = i._2
      trn = i._3
      this
    }
    def maxUnitPrice(p: Int) = { price = p; this }

    def using(pricing: (Int, Int) => Int) = {
      totalValue = pricing(qty, price)
      this
    }

    def forAccount(a: String)(implicit pricing: (Int, Int) => Int) = {
      account = a
      totalValue = pricing(qty, price)
      this
    }
  }

  def buy(qi: Tuple2[Int, Instrument]) = (qi._2, qi._1, buyT())
  def sell(qi: Tuple2[Int, Instrument]) = (qi._2, qi._1, sellT())

  def main(args: Array[String]) = {

    def premiumPricing(qty: Int, price: Int) = qty match {
      case q if q > 100 => q * price - 100
      case _            => qty * price
    }

    def defaultPricing(qty: Int, price: Int): Int = qty * price

    val orders = List[Order](

      new Order to buy(100 sharesOf "IBM")
        maxUnitPrice 300
        using premiumPricing,

      new Order to buy(200 sharesOf "CISCO")
        maxUnitPrice 300
        using premiumPricing,

      new Order to buy(200 sharesOf "GOOGLE")
        maxUnitPrice 300
        using defaultPricing,

      new Order to sell(200 bondsOf "Sun")
        maxUnitPrice 300
        using {
          (qty, unit) => qty * unit - 500
        }
    )
    println((0 /: orders)(_ + _.totalValue))
  }
}