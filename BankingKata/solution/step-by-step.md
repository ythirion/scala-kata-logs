## Add an Acceptance Test
- Add a package `acceptance` under test
- Create a test class `PrintStatementFeature`

```scala
class PrintStatementFeature extends AnyFlatSpec {
  behavior of "Account API"

  it should "print statement containing all the transactions" in {}
}
```

:red_circle: let's create our acceptance test based on the business scenario

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
- We choose to design 1 `Use Case` for each business behavior
  - Instead of having `all-in-one` services

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
  behavior of "Account API"

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

- Let's improve our test to take into account the order in which the printer should be called
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
Not existing account -> return a failure
Existing account -> store the updated account 
```

Passing Sequence:
![Deposit passing Use Case](img/deposit-passing-use-case.png)

Let's create a new test class `unit.DepositShould`

#### Non-passing test
Based on our sequence diagram, we can design the test by knowing what we will need to make it pass.
One question is remaining which return type do we want to use on `invoke`?

> Let's use an Either[String, Account]

:red_circle: Let's write the test:
```scala
class unit.DepositShould extends AnyFlatSpec with MockFactory with EitherValues with Matchers {

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

#### Passing test
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

Where are we?
```text
✅ Not existing account -> return a failure
✅ Existing account -> store the updated account 
```

:large_blue_circle: We have some potential improvement in the tests

```scala
class DepositShould extends AnyFlatSpec with MockFactory with EitherValues with Matchers {
  private val accountRepositoryStub = stub[AccountRepository]
  // Declare the same command for both tests
  // Instantiate the UseCase here
  
  it should "return a failure for a non existing account" in {
    val deposit = Deposit(UUID.randomUUID(), 1000)
    val depositUseCase = new DepositUseCase(accountRepositoryStub)

    (accountRepositoryStub.find _)
      .when(deposit.accountId)
      .returns(None)

    depositUseCase.invoke(deposit).left.get mustBe "Unknown account"
  }

  it should "store the account for an existing account" in {
    // We know we will use Account in a lot of tests
    // Let's encapsulate its creation through a Builder
    val account: Account = Account(UUID.randomUUID())
    val deposit = Deposit(account.id, 1000)
    val depositUseCase = new DepositUseCase(accountRepositoryStub)

    // Use functions to put an explicit on this setup
    (accountRepositoryStub.find _)
      .when(account.id)
      .returns(Some(account))

    val newAccount = depositUseCase.invoke(deposit)

    newAccount.isRight mustBe true
    (accountRepositoryStub.save _)
      .verify(newAccount.right.value)
      .once()
  }
}
```

Removed duplication and better setup
```scala
class unit.DepositShould extends AnyFlatSpec with MockFactory with EitherValues with Matchers {
  private val accountRepositoryStub = stub[AccountRepository]
  private val deposit = Deposit(UUID.randomUUID(), 1000)
  private val depositUseCase: DepositUseCase = new DepositUseCase(accountRepositoryStub)

  it should "return a failure for a non existing account" in {
    setupAccountNotFoundInRepository()
    depositUseCase.invoke(deposit).left.get mustBe "Unknown account"
  }

  it should "store the account for an existing account" in {
    val account: Account = aNewAccount(deposit.accountId).build()
    setupAccountExistingInRepository(account)

    val newAccount = depositUseCase.invoke(deposit)

    newAccount.isRight mustBe true
    (accountRepositoryStub.save _)
      .verify(newAccount.right.value)
      .once()
  }

  private def setupAccountExistingInRepository(account: Account): Unit =
    (accountRepositoryStub.find _)
      .when(deposit.accountId)
      .returns(Some(account))

  private def setupAccountNotFoundInRepository(): Unit =
    (accountRepositoryStub.find _)
      .when(deposit.accountId)
      .returns(None)
}
```

Create a builder for `Account`
```scala
class AccountBuilder(private val accountId: UUID) {
  def build(): Account = domain.Account(accountId)
}

object AccountBuilder {
  def aNewAccount(accountId: UUID = UUID.randomUUID()): AccountBuilder =
    new AccountBuilder(accountId)
}

// its usage
val account: Account = aNewAccount(deposit.accountId).build()
```

#### Implement deposit on Account
Add a new test class `unit.AccountShould` and identify our test list
```text
Return "Invalid amount" for 0
Given an empty account when I deposit 1000 then account should contain Transaction(currentDateTime, 1000)
Given an account containing already a Transaction(-200) when I deposit 1000 then account should contain Transaction(currentDateTime, 1000) 
```

:red_circle: Let's start with a failing test

```scala
  it should "return an error for a deposit of 0" in {
    val account = aNewAccount().build()
    account.deposit(0).left.get mustBe "Invalid amount for deposit"
  }
```

:green_circle: Implement the business rule -> return an error in case of invalid amount

```scala
  def deposit(amount: Double): Either[String, Account] = {
    if (amount <= 0) Left("Invalid amount for deposit")
    else Right(this)
  }
```

```text
✅ Return "Invalid amount" for 0
Given an empty account when I deposit 1000 then account should contain Transaction(currentDateTime, 1000)
Given an account containing already a Transaction(-200) when I deposit 1000 then account should contain Transaction(currentDateTime, 1000) 
```

:large_blue_circle: rename the account in the test -> emptyAccount

```scala
  it should "return an error for a deposit of 0" in {
    val emptyAccount = aNewAccount().build()
    emptyAccount.deposit(0).left.get mustBe "Invalid amount for deposit"
  }
```

:red_circle: Let's write a passing test

```scala
  it should "contain Transaction(currentDateTime, 1000) for an empty account and a deposit of 1000" in {
    val transactionTime = LocalDateTime.of(2022, 8, 19, 13, 0)

    (clockStub.now _)
      .when()
      .returns(transactionTime)

    emptyAccount
      .deposit(clockStub, 1000)
      .right
      .value
      .transactions must contain(Transaction(transactionTime, 1000))
  }
```

From here we identified that we need to add 2 stuff:
- create a new Value Object `Transaction`
- pass a `Clock` for the `Account` to be able to instantiate a `Transaction` with a date and a time

```scala
case class Transaction(at: LocalDateTime, amount: Double) {}

case class Account(id: UUID, transactions: List[Transaction] = List()) {

  def deposit(clock: Clock, amount: Double): Either[String, Account] = {
    if (amount <= 0) Left("Invalid amount for deposit")
    else
      Right(
        copy(transactions =
          List(
            Transaction(clock.now(), amount)
          )
        )
      )
  }
}

trait Clock {
  def now(): LocalDateTime
}
```

By changing the contract of `deposit` method we had an impact on the tests and production code.
Let's fix it and use your compiler as a driver.

```text
✅ Return "Invalid amount" for 0
✅ Given an empty account when I deposit 1000 then account should contain Transaction(currentDateTime, 1000)
Given an account containing already a Transaction(-200) when I deposit 1000 then account should contain Transaction(currentDateTime, 1000) 
```

:large_blue_circle: what can be improved?

:red_circle: Let's improve our confidence by adding a passing test for an `Account` containing existing transactions

```scala
  it should "contain Transaction(transactionTime, 1000) for an account containing already a Transaction(09/10/1987, -200) and a deposit of 1000" in {
    val transactionTime = LocalDateTime.of(2022, 8, 19, 13, 0)
    val formerTransaction = Transaction(LocalDateTime.of(1987, 10, 9, 23, 0), -200)

    val account = aNewAccount()
      .ContainingTransactions(formerTransaction)
      .build()

    (clockStub.now _)
      .when()
      .returns(transactionTime)

    account
      .deposit(clockStub, 1000)
      .right
      .value
      .transactions mustBe List(Transaction(transactionTime, 1000), formerTransaction)
  }
```

:green_circle: Here we need to adapt the test of the `AccountBuilder`

```scala
class AccountBuilder(private val accountId: UUID) {
  private var transactions: List[Transaction] = Nil

  def ContainingTransactions(transactions: Transaction*): AccountBuilder = {
    this.transactions = transactions.toList
    this
  }

  def build(): Account = Account(accountId, transactions)
}
```

```text
✅ Return "Invalid amount" for 0
✅ Given an empty account when I deposit 1000 then account should contain Transaction(currentDateTime, 1000)
✅ Given an account containing already a Transaction(-200) when I deposit 1000 then account should contain Transaction(currentDateTime, 1000) 
```

Then refactor the `deposit` code to handle this new test case

```scala
  def deposit(clock: Clock, amount: Double): Either[String, Account] = {
    if (amount <= 0) Left("Invalid amount for deposit")
    else
      Right(
        copy(transactions = Transaction(clock.now(), amount) :: transactions)
      )
  }
```

:large_blue_circle: Let's simplify the readability of our tests
- Centralize initialization of the stub
  - We need to use `OneInstancePerTest` trait to do so: more about it [here](https://scalamock.org/user-guide/sharing-scalatest/)
- Simplify the usage of `Transaction` and dates by creating a `TransactionBuilder`
  - And adapt the `AccountBuilder` accordingly

```scala
class AccountShould
    extends AnyFlatSpec
    with EitherValues
    with Matchers
    with MockFactory
    with OneInstancePerTest
    with BeforeAndAfterEach {

  private val transactionTime: LocalDateTime = aLocalDateTime

  private val clockStub: Clock = stub[Clock]
  (clockStub.now _).when().returns(transactionTime)

  private val emptyAccount = aNewAccount().build()

  it should "return an error for a deposit of 0" in {
    emptyAccount
      .deposit(clockStub, 0)
      .left
      .get mustBe "Invalid amount for deposit"
  }

  it should "contain Transaction(transactionTime, 1000) for an empty account and a deposit of 1000" in {
    emptyAccount
      .deposit(clockStub, 1000)
      .right
      .value
      .transactions must contain(Transaction(transactionTime, 1000))
  }

  it should "contain Transaction(transactionTime, 1000) for an account containing already a Transaction(09/10/1987, -200) and a deposit of 1000" in {
    val account = aNewAccount()
      .containing(
        aNewTransaction()
          .of(-200)
      )
      .build()

    account
      .deposit(clockStub, 1000)
      .right
      .value
      .transactions mustBe List(Transaction(transactionTime, 1000), account.transactions.head)
  }
}
```

The 2 Builders:

```scala
class AccountBuilder(private val accountId: UUID) {
  private var transactions: List[Transaction] = Nil

  def containing(transactions: TransactionBuilder*): AccountBuilder = {
    this.transactions = transactions
      .map(_.build())
      .toList

    this
  }

  def build(): Account = Account(accountId, transactions)
}

object AccountBuilder {
  def aNewAccount(accountId: UUID = UUID.randomUUID()): AccountBuilder =
    new AccountBuilder(accountId)
}

class TransactionBuilder {
  private var at = anotherDateTime
  private var amount = 1000d

  def madeAt(at: LocalDateTime): TransactionBuilder = {
    this.at = at
    this
  }

  def of(amount: Double): TransactionBuilder = {
    this.amount = amount
    this
  }

  def build(): Transaction = Transaction(at, amount)
}

object TransactionBuilder {
  def aNewTransaction(): TransactionBuilder = new TransactionBuilder()
}
```
