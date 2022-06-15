package examples

import examples.models.{Command, Order}

class OrderHandler extends CommandHandler {
  override def handle[TCommand <: Command](command: TCommand): Int = 42
}