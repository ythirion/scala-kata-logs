package banking.services

import banking.domain.Account

import java.util.UUID

class AccountService() {
  def deposit(customerId: UUID, amount: Double): Either[String, Account] = ???
  def withdraw(customerId: UUID, amount: Double): Either[String, Account] = ???
  def printStatement(customerId: UUID, printer: String => Unit): Unit = ???
}
