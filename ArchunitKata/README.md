# Test your Architecture with ArchUnit
## Learning Goals
- Understand the advantages of using a library like `ArchUnit`
- Clarify how we can use it on a daily basis

- ![Architecture tests](img/break.png)

## Connect - Architecture?
In group, discuss what are your current `architecture rules` inside your team and how you ensure that your code is always matching those rules

Examples :
 
- All our events must reside in the events package
- Our repository layer can only be accessed through our application layer
- …

## Concepts - What is it?
You can use the slides available [here](https://speakerdeck.com/thirion/test-your-architecture-with-archunit) for this part

![Zoom](img/zoom.png)

## Concrete Practice - Create some rules
To simplify the usage of the library during this kata some preparation work has already been made:
- The dependency is already there in the `build.sbt` file
```scala
libraryDependencies += "com.tngtech.archunit" % "archunit" % "0.23.1" % Test
```
- A base class for the test has been defined: `ArchUnitFunSpec`
  - To simplify the check of the `ArchRule` through `scalatest`
  - The class takes a few constructor parameters: 

```scala
abstract class ArchUnitFunSpec(private val name: String, // Name of the tests
                               private val packages: String, // packages to inspect from the rules
                               private val rules: ArchRule*) // ArchRule to run -> the ones defined in the class itself 
               extends AnyFunSpec {
  describe(name) {
    val classes = new ClassFileImporter().importPackages(packages)

    rules.foreach { rule =>
      it(rule.getDescription) {
        rule.check(classes)
      }
    }
  }
}

object ArchUnitFunSpec {
  val emptyRule: ArchRule = noClasses()
    .should()
    .be("Dummy")
}
```

- Test classes have been provided but implementations are missing
  - You have to fill the gap to implement the rule expressed in plain text english correctly
    - Do it for each rule using the `emptyRule`
  - If you want you can `fix` the production code based on the `ArchRule` discoveries - *Optional*

### Example
```scala
private val `no classes should depend on another`: ArchRule =
    emptyRule
      .as("Class with name SomeExample should not depend on Other")
```

- We use the `ArchUnit` dsl to write it
  - Let the public `api` guide you

```scala
private val `no classes should depend on another`: ArchRule =
    noClasses()
          .that().haveSimpleName("SomeExample")
          .should()
          .dependOnClassesThat().haveSimpleName("Other")
```

### Exercises

Recommended order:
- `LayeredArchitectureTests`
- `NamingConventionTests`
- `GuidelineTests`
- `LinguisticAntiPatternsTests`
- `MethodsReturnTypesTests`

Which other `rules` could be added?

### Solution
Solution is available in the `solution` folder

## Conclusion - Reflect
- What can we do with it ?
- Which rules could be useful ?
- Any volunteers to go further ?

![Reflect](img/reflect.png)