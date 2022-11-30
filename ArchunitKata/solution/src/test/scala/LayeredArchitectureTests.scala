import LayeredArchitectureTests._
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.library.Architectures.layeredArchitecture

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
    .consideringAllDependencies()
    .layer(controller).definedBy("..controller..")
    .layer(service).definedBy("..service..")
    .layer(model).definedBy("..model..")
    .layer(dal).definedBy("..repository..")
    .whereLayer(controller).mayNotBeAccessedByAnyLayer()
    .whereLayer(service).mayOnlyBeAccessedByLayers(controller)
    .whereLayer(dal).mayOnlyBeAccessedByLayers(service)
    .as("We should respect our Layer definition")
}