package com.gildedrose

import com.github.writethemfirst.approvals.Approvals.verifyAllCombinations
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class GildedRoseTest extends AnyFlatSpec with Matchers {
  it should "update quality of items" in {
    verifyAllCombinations(
      Array[String](
        "a common item",
        "Aged Brie",
        "Backstage passes to a TAFKAL80ETC concert",
        "Sulfuras, Hand of Ragnaros"
      ),
      Array[Integer](-100, -1, 0, 1, 2, 6, 8, 11),
      Array[Integer](-1, 0, 1, 49, 50),
      (name, sellIn, quantity) => updateQuality(name, sellIn, quantity)
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
