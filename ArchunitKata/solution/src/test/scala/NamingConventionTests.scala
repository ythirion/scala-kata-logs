import NamingConventionTests.*
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import examples.CommandHandler

class NamingConventionTests extends ArchUnitFunSpec(
  "Naming convention",
  "examples",
  `services should be suffixed by Service`,
  `command handler should be suffixed by CommandHandler`
)

object NamingConventionTests {
  private val `services should be suffixed by Service`: ArchRule =
    classes()
      .that().resideInAPackage("..service..")
      .should().haveSimpleNameEndingWith("Service")

  private val `command handler should be suffixed by CommandHandler`: ArchRule =
    classes()
      .that()
      .implement(classOf[CommandHandler])
      .should().haveSimpleNameEndingWith("CommandHandler")
}