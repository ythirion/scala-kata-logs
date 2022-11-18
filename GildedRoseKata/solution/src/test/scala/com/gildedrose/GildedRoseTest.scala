package com.gildedrose

import org.approvaltests.combinations.CombinationApprovals.verifyAllCombinations
import org.approvaltests.core.ApprovalFailureReporter
import org.approvaltests.reporters.{QuietReporter, UseReporter}
import org.junit.jupiter.api.Test

@UseReporter(Array[ApprovalFailureReporter](classOf[QuietReporter]))
class GildedRoseTest {
  @Test def update_quality_of_items: Unit = {
    verifyAllCombinations(
      (name, sellIn, quantity) => updateQuality(name, sellIn, quantity),
      Array[String](
        "a common item",
        "Aged Brie",
        "Backstage passes to a TAFKAL80ETC concert",
        "Sulfuras, Hand of Ragnaros"
      ),
      Array[Integer](-100, -1, 0, 1, 2, 6, 8, 11),
      Array[Integer](-1, 0, 1, 49, 50)
    )
  }

  private def updateQuality(
      name: String,
      sellIn: Integer,
      quality: Integer
  ): String = {
    val items = List(new Item(name, sellIn, quality))
    val gildedRose = new GildedRose(items.toArray)
    gildedRose.updateQuality()
    gildedRose.items(0).toString
  }
}
