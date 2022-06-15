import ArchUnitFunSpec.emptyRule
import NamingConventionTests._
import com.tngtech.archunit.lang.ArchRule

class NamingConventionTests extends ArchUnitFunSpec(
  "Naming convention",
  "examples",
  `services should be suffixed by Service`,
  `command handler should be suffixed by CommandHandler`
)

object NamingConventionTests {
  private val `services should be suffixed by Service`: ArchRule = emptyRule
    .as("All the Service class should be the Service package")

  private val `command handler should be suffixed by CommandHandler`: ArchRule = emptyRule
      .as("All the classes implementing CommandHandler should contain CommandHandler in their name")
}