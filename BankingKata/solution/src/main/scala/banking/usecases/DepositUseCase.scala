package banking.usecases

import banking.commands.Deposit
import banking.domain.{Account, AccountRepository, Clock}

class DepositUseCase(accountRepository: AccountRepository, clock: Clock) {
  def invoke(deposit: Deposit): Either[String, Account] = {
    accountRepository.find(deposit.accountId) match {
      case Some(account) => depositSafely(deposit, account)
      case None          => Left("Unknown account")
    }
  }

  private def depositSafely(deposit: Deposit, account: Account): Either[String, Account] =
    account.deposit(clock, deposit.amount) match {
      case Right(updatedAccount) =>
        accountRepository.save(updatedAccount)
        Right(updatedAccount)
      case Left(error) => Left(error)
    }
}
