import ArchUnitFunSpec.emptyRule
import MethodsReturnTypesTests.{`command handlers should only return Int`, `controllers public methods should only return ApiResponse[T]`}
import com.tngtech.archunit.base.{DescribedPredicate, PackageMatcher}
import com.tngtech.archunit.core.domain.JavaModifier.PUBLIC
import com.tngtech.archunit.core.domain.properties.HasModifiers
import com.tngtech.archunit.core.domain.properties.HasModifiers.Predicates.modifier
import com.tngtech.archunit.core.domain.{JavaClasses, JavaMember, JavaMethod}
import com.tngtech.archunit.lang._

import java.lang
import scala.collection.JavaConverters._

class MethodsReturnTypesTests extends ArchUnitFunSpec(
  "Constraints on return types",
  "..examples..",
  `command handlers should only return Int`,
  `controllers public methods should only return ApiResponse[T]`)

object MethodsReturnTypesTests {
  // You can use helper methods defined below

  private val `command handlers should only return Int`: ArchRule = emptyRule
    .as("Methods declared in CommandHandler classes can only return Int")

  private val `controllers public methods should only return ApiResponse[T]` = emptyRule
    .as("Public methods declared in Controllers should always return APIResponse[T]")

  def areInPackage(packageMatcher: PackageMatcher): DescribedPredicate[JavaMember] = new DescribedPredicate[JavaMember]("are in " + packageMatcher) {
    override def apply(member: JavaMember): Boolean = packageMatcher.matches(member.getOwner.getPackage.getName)
  }

  def arePublic: DescribedPredicate[HasModifiers] = modifier(PUBLIC).as("are public")

  private def allMethods() = new AbstractClassesTransformer[JavaMethod]("methods") {
    override def doTransform(javaClasses: JavaClasses): lang.Iterable[JavaMethod] = {
      javaClasses.asScala
        .foldLeft(List[JavaMethod]()) { (methods, javaClass) =>
          methods ::: javaClass.getMethods.asScala.toList
        }.asJava
    }
  }

  private def returnType(authorizedTypes: Class[_]*) = new ArchCondition[JavaMethod]("methods should return specified types") {
    override def check(method: JavaMethod, events: ConditionEvents): Unit = {
      val matches = authorizedTypes.exists(method.getRawReturnType.getFullName == _.getName)
      val message = s"${method.getFullName} return type ${method.getRawReturnType} is not authorized"
      events.add(new SimpleConditionEvent(method, matches, message))
    }
  }
}
