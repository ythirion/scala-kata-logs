## Add an Acceptance Test
- Add a package `acceptance` under test
- Create a test class `PrintStatementFeature`

```scala
class PrintStatementFeature extends AnyFlatSpec {
  behavior of "Account API"

  it should "print statement containing all the transactions" in {}
}
```

- Create the necessary objects from this acceptance test

```scala
  it should "print statement containing all the transactions" in {
    val customer = UUID.randomUUID()
    
    accountService.deposit(customer, 1000d)
    accountService.deposit(customer, 2000d)
    accountService.withdraw(customer, 500d)

    accountService.printStatement(customer, printerStub)

    printerStub.verify("date       |   credit |    debit |  balance").once()
    printerStub.verify("19-01-2022 |          |   500.00 |  2500.00").once()
    printerStub.verify("18-01-2022 |  2000.00 |          |  3000.00").once()
    printerStub.verify("12-01-2022 |  1000.00 |          |  1000.00").once()
  }
```

- Create an `AccountService` in the `banking.services` package

```scala
class AccountService() {
  def deposit(customerId: UUID, amount: Double): Either[String, Account] = ???
  def withdraw(customerId: UUID, amount: Double): Either[String, Account] = ???
  def printStatement(customerId: UUID, printer: String => Unit): Unit = ???
}
```

- Create an `Account` case class in the `banking.domain` package

```scala
case class Account() {}
```

- Instantiate a `stub` function for our test

```scala
class PrintStatementFeature extends AnyFlatSpec with Matchers with MockFactory {
  behavior of "Account API"

  private val accountService = new AccountService()
  private val printerStub = stubFunction[String, Unit]

  it should "print statement containing all the transactions" in {
    val customer = UUID.randomUUID()
    
    accountService.deposit(customer, 1000d)
    accountService.deposit(customer, 2000d)
    accountService.withdraw(customer, 500d)

    accountService.printStatement(customer, printerStub)

    printerStub.verify("date       |   credit |    debit |  balance").once()
    printerStub.verify("19-01-2022 |          |   500.00 |  2500.00").once()
    printerStub.verify("18-01-2022 |  2000.00 |          |  3000.00").once()
    printerStub.verify("12-01-2022 |  1000.00 |          |  1000.00").once()
  }
}
```

- Let's improve our test ta take into account the order in which the printer should be called
    - We can use the `inSequence` method from `scalamock` for that
```scala
inSequence {
  printerStub.verify("date       |   credit |    debit |  balance").once()
  printerStub.verify("19-01-2022 |          |   500.00 |  2500.00").once()
  printerStub.verify("18-01-2022 |  2000.00 |          |  3000.00").once()
  printerStub.verify("12-01-2022 |  1000.00 |          |  1000.00").once()
}
```

Congrats, you have a failing acceptance test that we will use as an `implementation driver`

![Failing Acceptance Test](img/failing-acceptance-test.png)

## TDD Loops
Go down to the Unit Level and work on the `AccountService`
