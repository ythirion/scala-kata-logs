package banking.commands

import java.util.UUID

case class Withdraw(accountId: UUID, amount: Double) {}
