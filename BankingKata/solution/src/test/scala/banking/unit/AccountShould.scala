package banking.unit

import banking.AccountBuilder.aNewAccount
import banking.DataForTests.aLocalDateTime
import banking.TransactionBuilder._
import banking.domain.{Clock, Transaction}
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{BeforeAndAfterEach, EitherValues, OneInstancePerTest}

import java.time.LocalDateTime

class AccountShould
    extends AnyFlatSpec
    with EitherValues
    with Matchers
    with MockFactory
    with OneInstancePerTest
    with BeforeAndAfterEach {

  private val transactionTime: LocalDateTime = aLocalDateTime

  private val clockStub: Clock = stub[Clock]
  (clockStub.now _).when().returns(transactionTime)

  private val emptyAccount = aNewAccount().build()

  it should "return an error for a deposit of 0" in {
    emptyAccount
      .deposit(clockStub, 0)
      .left
      .get mustBe "Invalid amount for deposit"
  }

  it should "contain Transaction(transactionTime, 1000) for an empty account and a deposit of 1000" in {
    emptyAccount
      .deposit(clockStub, 1000)
      .right
      .value
      .transactions must contain(Transaction(transactionTime, 1000))
  }

  it should "contain Transaction(transactionTime, 1000) for an account containing already a Transaction(09/10/1987, -200) and a deposit of 1000" in {
    val account = aNewAccount()
      .containing(
        aNewTransaction()
          .of(-200)
      )
      .build()

    account
      .deposit(clockStub, 1000)
      .right
      .value
      .transactions mustBe List(Transaction(transactionTime, 1000), account.transactions.head)
  }
}
