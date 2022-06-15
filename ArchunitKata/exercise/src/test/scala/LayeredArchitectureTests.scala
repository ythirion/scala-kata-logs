import LayeredArchitectureTests.*
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.priority
import com.tngtech.archunit.lang.{ArchRule, Priority}
import com.tngtech.archunit.library.Architectures.{layeredArchitecture, onionArchitecture}

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

  private val `layered architecture is respected`: ArchRule = layeredArchitecture()
    .layer(controller).definedBy("..controller..")
    .layer(service).definedBy("..service..")
    .layer(model).definedBy("..model..")
    .layer(dal).definedBy("..repository..")
    .whereLayer(controller).mayNotBeAccessedByAnyLayer()
    .whereLayer(service).mayOnlyBeAccessedByLayers(controller)
    .whereLayer(dal).mayOnlyBeAccessedByLayers(service)
    .as("We should respect our Layer definition")
}