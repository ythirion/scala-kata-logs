package banking.unit

import banking.AccountBuilder
import banking.DataForTests._
import banking.TransactionBuilder.aNewTransaction
import banking.commands.Deposit
import banking.domain.{Account, AccountRepository, Clock, Transaction}
import banking.usecases.DepositUseCase
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{EitherValues, OneInstancePerTest}

import java.time.LocalDateTime
import java.util.UUID

class DepositShould
    extends AnyFlatSpec
    with MockFactory
    with EitherValues
    with Matchers
    with OneInstancePerTest {
  private val anAccountId = UUID.randomUUID()
  private val transactionTime: LocalDateTime = aLocalDateTime
  private val depositOf1000 = Deposit(anAccountId, 1000)

  private val accountRepositoryStub = stub[AccountRepository]
  private val clockStub: Clock = stub[Clock]
  (clockStub.now _).when().returns(transactionTime)

  private val depositUseCase = new DepositUseCase(accountRepositoryStub, clockStub)

  it should "return a failure for a non existing account" in {
    notExistingAccount()
    depositUseCase.invoke(depositOf1000).left.get mustBe "Unknown account"
  }

  it should "return a failure for an existing account and a deposit of 0" in {
    val invalidDeposit = Deposit(anAccountId, 0)
    existingAccount()

    depositUseCase.invoke(invalidDeposit).left.get mustBe "Invalid amount for deposit"
  }

  it should "store the updated account containing a Transaction(transactionTime, 1000) for an existing account and a deposit of 1000" in {
    existingAccount()
    val newAccount = depositUseCase.invoke(depositOf1000)

    assertAccountHasBeenCorrectlyUpdated(newAccount, List(Transaction(transactionTime, 1000)))
  }

  it should "store the updated account containing a Transaction(transactionTime, 1000) for an existing account containing already a Transaction(09/10/1987, -200) and a deposit of 1000" in {
    val anExistingTransaction = aNewTransaction()
      .madeAt(anotherDateTime)
      .of(-200)
      .build()

    existingAccount(anExistingTransaction)

    val newAccount = depositUseCase.invoke(depositOf1000)

    assertAccountHasBeenCorrectlyUpdated(
      newAccount,
      List(
        Transaction(transactionTime, 1000),
        anExistingTransaction
      )
    )
  }

  private def notExistingAccount(): Unit =
    (accountRepositoryStub.find _)
      .when(anAccountId)
      .returns(None)

  private def existingAccount(transactions: Transaction*): Unit =
    (accountRepositoryStub.find _)
      .when(anAccountId)
      .returns(
        Some(
          AccountBuilder
            .aNewAccount(anAccountId)
            .containing(transactions.toList)
            .build()
        )
      )

  private def assertAccountHasBeenCorrectlyUpdated(
      newAccount: Either[String, Account],
      expectedTransactions: List[Transaction]
  ): Unit = {
    newAccount.right.value.transactions mustBe expectedTransactions
    (accountRepositoryStub.save _)
      .verify(newAccount.right.value)
      .once()
  }
}
