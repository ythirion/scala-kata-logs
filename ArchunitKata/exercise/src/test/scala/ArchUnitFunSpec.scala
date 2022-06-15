import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.scalatest.funspec.AnyFunSpec

abstract class ArchUnitFunSpec(private val name: String,
                               private val packages: String,
                               private val rules: ArchRule*) extends AnyFunSpec {
  describe(name) {
    val classes = new ClassFileImporter().importPackages(packages)

    rules.foreach { rule =>
      it(rule.getDescription) {
        rule.check(classes)
      }
    }
  }
}

object ArchUnitFunSpec {
  val emptyRule: ArchRule = noClasses()
    .should()
    .be("Dummy")
}