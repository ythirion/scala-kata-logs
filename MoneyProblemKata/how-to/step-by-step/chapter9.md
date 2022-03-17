### Chapter 9 - Currencies, Currencies, Everywhere
```text
1 USD + 1100 KRW = 2200 KRW
Determine exchange rate based on the currencies involved (from -> to)
```

* We can use this table to determine exchange rates :

| From | To   | Rate    |
|------|------|---------|
| EUR  | USD  | 1.2     |
| USD  | EUR  | 0.82    |
| USD  | KRW  | 1100    |
| KRW  | EUR  | 0.0009  |
| EUR  | KRW  | 1344    |
| KRW  | EUR  | 0.00073 |

* Let's write our next test :
    * It fails with this message : `Expected portfolio.Evaluate(Currency.KRW) to be 2200 KRW, but found 1101.2 KRW.`
        * It takes the EUR to USD change rate
```c#
[Fact(DisplayName = "1 USD + 1100 KRW = 2200 KRW")]
public void AddDollarsAndKoreanWons()
{
    var portfolio = new Portfolio(1d.Dollars(), 1100d.KoreanWons());
    portfolio.Evaluate(Currency.KRW)
        .Should()
        .Be(2200d.KoreanWons());
} 
```
* Let's introduce a `Dictionary` to store exchange rates
    * Add the 2 entries we need now (EUR -> USD, USD -> KRW)
        * We use a function `KeyFor` that makes it easy to generate a key for a currency pair (from -> to)
    * We use it in the convert method
```c#
public record Portfolio(params Money[] Moneys)
{
    private static readonly Dictionary<string, double> ExchangeRates = new()
    {
        {KeyFor(EUR, USD), 1.2},
        {KeyFor(USD, KRW), 1100},
    };

    public Money Evaluate(Currency currency) =>
        new(Moneys.Aggregate(0d, (acc, money) => acc + Convert(money, currency)), currency);

    private static double Convert(Money money, Currency currency) =>
        currency == money.Currency
            ? money.Amount
            : money.Amount * ExchangeRates[KeyFor(money.Currency, currency)];

    private static string KeyFor(Currency from, Currency to) => $"{from}->{to}";
}
```

* What happens if we try to evaluate in a currency without `exchangeRates` ?
    * Remove all entries from our `Dictionary`
    * It fails with the message : `System.Collections.Generic.KeyNotFoundException: The given key 'USD->KRW' was not present in the dictionary.`
* We need to improve error handling in our code
    * Let's add it in our feature list

#### Where we are
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
Improve error handling when exchange rates are unspecified
Allow exchange rates to be modified
```