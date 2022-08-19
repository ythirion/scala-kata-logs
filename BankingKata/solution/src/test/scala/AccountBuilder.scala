import banking.domain.Account

import java.util.UUID

class AccountBuilder(private val accountId: UUID) {
  def build(): Account = Account(accountId)
}

object AccountBuilder {
  def aNewAccount(accountId: UUID = UUID.randomUUID()): AccountBuilder =
    new AccountBuilder(accountId)
}
