package banking

import banking.domain.{Account, Transaction}

import java.util.UUID

class AccountBuilder(private val accountId: UUID) {
  private var transactions: List[Transaction] = Nil

  def containing(transactions: List[Transaction]): AccountBuilder = {
    this.transactions = transactions
    this
  }

  def build(): Account = Account(accountId, transactions)
}

object AccountBuilder {
  def aNewAccount(accountId: UUID = UUID.randomUUID()): AccountBuilder =
    new AccountBuilder(accountId)
}
