## Add an Acceptance Test
- Add a package `acceptance` under test
- Create a test class `PrintStatementFeature`

```scala
class PrintStatementFeature extends AnyFlatSpec {
  behavior of "banking.domain.Account API"

  it should "print statement containing all the transactions" in {}
}
```

- Create the necessary objects from this acceptance test
- We choose to design 1 `Use Case` for each business behavior
  - Instead of having `all-in-one` services

```scala
  it should "print statement containing all the transactions" in {
    val accountId = UUID.randomUUID()

    depositUseCase.invoke(Deposit(accountId, 1000d))
    depositUseCase.invoke(Deposit(accountId, 2000d))
    withDrawUseCase.invoke(Withdraw(accountId, 500d))

    printStatementUseCase.invoke(PrintStatement(accountId))

    printerStub.verify("date       |   credit |    debit |  balance").once()
    printerStub.verify("19-01-2022 |          |   500.00 |  2500.00").once()
    printerStub.verify("18-01-2022 |  2000.00 |          |  3000.00").once()
    printerStub.verify("12-01-2022 |  1000.00 |          |  1000.00").once()
  }
```

- From here, we can generate the necessary objects to compile this code

![Generate Code from Acceptance Test](img/generate-code.png)

- Generate the related commands inside a `banking.commands` package
```scala
case class Deposit(accountId: UUID, amount: Double) {}
case class PrintStatement(accountId: UUID) {}
case class Withdraw(accountId: UUID, amount: Double) {}
```

- Generate the Use Cases inside a `banking.usecases` package

```scala
class DepositUseCase() {
  def invoke(deposit: Deposit) = ???
}

class PrintStatementUseCase(printer: String => Unit) {
  def invoke(statement: PrintStatement) = ???
}

class WithdrawUseCase() {
  def invoke(withdraw: Withdraw) = ???
}
``` 

- Instantiate a `stub` function for our test

```scala
class PrintStatementFeature extends AnyFlatSpec with Matchers with MockFactory {
  behavior of "banking.domain.Account API"

  private val printerStub = stubFunction[String, Unit]

  private val depositUseCase = new DepositUseCase()
  private val withDrawUseCase = new WithdrawUseCase()
  private val printStatementUseCase = new PrintStatementUseCase(printerStub)

  it should "print statement containing all the transactions" in {
    val accountId = UUID.randomUUID()

    depositUseCase.invoke(Deposit(accountId, 1000d))
    depositUseCase.invoke(commands.Deposit(accountId, 2000d))
    withDrawUseCase.invoke(Withdraw(accountId, 500d))

    printStatementUseCase.invoke(PrintStatement(accountId))

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
Go down to the Unit Level and work on a first `Use Case`

> What is the responsibility of this class?

- Fetch a database to identify if the `account` exists in the system
  - If so, delegate the business logic to the `domain` entity then `store` the new state
  - If no, return a failure

### Deposit
Let's think about test cases for the deposit:
```text
- Not existing account -> return a failure
- Existing account -> store the update account 
```

Passing Sequence:
![Deposit passing Use Case](img/deposit-passing-use-case.png)

Let's create a folder `Deposit` in our tests and create a new test class `DepositShould`

#### Non-passing test
Based on our sequence diagram, we can design the test by knowing what we will need to make it pass.
One question is remaining which return type do we want to use on `invoke`?

> Let's use an Either[String, Account]

:red_circle: Let's write the test:
```scala
class DepositShould extends AnyFlatSpec with MockFactory with EitherValues with Matchers {

    it should "return a failure for a non existing account" in {
      val deposit = Deposit(UUID.randomUUID(), 1000)
      val depositUseCase = new DepositUseCase(accountRepositoryStub)
  
      (accountRepositoryStub.find _)
        .when(deposit.accountId)
        .returns(None)
  
      depositUseCase.invoke(deposit).left.get mustBe "Unknown account"
    }
}
```

Start by generating an `Account` class in a package `banking.domain`

```scala
case class Account(id: UUID) {}
```

Then, instantiate a stub for the repository and create a trait for it.

```scala
private val accountRepositoryStub = stub[AccountRepository]

// under the domain -> check hexagonal architecture (ports and adapters)
trait AccountRepository {
  def find(accountId: UUID): Option[Account]
}
```

Adapt the `Use Case`:
- Inject the repository
- Change the `invoke` return type -> `Either[String, Account]`

```scala
class DepositUseCase(accountRepository: AccountRepository) {
  def invoke(deposit: Deposit): Either[String, Account] = ???
}
```

:green_circle: Make it green as fast as possible
```scala
def invoke(deposit: Deposit): Either[String, Account] = Left("Unknown account")
```

:large_blue_circle: Do you think any refactoring could be done ?

:red_circle: Let's write a passing test
- We need to instantiate an existing `Account`
- Simulate it can be found from the `db`
- Verify that the updatedAccount is saved through our `AccountRepository`
- `Should we use a test double for Account? -> check the behavior is called from here`

```scala
  it should "store the account for an existing account" in {
      val account: Account = Account(UUID.randomUUID())
      val deposit = Deposit(account.id, 1000)
      val depositUseCase = new DepositUseCase(accountRepositoryStub)
  
      (accountRepositoryStub.find _)
        .when(account.id)
        .returns(Some(account))
  
      val newAccount = depositUseCase.invoke(deposit)
  
      newAccount.isRight mustBe true
      (accountRepositoryStub.save _)
        .verify(newAccount.right.value)
        .once()
    }
```

:green_circle: Let's iterate on the `UseCase` to make it green
```scala
class DepositUseCase(accountRepository: AccountRepository) {
  def invoke(deposit: Deposit): Either[String, Account] = {
    accountRepository.find(deposit.accountId) match {
      case Some(account) => makeDeposit(account, deposit.amount)
      case None          => Left("Unknown account")
    }
  }

  private def makeDeposit(account: Account, amount: Double): Either[String, Account] = {
    account.deposit(amount) match {
      case Right(updatedAccount) =>
        accountRepository.save(updatedAccount)
        Right(updatedAccount)
      case Left(error) => Left(error)
    }
  }
}
```

We need to generate a `deposit` method from it as well.
We `fake its result` -> we will implement this class after it.

```scala
case class Account(id: UUID) {
  def deposit(amount: Double): Either[String, Account] = Right(this)
}
```

