package banking.unit

import banking.AccountBuilder
import banking.AccountBuilder.aNewAccount
import banking.commands.Deposit
import banking.domain.{Account, AccountRepository, Clock}
import banking.usecases.DepositUseCase
import org.scalamock.scalatest.MockFactory
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

import java.util.UUID

class DepositShould extends AnyFlatSpec with MockFactory with EitherValues with Matchers {
  private val accountRepositoryStub = stub[AccountRepository]
  private val deposit = Deposit(UUID.randomUUID(), 1000)
  private val depositUseCase: DepositUseCase =
    new DepositUseCase(accountRepositoryStub, stub[Clock])

  it should "return a failure for a non existing account" in {
    accountNotFound()
    depositUseCase.invoke(deposit).left.get mustBe "Unknown account"
  }

  it should "store the account for an existing account" in {
    existingAccount(aNewAccount(deposit.accountId))

    val newAccount = depositUseCase.invoke(deposit)

    newAccount.isRight mustBe true
    (accountRepositoryStub.save _)
      .verify(newAccount.right.value)
      .once()
  }

  private def existingAccount(accountBuilder: AccountBuilder): Unit = {
    val account = accountBuilder.build()
    (accountRepositoryStub.find _)
      .when(account.id)
      .returns(Some(account))
  }

  private def accountNotFound(): Unit =
    (accountRepositoryStub.find _)
      .when(deposit.accountId)
      .returns(None)
}
