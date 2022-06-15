package examples

import examples.models.Command

trait CommandHandler {
  def handle[TCommand <: Command](command: TCommand): Int
}