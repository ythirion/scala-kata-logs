import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ElectionsTest extends AnyFlatSpec with Matchers {
  "Elections" should "run without districts" in {
    val list = Map(
      "District 1" -> List("Bob", "Anna", "Jess", "July"),
      "District 2" -> List("Jerry", "Simon"),
      "District 3" -> List("Johnny", "Matt", "Carole")
    )

    val elections = new Elections(list, false)
    elections.addCandidate("Michel")
    elections.addCandidate("Jerry")
    elections.addCandidate("Johnny")

    elections.voteFor("Bob", "Jerry", "District 1")
    elections.voteFor("Jerry", "Jerry", "District 2")
    elections.voteFor("Anna", "Johnny", "District 1")
    elections.voteFor("Johnny", "Johnny", "District 3")
    elections.voteFor("Matt", "Donald", "District 3")
    elections.voteFor("Jess", "Joe", "District 1")
    elections.voteFor("Simon", "", "District 2")
    elections.voteFor("Carole", "", "District 3")

    val results = elections.results()

    // TODO Add approval tests here
  }

  "Elections" should "run with districts" in {
    val list = Map(
      "District 1" -> List("Bob", "Anna", "Jess", "July"),
      "District 2" -> List("Jerry", "Simon"),
      "District 3" -> List("Johnny", "Matt", "Carole")
    )

    val elections = new Elections(list, true)
    elections.addCandidate("Michel")
    elections.addCandidate("Jerry")
    elections.addCandidate("Johnny")

    elections.voteFor("Bob", "Jerry", "District 1")
    elections.voteFor("Jerry", "Jerry", "District 2")
    elections.voteFor("Anna", "Johnny", "District 1")
    elections.voteFor("Johnny", "Johnny", "District 3")
    elections.voteFor("Matt", "Donald", "District 3")
    elections.voteFor("Jess", "Joe", "District 1")
    elections.voteFor("July", "Jerry", "District 1")
    elections.voteFor("Simon", "", "District 2")
    elections.voteFor("Carole", "", "District 3")

    val results = elections.results()

    // TODO Add approval tests here
  }
}
