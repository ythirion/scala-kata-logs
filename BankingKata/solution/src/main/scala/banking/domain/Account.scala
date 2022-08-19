package banking.domain

import java.util.UUID

case class Account(id: UUID) {
  def deposit(amount: Double): Either[String, Account] = Right(this)
}
