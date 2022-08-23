package banking.unit

import banking.AccountBuilder.aNewAccount
import banking.commands.Deposit
import banking.domain.{AccountRepository, Clock}
import banking.usecases.DepositUseCase
import org.scalamock.scalatest.MockFactory
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

import java.util.UUID

class DepositShould extends AnyFlatSpec with MockFactory with EitherValues with Matchers {
  private val accountRepositoryStub = stub[AccountRepository]
  private val anAccountId = UUID.randomUUID()
  private val deposit = Deposit(anAccountId, 1000)
  private val invalidDeposit = Deposit(anAccountId, -1)
  private val depositUseCase: DepositUseCase =
    new DepositUseCase(accountRepositoryStub, stub[Clock])

  it should "return a failure for a non existing account" in {
    accountNotFound()
    depositUseCase.invoke(deposit).left.get mustBe "Unknown account"
  }

  it should "return a failure for an existing account and negative amount" in {
    existingAccount()
    depositUseCase.invoke(invalidDeposit).left.get mustBe "Invalid amount for deposit"
  }

  it should "store the account for an existing account" in {
    existingAccount()

    val newAccount = depositUseCase.invoke(deposit)

    newAccount.isRight mustBe true
    (accountRepositoryStub.save _)
      .verify(newAccount.right.value)
      .once()
  }

  private def existingAccount(): Unit = {
    val account = aNewAccount(deposit.accountId).build()
    (accountRepositoryStub.find _)
      .when(account.id)
      .returns(Some(account))
  }

  private def accountNotFound(): Unit =
    (accountRepositoryStub.find _)
      .when(deposit.accountId)
      .returns(None)
}
