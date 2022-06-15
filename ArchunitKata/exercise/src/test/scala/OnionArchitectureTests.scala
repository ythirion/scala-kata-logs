import OnionArchitectureTests.*
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import com.tngtech.archunit.library.Architectures.onionArchitecture
import org.scalatest.funspec.AnyFunSpec

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