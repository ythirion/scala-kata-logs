### Chapter 4 - Separation of Concerns
* Our source code has grown
* Let's spend some time organizing it

#### Test and Production Code
We have written 2 types of code :
1. Code that `solves` our `Money` problem
* Including `Money` / `Portfolio`
* We call this `production code`
2. Code that `verifies the problem is correctly solved`
    * Including all the tests and the code needed to support these tests
    * We call this `test code`

`Test code depends on production code, however there should be no dependency in the other direction`

#### Packaging and Deployment
`Test code should be packaged separately from production code so that they can be deployed independently via CI/CD pipeline`
* Modularization
    * Let's separate the test code from the production code
* This means :
    * Test and production code should be in separate files
        * Allows to read / edit / focus on test or production code independently
    * The code should use namespaces to clearly identify which entities belong together
        * A namespace may be called a "module" or "package'
    * Add explicit `import` in our test code

#### Removing redundancy
* We have had 2 multiplication tests
    * They test the same functionality
    * In contrast we have only one test for division
    * `Should we keep both the multiplication tests ?`

#### Checklist for cleaning tests :
- [ ] Would we have the same code coverage if we delete a test ?
- [ ] Does one of the tests verify a significant edge case ?
- [ ] Do the different tests provide unique value as a `living documentation` ?

#### Update our feature list
```text
✅ 5 USD x 2 = 10 USD 
✅ 10 EUR x 2 = 20 EUR
✅ 4002 KRW / 4 = 1000.5 KRW
✅ 5 USD + 10 USD = 15 USD
Separate test code from production code
Remove redundant tests
5 USD + 10 EUR = 17 USD
1 USD + 1100 KRW = 2200 KRW
```

The steps of separation of concerns vary from language to language. Saleem has dedicated 3 chapters in his boook on this topic :
* Chapter 5, "Packages and Modules in Go"
* Chapter 6, "Modules in Javascript"
* Chapter 7, "Modules in Python"

I propose here how I would do that in C#.

### Chapter "custom" Projects and namespaces in C#
#### Splitting Our Code into Classes
* Start our separation of concerns by splitting our file into distinct Classes
```
money-problem
│─── Currency.cs
│─── Money.cs    
│─── Portfolio.cs
│─── MoneyShould.cs
│─── PortfolioShould.cs
```

#### Separate Production code and Test Code
* Let's isolate our Test code by creating a new Project for `production code`
* To do it, we have some prerequisites :
    * Create an empty solution `money-problem`
    * Create a folder for `money-problem.Tests`
        * Move actual files in this folder
    * Add the existing project `money-problem.Tests` to the solution
* Create a new project (`Class Library`)
    * Name it `money-problem.Domain`
    * Move `Domain entities` to it
    * Our tests are now `Red`
        * Add dependency to the `Domain` project in the `Tests` project
        * Dependency is unidirectional so we can not reference test objects from our `production code`

```
money-problem   
│─── money-problem.Domain
│        │─── Currency.cs
│        │─── Money.cs    
│        │─── Portfolio.cs
│─── money-problem.Tests
│        │─── MoneyShould.cs
│        │─── PortfolioShould.cs
```

#### Fix our tests
* Our tests are not compiling anymore
* We need to fix `usings` in our tests :
```c#
using money_problem.Domain;
```

#### Remove Redundancy in Tests
* We have 2 tests on multiplication
    * The 2 tests test the same functionality and does not provide any added-value according to our check-list :

- [X] Would we have the same code coverage if we delete a tests ?
- [ ] Does one of the tests verify a significant edge case ?
- [ ] Do the different tests provide unique value as a `living documentation` ?

* Delete the `MultiplyInDollars`
    * Rename the tests to represent the features under tests :
    * Add / Divide / Multiply

#### Update our feature list
```text
✅ 5 USD x 2 = 10 USD 
✅ 10 EUR x 2 = 20 EUR
✅ 4002 KRW / 4 = 1000.5 KRW
✅ 5 USD + 10 USD = 15 USD
✅ Separate test code from production code
✅ Remove redundant tests
5 USD + 10 EUR = 17 USD
1 USD + 1100 KRW = 2200 KRW
```