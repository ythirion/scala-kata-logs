import CyclesTests.`ensure no cycle dependencies`
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition

class CyclesTests extends ArchUnitFunSpec(
  "Free of cycles ?",
  "..cycles..",
  `ensure no cycle dependencies`)

object CyclesTests {
  private val `ensure no cycle dependencies`: ArchRule =
    SlicesRuleDefinition.slices()
      .matching("..(cycles).(*)..")
      .should().beFreeOfCycles()
}