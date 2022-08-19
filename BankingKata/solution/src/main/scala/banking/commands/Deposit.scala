package banking.commands

import java.util.UUID

case class Deposit(accountId: UUID, amount: Double) {}
