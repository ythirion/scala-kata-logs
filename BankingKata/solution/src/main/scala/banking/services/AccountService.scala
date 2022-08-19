package banking.services

import banking.domain.Account

import java.util.UUID

class AccountService() {
  def deposit(accountId: UUID, amount: Double): Either[String, Account] = ???
  def withdraw(accountId: UUID, amount: Double): Either[String, Account] = ???
  def printStatement(accountId: UUID, printer: String => Unit): Unit = ???
}
