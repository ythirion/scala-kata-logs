package banking.usecases

import banking.commands.PrintStatement

class PrintStatementUseCase(printer: String => Unit) {
  def invoke(statement: PrintStatement) = ???
}
