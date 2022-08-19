package banking.domain

import java.util.UUID

trait AccountRepository {
  def find(accountId: UUID): Option[Account]
  def save(account: Account): Unit
}
