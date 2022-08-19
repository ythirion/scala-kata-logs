package banking.usecases

import banking.commands.Deposit
import banking.domain.{Account, AccountRepository}

class DepositUseCase(accountRepository: AccountRepository) {
  def invoke(deposit: Deposit): Either[String, Account] = {
    accountRepository.find(deposit.accountId) match {
      case Some(account) => makeDeposit(account, deposit.amount)
      case None          => Left("Unknown account")
    }
  }

  private def makeDeposit(account: Account, amount: Double): Either[String, Account] = {
    account.deposit(amount) match {
      case Right(updatedAccount) =>
        accountRepository.save(updatedAccount)
        Right(updatedAccount)
      case Left(error) => Left(error)
    }
  }
}
