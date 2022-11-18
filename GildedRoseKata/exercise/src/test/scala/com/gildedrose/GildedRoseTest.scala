package com.gildedrose

import org.approvaltests.Approvals
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class GildedRoseTest extends AnyFlatSpec with Matchers {
  it should "update quality of a common item with approval" in {
    val items = Array[Item] {
      new Item("a common item", 0, 0)
    }
    val gildedRose = new GildedRose(items)

    gildedRose.updateQuality()

    Approvals.verify(gildedRose.items(0))
  }
}
