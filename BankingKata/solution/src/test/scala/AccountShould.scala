import AccountBuilder.aNewAccount
import banking.domain.{Clock, Transaction}
import org.scalamock.scalatest.MockFactory
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

import java.time.LocalDateTime

class AccountShould extends AnyFlatSpec with EitherValues with Matchers with MockFactory {
  private val clockStub: Clock = stub[Clock]
  private val emptyAccount = aNewAccount().build()

  it should "return an error for a deposit of 0" in {
    emptyAccount
      .deposit(clockStub, 0)
      .left
      .get mustBe "Invalid amount for deposit"
  }

  it should "contain banking.domain.Transaction(currentDateTime, 1000) for an empty account and a deposit of 1000" in {
    val transactionTime = LocalDateTime.of(2022, 8, 19, 13, 0)

    (clockStub.now _)
      .when()
      .returns(transactionTime)

    emptyAccount
      .deposit(clockStub, 1000)
      .right
      .value
      .transactions must contain(Transaction(transactionTime, 1000))
  }
}
