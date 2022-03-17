### Chapter 8 - Evaluating a Portfolio
`Money itself isn't lost or made. It's simply transfered from one perception to another. Like magic. - Gordon Gekko, Wall Street`

#### Mixing Money
Heterogeneous combination of currencies demands that we create a new abstraction : conversion of money from one currency to another :
```text
- Conversion always relates a pair of currencies
- Conversion is from one currency to another with a well-defined exchange rate
- The two exchange rates between a pair of currencies may or may not be arithmetical reciprocals of each other
    - Exchange Rate from EUR to USD may or may not be the mathematical reciprocal of USD to EUR
- It is possible for a currency to have no defined exchange rate to another currency
    - Inconvertible currencies : economical, political, ... reasons    
```

* Add our next test :

```c#
[Fact(DisplayName = "5 USD + 10 EUR = 17 USD")]
public void AddDollarsAndEuros()
{
    var fiveDollars = new Money(5, Currency.USD);
    var tenEuros = new Money(10, Currency.EUR);

    var portfolio = new Portfolio(fiveDollars, tenEuros);
    portfolio.Evaluate(Currency.USD)
        .Should()
        .Be(new Money(17, Currency.USD));
}
```

* We need an exchange rate from EUR to USD
    * We hardcode it for now (with a const)
* We add a `Convert` method :

```c#
public record Portfolio(params Money[] Moneys)
{
    private const double EuroToUsd = 1.2;

    public Money Evaluate(Currency currency) =>
        new(Moneys.Aggregate(0d, (acc, money) => acc + Convert(money, currency)), currency);

    private static double Convert(Money money, Currency currency) =>
        currency == money.Currency
            ? money.Amount
            : money.Amount * EuroToUsd;
}
```

#### Remove redundancy
* A cool feature of C# is the ability to declare and use extension methods on primitive types
* In our tests we instantiate a lot of `Money` objects
    * Let's instantiate them in a more fluent way thanks to an extension method on `double`
        * Ex : `2.Dollars()`
    * It allows us to create true business DSL

```c#
public static class DomainExtensions
{
    public static Money Dollars(this double amount) => new(amount, Currency.USD);
    public static Money Euros(this double amount) => new(amount, Currency.EUR);
    public static Money KoreanWons(this double amount) => new(amount, Currency.KRW);
}
```

* Let's use it in our tests

```c#
[Fact(DisplayName = "5 USD + 10 EUR = 17 USD")]
public void AddDollarsAndEuros()
{
    var portfolio = new Portfolio(5d.Dollars(), 10d.Euros());
    portfolio.Evaluate(Currency.USD)
        .Should()
        .Be(17d.Dollars());
}
```

#### Where we are
```text
✅ 5 USD x 2 = 10 USD 
✅ 10 EUR x 2 = 20 EUR
✅ 4002 KRW / 4 = 1000.5 KRW
✅ 5 USD + 10 USD = 15 USD
✅ Separate test code from production code
✅ Remove redundant tests
✅ 5 USD + 10 EUR = 17 USD
1 USD + 1100 KRW = 2200 KRW
Determine exchange rate based on the currencies involved (from -> to)
Allow exchange rates to be modified
```