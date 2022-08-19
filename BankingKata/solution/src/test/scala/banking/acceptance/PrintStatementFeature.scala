package banking.acceptance

import banking.commands
import banking.commands.{Deposit, PrintStatement, Withdraw}
import banking.domain.{AccountRepository, Clock}
import banking.usecases.{DepositUseCase, PrintStatementUseCase, WithdrawUseCase}
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

import java.util.UUID

class PrintStatementFeature extends AnyFlatSpec with Matchers with MockFactory {
  behavior of "banking.domain.Account API"

  private val printerStub = stubFunction[String, Unit]
  private val accountRepositoryStub = stub[AccountRepository]

  private val depositUseCase = new DepositUseCase(accountRepositoryStub, stub[Clock])
  private val withDrawUseCase = new WithdrawUseCase()
  private val printStatementUseCase = new PrintStatementUseCase(printerStub)

  it should "print statement containing all the transactions" in {
    val accountId = UUID.randomUUID()

    depositUseCase.invoke(Deposit(accountId, 1000d))
    depositUseCase.invoke(commands.Deposit(accountId, 2000d))
    withDrawUseCase.invoke(Withdraw(accountId, 500d))

    printStatementUseCase.invoke(PrintStatement(accountId))

    printerStub.verify("date       |   credit |    debit |  balance").once()
    printerStub.verify("19-01-2022 |          |   500.00 |  2500.00").once()
    printerStub.verify("18-01-2022 |  2000.00 |          |  3000.00").once()
    printerStub.verify("12-01-2022 |  1000.00 |          |  1000.00").once()
  }
}
