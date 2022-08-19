package banking.domain

import java.time.LocalDateTime

case class Transaction(at: LocalDateTime, amount: Double) {}
