### Chapter 3 - Portfolio
* We can multiply and divide amounts in any one currency by numbers
* Now we need to add amounts in multiple currencies

#### Designing our Next Test
`5 USD + 10 EUR = 17 USD`
* TDD plays nicely with software design
    * TDD gives us an opportunity to pause after each RGR cycle and design our code intentionally
* We realize with this feature that "adding dollars to dollars results in dollars" is an oversimplification
* Adding `Money` entities in different currencies gives us a `Portfolio
    * That can be expressed in any one currency
* We just introduced a new concept
    * Let's reflect this reality in our domain model

* Let's start with a test to add 2 `Money` entities in the same currency : `5 USD + 10 USD = 15 USD`

* Create our first `Portfolio` test :
```c#
[Fact(DisplayName = "5 USD + 10 USD = 15 USD")]
public void Addition()
{
    var fiveDollars = new Money(5, Currency.USD);
    var tenDollars = new Money(10, Currency.USD);
    
    // Declare an empty Portfolio
    var portfolio = new Portfolio();
    // Add multiple Money in it
    portfolio.Add(fiveDollars, tenDollars);
    // Evaluate the Portfolio in a given currency
    portfolio.Evaluate(Currency.USD)
        .Should()
        .Be(new Money(15, Currency.USD));
}
```

* Use our IDE to implement missing methods amd make our test green

```c#
public class Portfolio
{
    private readonly List<Money> _moneys = new();
    public void Add(params Money[] moneys) => _moneys.AddRange(moneys);
    public Money Evaluate(Currency currency) => new(15, Currency.USD);
}
```

* Refactor
    * Where is the duplication / code smells ?
    * Let's work on the hardcode values : 15 / USD
        * We can simply sums up moneys

```c#
public class Portfolio
{
    private readonly List<Money> _moneys = new();
    public void Add(params Money[] moneys) => _moneys.AddRange(moneys);
    public Money Evaluate(Currency currency) =>
        new(_moneys.Aggregate(0d, (acc, money) => acc + money.Amount), currency);
}
```

* Do we really need to have a state in Portfolio ?
    * Let's refactor it to only have Pure functions and immutable data structure
    * It's a design choice

```c#
[Fact(DisplayName = "5 USD + 10 USD = 15 USD")]
public void Addition()
{
    var fiveDollars = new Money(5, Currency.USD);
    var tenDollars = new Money(10, Currency.USD);

    var portfolio = new Portfolio(fiveDollars, tenDollars);
    portfolio.Evaluate(Currency.USD)
        .Should()
        .Be(new Money(15, Currency.USD));
}

public record Portfolio(params Money[] Moneys)
{
    public Money Evaluate(Currency currency) =>
        new(Moneys.Aggregate(0d, (acc, money) => acc + money.Amount), currency);
}
```

#### Where we are ?
* We started to tackle the problem of adding different representations of `Money`
    * This requires introduction of exchange rates
* We used a divide-and-conquer strategy to
    * Add 2 `Money entities` (through constructor here)
    * And evaluate in the same currency
* We can notice that our code is growing
    * We need to restructure it : separate our tests from production code

```text
✅ 5 USD x 2 = 10 USD 
✅ 10 EUR x 2 = 20 EUR
✅ 4002 KRW / 4 = 1000.5 KRW
✅ 5 USD + 10 USD = 15 USD
5 USD + 10 EUR = 17 USD
1 USD + 1100 KRW = 2200 KRW
```