### Chapter 1 - The money problem
#### Building block of TDD
A 3-phase process :

* ***Red*** : We write a failing test
    * Including possible compilation failures
    * We run the test suite to verify the failing test
* ***Green*** : We write **just enough production code** to make the test green
    * We run the test suite to verify this
* ***Refactor*** : We remove any code smells
    * Duplication, hardcoded values, improper use of language idioms, ...
    * If we break any test during this phase :
        * Prioritize getting back to green before exiting this phase

![TDD phases](../../img/tdd.png)

### What's the problem ?
We have  to build a spreadsheet to manage money in more than one currency : perhaps to manage a stock portfolio ?

| Stock | Stock exchange | Shares | Share Price | Total |
|---|---|---|---|---|
| IBM | NASDAQ | 100 | 124 USD | 12400 USD |
| BMW | DAX | 400 | 75 EUR | 30000 EUR |
| Samsung | KSE | 300 | 68000 KRW | 20400000 KRW |

![Money problem](../../img/money-problem.png)

To build it, we'd need to do simple arithmetic operations on numbers :

```text
5 USD x 2 = 10 USD
4002 KRW / 4 = 1000.5 KRW

// convert
5 USD + 10 EUR = 17 USD
1 USD + 1100 KRW = 2200 KRW
```

List of Features to implement :

```text
5 USD x 2 = 10 USD
10 EUR x 2 = 20 EUR
4002 KRW / 4 = 1000.5 KRW
5 USD + 10 EUR = 17 USD
1 USD + 1100 KRW = 2200 KRW
```

### Our first failing test
* Create a new dotnet project called `money-problem` :

```shell
dotnet new xunit -n "money-problem"
dotnet test
```

* Write our first test for
    * `5 USD x 2 = 10 USD`
    * This code does not compile
    * Congrats here is our failing test

```c#
public class TestMoney
{
    [Fact]
    public void TestMultiplication()
    {
        var fiver = new Dollar(5);
        var tenner = fiver.Times(2);

        tenner.Amount.Should().Be(10);
    }
}
```

### Going for Green
* We wrote our tests as we would expect them to work
* Start with the smallest bit of code that sets up on the path to progress
    * Add an abstraction `Dollar`
    * Use IDE for that

```c#
public record Dollar(int Amount)
{
    public Dollar Times(int multiplier) => new(10);
}
```

### Cleaning Up
* Just enough code to make the test pass
* Remove hardcoded values

```c#
public record Dollar(int Amount)
{
    public Dollar Times(int multiplier) => new(Amount * multiplier);
}
```