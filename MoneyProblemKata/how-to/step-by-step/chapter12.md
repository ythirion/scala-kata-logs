### Chapter 12 - Test Order
* One feature of our new `Bank` entity is the ability to accept and store any pair of currencies
* We want to gain confidence in the feature that `allow exchange rates to be modified`
* Why ?
    * To repeat : a new test would *increase our confidence* in this feature
        * even if no new production code is necessary
    * The new test would serve as *executable documentation* of this feature
    * The test may expose *inadvertent interactions* between existing tests

### Add a conversion test
```c#
[Fact(DisplayName = "Conversion with different exchange rates EUR -> USD")]
public void ConvertWithDifferentExchangeRates()
{
    _bank.Convert(10d.Euros(), USD)
        .RightUnsafe()
        .Should()
        .Be(12d.Dollars());

    _bank.AddExchangeRate(EUR, USD, 1.3)
        .Convert(10d.Euros(), USD)
        .RightUnsafe()
        .Should()
        .Be(13d.Dollars());
}
```

* It's failing because of the implementation choice made earlier
    * The `Add` method throws an exception when adding 2 items with the same key
    * Simply use the `Set` method instead
```c#
new(_exchangeRates.Add(KeyFor(from, to), rate));
// use Set instead
new(_exchangeRates.SetItem(KeyFor(from, to), rate));
```
* Our test is now green

### Do we have a problem with test order ?
* Is the updated "EUR -> USD" exchange rate could have an impact on other tests ?
* Because of the immutable nature of our objects there is no chance for it
    * That's the purpose of immutability : avoid side effects
> Tests, especially unit tests, should be independent from each other.

#### Where we are
* We added tests to document an existing feature (that we improved)
* We're done with our list of features
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
✅ Improve the implementation of exchange rates
✅ Allow exchange rates to be modified
```