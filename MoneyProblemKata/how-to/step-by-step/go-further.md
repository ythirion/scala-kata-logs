# Go Further
* Our `Domain` is fully `Functional`
  ![Functional Core Imperative Shell](../../img/functional-core-imperative-shell.png)
* Know more about it [here](https://www.youtube.com/watch?v=yTkzNHF6rMs&ab_channel=Confreaks)

> Could we benefit from using a functional language ?
* Let's try it 👌
* Remove the project reference to `money-problem.Domain` from `money-problem.Domain`
* Rename `money-problem.Domain` to `money-problem.Domain.old`
* Create a new `F#` project called `money-problem.Domain`
* Migrate the `C#` domain to `F#`
    * Try to not change the `test` code

![Go Further](../../img/go-further.png)

My implementation is available in the `functional-core-f#` branch of this repository

## Modules
* In F# we don't use classes we use `modules`
  * There are three common patterns for mixing types and functions together :
    * Declare Types in the SAME MODULE as the functions
    * Declare Types SEPARATELY from the functions but in the SAME FILE
    * Type declared separately from the functions and in a different file
      * Containing type definitions only
  * Deep dive [here](https://fsharpforfunandprofit.com/posts/recipe-part3/)
* I have chosen option 2
  * I have centralized all the `Domain types` in the `Types` module
```f#
module Types =
    type Currency =
        | EUR
        | USD
        | KRW

    type Money = { Amount: double; Currency: Currency }
    type Bank = { ExchangeRates: Map<string, double> }
    type Portfolio = Money list
```
* Start creating the `Money` module
```f#
open Types

module Money =
    let from(amount, currency) : Money = { Amount = amount; Currency = currency  }
    let times(money, multiplier): Money = { money with Amount = money.Amount * multiplier }
    let divide(money, divisor): Money = { money with Amount = money.Amount / divisor }
```
* In `F#` we use `camelCase`
  * To not have weird naming with our `C#` calls we can declare to the compiler a name for compilation
```f#
let from(amount, currency) : Money = { Amount = amount; Currency = currency  }
[<CompiledName("Times")>]
let times(money, multiplier): Money = { money with Amount = money.Amount * multiplier }
[<CompiledName("Divide")>]
let divide(money, divisor): Money = { money with Amount = money.Amount / divisor }
```
* Let's add Extension methods
  * To do so we need to add `Extension` annotation at the `function` and `module` level
  * Declare the 
```f#
[<Extension>]
module Money =
    let from(amount, currency) : Money = { Amount = amount; Currency = currency  }
    [<CompiledName("Times"); Extension>]
    let times(money, multiplier): Money = { money with Amount = money.Amount * multiplier }
    [<CompiledName("Divide"); Extension>]
    let divide(money, divisor): Money = { money with Amount = money.Amount / divisor }
    
    // Extension methods
    [<CompiledName("Dollars"); Extension>]
    let dollars (amount: Double) : Money = { Amount = amount; Currency = USD }
    [<CompiledName("Euros"); Extension>]
    let euros (amount: Double) : Money = { Amount = amount; Currency = EUR }
    [<CompiledName("KoreanWons"); Extension>]
    let koreanWons (amount: Double) : Money = { Amount = amount; Currency = KRW }
```
* Create the `Bank` module
  * We want to have the least impact on our test code
  * F# does not support `Either` by default so let's continue to use `LanguageExt`
  * We need to add a `#nowarn "3391"` to not have a warning on the implicit conversions to `Either`
  * Observe the pattern matching in the `convert` function
```f#
#nowarn "3391"
[<Extension>]
module Bank =
    let private keyFor(fromCurrency: Currency, toCurrency: Currency) = $"{fromCurrency}->{toCurrency}"
    
    [<CompiledName("WithExchangeRate")>]
    let withExchangeRate fromCurrency toCurrency rate = { ExchangeRates = Map.empty.Add(keyFor(fromCurrency, toCurrency), rate) }
    
    [<CompiledName("AddExchangeRate"); Extension>]
    let addExchangeRates(bank: Bank, fromCurrency: Currency, toCurrency: Currency, rate: double) =
        { bank with ExchangeRates = bank.ExchangeRates.Add(keyFor(fromCurrency, toCurrency), rate) }
    
    let private convertSafely (bank: Bank, money: Money, currency: Currency) =
        Money.from (
        money.Amount * bank.ExchangeRates.[keyFor (money.Currency, currency)],
            currency
        )

    [<CompiledName("Convert"); Extension>]
    let convert (bank: Bank, money: Money, toCurrency: Currency) : Either<string, Money> =
        let exchangeKey = keyFor (money.Currency, toCurrency)

        match money.Currency with
        | from when from = toCurrency -> money
        | _ when bank.ExchangeRates.ContainsKey(exchangeKey) -> convertSafely (bank, money, toCurrency)
        | _ -> exchangeKey
```
* Let's finish with the `Portfolio`
```f#
[<Extension>]
module Portfolio = 
    [<CompiledName("Evaluate"); Extension>]
    let evaluate(portfolio: Portfolio,
                 bank: Bank,
                 currency: Currency): Either<string, Money> =
        let convertedMoneys = portfolio |> List.map(fun money -> Bank.convert(bank, money, currency))
        if(convertedMoneys.Lefts() |> Seq.isEmpty) then
            let foldAmount = convertedMoneys.Rights().Fold(0., fun acc money -> acc + money.Amount)
            Money.from(foldAmount, currency)
        else
            let errorMessage = convertedMoneys.Lefts() |> String.concat ","
            $"Missing exchange rate(s): [{errorMessage}]"
    
    [<CompiledName("AddToPortfolio"); Extension>]
    let addToPortfolio money1 money2: Portfolio = [money1; money2]
    
    [<CompiledName("Add"); Extension>]
    let add(portfolio: Portfolio, money: Money): Portfolio = money :: portfolio 
```

## What are your learnings
* What do you think about F# ?
* What are your discoveries ?
* What do you think about Interop between the 2 languages ?
