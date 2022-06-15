import ArchUnitFunSpec.emptyRule
import GuidelineTests._
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.library.GeneralCodingRules
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices

class GuidelineTests extends ArchUnitFunSpec(
  "Our team guidelines",
  "examples",
  `traits should not contain big I`,
  `classes in domain can only access classes in domain itself`,
  `no classes should depend on another`,
  `classes should reside in a given package`,
  `ensure no cycle dependencies`,
  GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS,
  GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION,
  GeneralCodingRules.NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING,
  GeneralCodingRules.NO_CLASSES_SHOULD_USE_JODATIME,
  GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS
)

object GuidelineTests {
  private val domainPackages = List("..domain..")
  private val systemPackages = List("java..", "scala..")

  private val `no classes should depend on another`: ArchRule = emptyRule
    .as("Class with name SomeExample should not depend on Other")

  private val `traits should not contain big I`: ArchRule = emptyRule
    .as("Interfaces should not start with a I because we are not in C#")

  private val `classes should reside in a given package`: ArchRule = emptyRule
    .as("Classes with name like *Repository* should be interfaces and in a package named repository or repositories")

  private val `classes in domain can only access classes in domain itself`: ArchRule = emptyRule
    .as("Domain should only have dependencies on Domain itself")

  private val `ensure no cycle dependencies`: ArchRule =
    slices.matching(".(*)..")
      .should()
      .beFreeOfCycles()
}