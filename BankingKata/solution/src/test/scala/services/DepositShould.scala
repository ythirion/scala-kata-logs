package services

import org.scalamock.scalatest.MockFactory
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

class DepositShould extends AnyFlatSpec with MockFactory with EitherValues with Matchers {
  /*private val accountRepositoryStub = stub[AccountRepository]
  private val depositUseCase = new DepositUseCase(accountRepositoryStub)

  it should "return a failure for a non existing account" in {
    val account: Account = aNewAccount().build()

    (accountRepositoryStub.find _)
      .when(account.id)
      .returns(None)

    depositUseCase.invoke(account.id, 100).left.get mustBe "Unknown account"
  }

  it should "store the account for an existing account" in {
    val account: Account = aNewAccount().build()

    (accountRepositoryStub.find _)
      .when(account.id)
      .returns(Some(account))

    val newAccount = depositUseCase.invoke(account.id, 100)

    newAccount.isRight mustBe true
    (accountRepositoryStub.save _)
      .verify(newAccount.right.value)
      .once()
  }*/
}
