package acceptance

import banking.services.AccountService
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

import java.util.UUID

class PrintStatementFeature extends AnyFlatSpec with Matchers with MockFactory {
  behavior of "Account API"

  private val accountService = new AccountService()
  private val printerStub = stubFunction[String, Unit]

  it should "print statement containing all the transactions" in {
    val customer = UUID.randomUUID()

    accountService.deposit(customer, 1000d)
    accountService.deposit(customer, 2000d)
    accountService.withdraw(customer, 500d)

    accountService.printStatement(customer, printerStub)

    inSequence {
      printerStub.verify("date       |   credit |    debit |  balance").once()
      printerStub.verify("19-01-2022 |          |   500.00 |  2500.00").once()
      printerStub.verify("18-01-2022 |  2000.00 |          |  3000.00").once()
      printerStub.verify("12-01-2022 |  1000.00 |          |  1000.00").once()
    }
  }
}
