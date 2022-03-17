### Chapter 10 - Error Handling
`What error drives our eyes and ears amiss ? - William Shakespeare`

#### Error Wish List
```text
- The Evaluate method should signal an explicit error when one or more necessary exchange rates ares missing
- The error message should be "greedy" - indicate all the missing exchange rates
- To prevent error from being ignored by the caller : no valid Money should be returned when an error happens due to missing exchange rates
```

* We will use exception here in case of failure
    * We will use other data structure later (`Either<string,Money>` for example)
```c#
[Fact(DisplayName = "Throw greedy exception in case of missing exchange rates")]
public void AddWithMissingExchangeRatesShouldThrowGreedyException()
{
    var portfolio = new Portfolio(1d.Dollars(), 1d.Euros(), 1d.KoreanWons());
    portfolio.Invoking(p => p.Evaluate(Currency.KRW))
        .Should()
        .Throw<MissingExchangeRatesException>()
        .WithMessage("Missing exchange rate(s): [EUR->KRW]");
}  
```

* Create the `MissingExchangeRatesException` class
```c#
public class MissingExchangeRatesException : Exception
{
    public MissingExchangeRatesException()
    {
    }
} 
```
* Add a `CheckExchangeRates` method that will throw a `MissingExchangeRatesException` in case of missing Exchange rates
```c#
public Money Evaluate(Currency toCurrency)
{
    CheckExchangeRates(toCurrency);
    return new Money(Moneys.Aggregate(0d, (acc, money) => acc + Convert(money, toCurrency)), toCurrency);


private void CheckExchangeRates(Currency toCurrency)
{
    var missingExchangeRates =
        Moneys.Select(m => m.Currency)
            .Where(c => c != toCurrency)
            .Distinct()
            .Select(c => KeyFor(c, toCurrency))
            .Where(key => !ExchangeRates.ContainsKey(key))
            .ToArray();

    if (missingExchangeRates.Any())
        throw new MissingExchangeRatesException(missingExchangeRates);
}
```
* Improve our `MissingExchangeRatesException` to create a descriptive message :
```c#
public class MissingExchangeRatesException : Exception
{
    public MissingExchangeRatesException(string[] missingExchangeRates)
        : base($"Missing exchange rate(s): [{string.Join(",", missingExchangeRates)}]")
    {
    }
}
```
* Our test is green now

#### Improve Portfolio instantiation
* Extension methods can make our future refactoring easiest
```c#
public static Portfolio AddToPortfolio(this Money money1, Money money2) => new(money1, money2);
public static Portfolio AddToPortfolio(this Portfolio portfolio, Money money) => new(portfolio.Moneys.Append(money).ToArray());

// Impact in the tests 
[Fact(DisplayName = "Throw greedy exception in case of missing exchange rates")]
public void AddWithMissingExchangeRatesShouldThrowGreedyException()
{
    var portfolio = 1d.Dollars()
        .AddToPortfolio(1d.Euros())
        .AddToPortfolio(1d.KoreanWons());

    portfolio.Invoking(p => p.Evaluate(Currency.KRW))
        .Should()
        .Throw<MissingExchangeRatesException>()
        .WithMessage("Missing exchange rate(s): [EUR->KRW]");
} 
```

#### Where we are
* We have added error handling
    * Portfolio evaluation is not simple anymore
        * Clumsy code to check if we have missing exchange rates
    * Let's add a new feature in our list
```text
✅ 5 USD x 2 = 10 USD 
✅ 10 EUR x 2 = 20 EUR
✅ 4002 KRW / 4 = 1000.5 KRW
✅ 5 USD + 10 USD = 15 USD
✅ Separate test code from production code
✅ Remove redundant tests
✅ 5 USD + 10 EUR = 17 USD
✅ 1 USD + 1100 KRW = 2200 KRW
✅ Determine exchange rate based on the currencies involved (from -> to)
✅ Improve error handling when exchange rates are unspecified
Improve the implementation of exchange rates
Allow exchange rates to be modified
```