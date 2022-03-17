import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class EmptyTest extends AnyFunSuite with Matchers{
  test("scalatest should work") {
    42 shouldBe 43
  }
}