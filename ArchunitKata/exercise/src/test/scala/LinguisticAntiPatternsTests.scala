import LinguisticAntiPatternsTests.*
import com.tngtech.archunit.core.domain.JavaMethod
import com.tngtech.archunit.lang.*
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.{methods, priority}
import com.tngtech.archunit.lang.syntax.elements.{CodeUnitsShould, CodeUnitsShouldConjunction, GivenMembersConjunction}

class LinguisticAntiPatternsTests extends ArchUnitFunSpec(
  "Linguistic Anti Patterns",
  "examples",
  `no get function can return Unit`,
  `iser and haser should return booleans`,
  `setters should not return something`
)

object LinguisticAntiPatternsTests {
  private val `no get function can return Unit`: ArchRule =
    priority(Priority.HIGH)
      .methods()
      .that().haveNameMatching("get[A-Z].*")
      .asInstanceOf[GivenMembersConjunction[JavaMethod]]
      .should(notBeVoid)

  private val `iser and haser should return booleans`: ArchRule =
    priority(Priority.HIGH)
      .methods()
      .that().haveNameMatching("is[A-Z].*")
      .asInstanceOf[GivenMembersConjunction[JavaMethod]]
      .or().haveNameMatching("has[A-Z].*")
      .should()
      .asInstanceOf[CodeUnitsShould[CodeUnitsShouldConjunction[JavaMethod]]]
      .haveRawReturnType(classOf[Boolean])

  private val `setters should not return something`: ArchRule =
    priority(Priority.HIGH)
      .methods()
      .that().haveNameMatching("set[A-Z].*")
      .asInstanceOf[GivenMembersConjunction[JavaMethod]]
      .should()
      .asInstanceOf[CodeUnitsShould[CodeUnitsShouldConjunction[JavaMethod]]]
      .haveRawReturnType("void")

  def notBeVoid: ArchCondition[JavaMethod] = new ArchCondition[JavaMethod]("not return void") {
    override def check(method: JavaMethod, events: ConditionEvents): Unit = {
      val matches = !("void" == method.getRawReturnType.getName)
      val message = s"${method.getFullName} returns ${method.getRawReturnType.getName}"
      events.add(new SimpleConditionEvent(method, matches, message))
    }
  }
}