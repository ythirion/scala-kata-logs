import ArchUnitFunSpec.emptyRule
import LayeredArchitectureTests._
import com.tngtech.archunit.lang.ArchRule

class LayeredArchitectureTests extends ArchUnitFunSpec(
  "Layered architecture",
  "..layered..",
  `layered architecture is respected`
)

object LayeredArchitectureTests {
  private val controller = "Controller"
  private val service = "Service"
  private val model = "Model"
  private val dal = "DAL"

  private val `layered architecture is respected`: ArchRule = emptyRule
    .as("Detail your layer architecture here " +
      "Controller can not be accessed by another layer " +
      "Service can only be accessed by Controller" +
      "Dal can only be accessed by Service")
}