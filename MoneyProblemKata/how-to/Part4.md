## Part 4 - Finishing Up
### Chapter 12 - Test Order
* One feature of our new `Bank` entity is the ability to accept and store any pair of currencies
* We want to gain confidence in the feature that `allow exchange rates to be modified`
* Why ?
    * To repeat : a new test would *increase our confidence* in this feature
        * even if no new production code is necessary
    * The new test would serve as *executable documentation* of this feature
    * The test may expose *inadvertent interactions* between existing tests

### Add a conversion test
* Add a test `Conversion with different exchange rates EUR -> USD`
  * What happens ?
* Do we have a problem with test order ?
  * If you have designed your code with immutability you should not

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

[step by step guide](step-by-step/chapter12.md)

### Chapter 13 - Continuous Integration
> With continuous integration, your software is proven to work (assuming a sufficiently comprehensive set of automated tests) with every new change-and you now the moment it breaks and can fix it immediately - *Jez Humble and David Farley*

* Software entropy is the principle that the degree of disorder in a system tends to increase over time
* Our best current defense against the ruinous effect of code chaos is `Continuous Delivery`

![Continuous Integration](../img/continuous-integration.png)

#### Putting It All Together
* We'll use GitHub Actions to add continuous integration to our project
* Here are the steps to build a CI Pipeline for our code :

```
1. Create and/or verify our GitHub account
2. Create a new project in GitHub
3. Push our code repository to GitHub
4. Prepare the source code for CI build scripts
5. Create a CI build script for our language (C# here)
6. Push the build scripts to GitHub
```

#### Where we are
* At end of our journey of writing code to solve the “Money” problem
* We have covered a lot of ground
    * We have written code
    * Written tests
    * Deleted and refined both
    * Added continuous integration
* There’s something more that we deserve and need: a review of our journey

[step by step guide](step-by-step/chapter13.md)

### Chapter 14 - Retrospective
`Retrospectives can be a powerful catalyst for change. A major transformation can start
from a single retrospective. —Esther Derby and Diana Larsen, Agile Retrospectives`

* Let’s take some time to recap what we did and reflect upon how we did it.
* We’ll frame our retrospective along these dimensions:
    * Profile : refers to the shape of the code
    * Purpose : includes what the code does and—more importantly—does not do
    * Process :
        * How we got to where we are
        * What other ways might have been possible
        * Implications of taking a specific path

#### Profile
* Include both :
    * Subjective aspects (readability, obviousness)
    * Their objective manifestations : namely complexity, coupling, and succinctness
* **Cyclomatic Complexity** : a measure of the degree of branching and looping in code
* **Coupling** : a measure of the interdependency of a block of code (e.g., a class or method) on other blocks of code
    * The two kinds of coupling are afferent and efferent coupling :
        * _Afferent_  : number of other components that depend on a given component
        * _Efferent_ : number of other components that a given component depends on
    * A measure of the stability of the code is the balance between afferent and efferent coupling
```text
Instability = efferent / efferent + afferent
```
* **Succinctness** :
    * Lines of code is a dangerous metric—especially across different languages
    * Compare lines of test code to the lines of production code in the same language

#### Purpose
* **Cohesion** : measure of the relatedness of the code in a module
    * High cohesion reflects that the code in a module—method, class, or package—represents a single, unified concept
* **Completeness** : Does our code do everything it should?
    * How complete are our tests, though?
    * Could we gain more confidence by writing additional tests?
        * Consider these cases: Overflow, Underflow, Division by zero

#### Process
* Assess the process of how we got to the final version of our code

* Reflect on your own code
* What could be said about it regarding the 3P ?
* What surprised you during this workshop ?
* What have been your discoveries ?

[step by step guide](step-by-step/chapter14.md)

![Thanks](../img/thanks.png)

`Thanks for having followed it and thanks Saleem for this great book`