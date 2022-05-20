# Bank account kata

## Objectives

Think of your personal bank account experience
When in doubt, go for the simplest solution

## Problem Description
Create a simple bank application with the following features :
- Deposit into Account
- Withdraw from an account
- Print a bank statement to the console

## Starting points and Constraints
1. Start with a class following this structure:
````scala
class Account {
  def deposit(amount: Int): Either[String, Account]
  def withdraw(amount: Int): Either[String, Account] 
  def printStatement(printer: String => Unit): Unit
}
````
- Account must stay immutable

2. You are not allowed to add any other `public` function
3. Use `String` and `Int` for dates and amounts (keep it simple)

## BDD

Starting from an acceptance test: (you can use cucumber to implement it)

```gherkin
Scenario: Printing statement after deposits and withdrawal
  Given a client makes a deposit of 1000 on 10-01-2012
  And a deposit of 2000 on 13-01-2012
  And a withdrawal of 500 on 14-01-2012
  When she prints her bank statement
  Then she would see
  """
  date       ||   credit ||    debit ||  balance
  14-01-2012 ||          ||   500.00 ||  2500.00
  13-01-2012 ||  2000.00 ||          ||  3000.00
  10-01-2012 ||  1000.00 ||          ||  1000.00
  """
```