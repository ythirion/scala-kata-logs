## How to use `ApprovalTests` in scala with sbt
- In your `build.sbt` add those dependencies

```scala
"net.aichler" % "jupiter-interface" % "0.11.1",
"com.approvaltests" % "approvaltests" % "18.5.0" % "test",
```

- In your `project/plugins.sbt` add this plugin
	- Ensure that we can run our `junit` tests with `sbt`

```scala
addSbtPlugin("net.aichler" % "sbt-jupiter-interface" % "0.11.1")
```

## Approval and CI
You should use the `QuietReporter` to avoid reporter issues in your CI pipelines in case of test failure.

```scala
import org.approvaltests.combinations.CombinationApprovals.verifyAllCombinations
import org.approvaltests.core.ApprovalFailureReporter
import org.approvaltests.reporters.{QuietReporter, UseReporter}
import org.junit.jupiter.api.Test

@UseReporter(Array[ApprovalFailureReporter](classOf[QuietReporter]))
class GildedRoseTest {
  
}
```

You can configure it for your whole package by following instructions available [here](https://github.com/approvals/ApprovalTests.Java/blob/master/approvaltests/docs/Reporters.md#configuring-a-reporter)

## Use scrubber
![Scrubber](img/scrubber.png)

Imagine you want to `approve` this code

```scala
  private case class Person(
      id: UUID,
      creationDate: LocalDate,
      firstName: String,
      lastName: String
  )

  private val tonyMontana = Person(
    id = UUID.randomUUID(),
    creationDate = LocalDate.now(),
    firstName = "Tony",
    lastName = "Montana"
  )
```

If you simply use `Approvals` it will result with a file like this:

```text
Person(1766e8d3-ab87-4209-a7ee-fa3a0da5a4b4,2022-11-18T11:59:21.438083,Tony,Montana)
```

If you run it again, your tests will fail because of non-deterministic data (date/UUID)

Let's use `Scrubbers` to solve it:

```scala
package com.gildedrose

import org.approvaltests.Approvals
import org.approvaltests.core.{Options, Scrubber}
import org.approvaltests.scrubbers.{GuidScrubber, RegExScrubber}
import org.junit.jupiter.api.Test

import java.time.LocalDate
import java.util.UUID

class ScrubbingExample {
  @Test def simple(): Unit = {
    Approvals.verify(
      tonyMontana,
      new Options(
        new Scrubbers(
          new RegExScrubber(
            "\\d{4}-\\d{2}-\\d{2}",
            "date"
          ),
          new GuidScrubber
        )
      )
    )
  }

  private case class Person(
      id: UUID,
      creationDate: LocalDate,
      firstName: String,
      lastName: String
  )

  private val tonyMontana = Person(
    id = UUID.randomUUID(),
    creationDate = LocalDate.now(),
    firstName = "Tony",
    lastName = "Montana"
  )

  // scrubAll is failing with interop
  private sealed case class Scrubbers(scrubbers: Scrubber*) extends Scrubber {
    override def scrub(input: String): String = {
      scrubbers
        .foldLeft(input) { (str, scrubber) =>
          scrubber.scrub(str)
        }
    }
  }
}
```

More about it [here](https://github.com/approvals/ApprovalTests.Java/blob/master/approvaltests/docs/Scrubbers.md#top)

## Use Json
You can use `json` format by using the `JsonApprovals` class.

You need to add `gson` dependency for that.