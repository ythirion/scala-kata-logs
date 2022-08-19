package banking

import banking.domain.{Account, Transaction}

import java.util.UUID

class AccountBuilder(private val accountId: UUID) {
  private var transactions: List[Transaction] = Nil

  def containing(transactions: TransactionBuilder*): AccountBuilder = {
    this.transactions = transactions
      .map(_.build())
      .toList

    this
  }

  def build(): Account = Account(accountId, transactions)
}

object AccountBuilder {
  def aNewAccount(accountId: UUID = UUID.randomUUID()): AccountBuilder =
    new AccountBuilder(accountId)
}
