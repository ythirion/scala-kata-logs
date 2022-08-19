package Deposit

import banking.commands.Deposit
import banking.domain.{Account, AccountRepository}
import banking.usecases.DepositUseCase
import org.scalamock.scalatest.MockFactory
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

import java.util.UUID

class DepositShould extends AnyFlatSpec with MockFactory with EitherValues with Matchers {
  private val accountRepositoryStub = stub[AccountRepository]

  it should "return a failure for a non existing account" in {
    val deposit = Deposit(UUID.randomUUID(), 1000)
    val depositUseCase = new DepositUseCase(accountRepositoryStub)

    (accountRepositoryStub.find _)
      .when(deposit.accountId)
      .returns(None)

    depositUseCase.invoke(deposit).left.get mustBe "Unknown account"
  }

  it should "store the account for an existing account" in {
    val account: Account = Account(UUID.randomUUID())
    val deposit = Deposit(account.id, 1000)
    val depositUseCase = new DepositUseCase(accountRepositoryStub)

    (accountRepositoryStub.find _)
      .when(account.id)
      .returns(Some(account))

    val newAccount = depositUseCase.invoke(deposit)

    newAccount.isRight mustBe true
    (accountRepositoryStub.save _)
      .verify(newAccount.right.value)
      .once()
  }
}
