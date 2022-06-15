import GuidelineTests.*
import com.tngtech.archunit.core.domain.{JavaCodeUnit, JavaMember, JavaMethod}
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.{classes, fields, noClasses}
import com.tngtech.archunit.lang.syntax.elements.*
import com.tngtech.archunit.lang.{ArchCondition, ArchRule, ConditionEvents, SimpleConditionEvent}
import com.tngtech.archunit.library.GeneralCodingRules
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices
import org.scalatest.funspec.AnyFunSpec

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

  private val `classes in domain can only access classes in domain itself`: ArchRule =
    restrictClassDependencyOnItselfAndOnAuthorizedPackages(domainPackages)
      .as("Domain should only have dependencies on Domain itself")

  private val `no classes should depend on another`: ArchRule =
    noClasses()
      .that().haveSimpleName("SomeExample")
      .should()
      .dependOnClassesThat().haveSimpleName("Other")

  // Something should reside in a given package
  // Can be done on areAnnotatedWith, areAssignableFrom / areAssignableTo, ...
  // For example : classes annotated with @Controller in package controller .resideInAnyPackage("..controller..", "..controllers..)"
  private val `classes should reside in a given package`: ArchRule =
  classes.that.haveSimpleNameContaining("Repository")
    .should.resideInAnyPackage("..repository..", "..repositories..")
    .andShould()
    .beInterfaces()
    .as("Repositories should be in Repository package")

  private val `ensure no cycle dependencies`: ArchRule =
    slices.matching(".(*)..")
      .should()
      .beFreeOfCycles()

  private val `traits should not contain big I`: ArchRule =
    noClasses()
      .that().areInterfaces()
      .should()
      .haveNameMatching("^.*[.]I[A-Z].*")
      .because("Not in C#")

  private def restrictClassDependencyOnItselfAndOnAuthorizedPackages(classesInPackages: List[String],
                                                                     authorizedPackages: List[String] = List.empty): ArchRule =
    classes()
      .that().resideInAnyPackage(classesInPackages: _*)
      .should()
      .onlyDependOnClassesThat()
      .resideInAnyPackage(classesInPackages ::: authorizedPackages ::: systemPackages: _*)
}