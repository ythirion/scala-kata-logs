# Bank account - Outside-In kata

## Objectives
Think of your personal bank account experience
When in doubt, go for the simplest solution

## Problem Description
Create a simple bank system with the following features :
- Deposit into Account
- Withdraw from an account
- Print a bank statement to the console

### Starting points and Constraints
1. Your system should be able to support the operations specified in the below scenario
2. Use `String` for errors, `UUID` for account ids, `Double` for amounts (keep it simple)
3. Use `Use Case` as first class citizens
    - They take a `Command` as input and return a `Result`
    - They contain only 1 public method `invoke`

### Scenario
Here is an acceptance scenario as described by one of our Domain Expert

```gherkin
Scenario: Printing statement after transactions
  Given a client makes a deposit of 1000 on 12-08-2022
  And a deposit of 2000 on 18-08-2012
  And a withdrawal of 500 on 19-01-2012
  When he/she prints her bank statement
  Then he/she would see
  """
  date       |   credit |    debit |  balance
  19-01-2022 |          |   500.00 |  2500.00
  18-01-2022 |  2000.00 |          |  3000.00
  12-01-2022 |  1000.00 |          |  1000.00
  """
```

## Outside-In TDD
![TDD Outside-In](img/outside-in.png)

![TDD double loop](img/tdd-double-loop.png)

## Clean Architecture
[![Clean architecture schema from cleancoder](https://blog.cleancoder.com/uncle-bob/images/2012-08-13-the-clean-architecture/CleanArchitecture.jpg)](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)

A step-by-step solution guide is available [here](solution/step-by-step.md)