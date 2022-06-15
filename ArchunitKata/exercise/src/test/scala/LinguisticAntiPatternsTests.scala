import ArchUnitFunSpec.emptyRule
import LinguisticAntiPatternsTests._
import com.tngtech.archunit.lang._


class LinguisticAntiPatternsTests extends ArchUnitFunSpec(
  "Linguistic Anti Patterns",
  "examples",
  `no get function can return Unit`,
  `iser and haser should return booleans`,
  `setters should not return something`
)

object LinguisticAntiPatternsTests {
  // You will have to cast with `asInstanceOf` in several Rules
  // example : .asInstanceOf[GivenMembersConjunction[JavaMethod]] or .asInstanceOf[CodeUnitsShould[CodeUnitsShouldConjunction[JavaMethod]]]

  private val `no get function can return Unit`: ArchRule = emptyRule
    .as("No methods starting with get should retun a Unit / void")

  private val `iser and haser should return booleans`: ArchRule = emptyRule
    .as("No methods starting with is or has should return something else than a Boolean")

  private val `setters should not return something`: ArchRule = emptyRule
    .as("No methods starting with set should return something")
}