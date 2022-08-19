package banking

import banking.DataForTests.anotherDateTime
import banking.domain.Transaction

import java.time.LocalDateTime

class TransactionBuilder {
  private var at = anotherDateTime
  private var amount = 1000d

  def madeAt(at: LocalDateTime): TransactionBuilder = {
    this.at = at
    this
  }

  def of(amount: Double): TransactionBuilder = {
    this.amount = amount
    this
  }

  def build(): Transaction = Transaction(at, amount)
}

object TransactionBuilder {
  def aNewTransaction(): TransactionBuilder = new TransactionBuilder()
}
