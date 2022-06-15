import OnionArchitectureTests._
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.library.Architectures.onionArchitecture

class OnionArchitectureTests extends ArchUnitFunSpec(
  "Onion architecture",
  "..onion..",
  `onion architecture is respected`
)

object OnionArchitectureTests {
  private val `onion architecture is respected`: ArchRule = onionArchitecture()
    .domainModels("..domain.model..")
    .domainServices("..domain.service..")
    .applicationServices("..application..")
    .adapter("persistence", "..adapter.persistence..")
    .adapter("rest", "..adapter.rest..")
}