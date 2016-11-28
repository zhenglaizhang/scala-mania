package net.zhenglai.accounting

import java.util.{ Calendar, Date }

import scala.util.{ Failure, Success, Try }

import net.zhenglai.accounting.AccountService.Amount

object AccountService extends AccountService {
  def today = Calendar.getInstance.getTime

  type Amount = BigDecimal
}

case class Balance(amount: Amount = 0)

case class Account(
  no: String,
  name: String,
  dateOfOpening: Date,
  balance: Balance = Balance()
)

trait AccountService {

  def debit(account: Account, amount: Amount): Try[Account] = {
    if (account.balance.amount < amount) {
      Failure(new Exception("Insufficient balance in account"))
    } else {
      Success(account.copy(balance = Balance(account.balance.amount - amount)))
    }
  }

  def credit(account: Account, amount: Amount): Try[Account] = {
    Success(account.copy(balance = Balance(account.balance.amount + amount)))
  }
}

object Main extends App {
  import AccountService._
  val a = Account("a1", "Zhenglai", today)

  for {
    b <- credit(a, 1000)
    c <- debit(b, 200)
    d <- debit(c, 300)
  } yield d
}

