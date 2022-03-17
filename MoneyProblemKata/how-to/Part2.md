## Part 2 - Modularization
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

* Separate the test code from the production code
    * How to do it ?
    * Reflect on dependencies
* Remove redundancy
    * We have 2 multiplication tests
    * `Should we keep both the multiplication tests ?`
    * Fill the checklist mentioned above

#### Where we are ?
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

[step by step guide](step-by-step/chapter4.md)