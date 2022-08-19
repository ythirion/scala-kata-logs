package banking.services

import banking.domain.Account

class AccountService() {
  def deposit(amount: Double): Either[String, Account] = ???
  def withdraw(amount: Double): Either[String, Account] = ???
  def printStatement(printer: String => Unit): Unit = ???
}
