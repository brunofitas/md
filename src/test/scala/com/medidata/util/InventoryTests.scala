package com.medidata.util

import com.medidata.util.Inventory.{Item, ItemType}
import org.scalatest.{FlatSpec, Matchers}

class InventoryTests extends FlatSpec with Matchers {

  new {} with Inventory {

    "inventory" must "return a list of tupples with all available items" in {
      assert(inventory.nonEmpty)
      assert(inventory.exists(_._1 == "SRV_X_RAY"))
      assert(!inventory.exists(_._1 == "FAKE"))
    }

    "toItem" must "convert tupple to Item" in {
      val t = ("SRV_A", "Service A", BigDecimal(10.0), ItemType.SERVICE )
      Inventory.toItem(t) shouldBe a [Item]
      Inventory.toItem(t) shouldBe Item("SRV_A", "Service A", BigDecimal(10.0), ItemType.SERVICE )
    }

    "getAll" must "return a list of all Items in the collection" in {
      getAll shouldBe a [List[_]]
      assert(getAll.lengthCompare(inventory.size) == 0)
    }

    "getServices" must "return all Services from the collection" in {
      getServices shouldBe a [List[_]]
      assert(getServices.lengthCompare( inventory.count(_._4 == ItemType.SERVICE)) == 0)
    }

    "getProducts" must "return all Products from the collection" in {
      getProducts shouldBe a [List[_]]
      assert(getProducts.lengthCompare(inventory.count(_._4 == ItemType.PRODUCT)) == 0)
    }

    "getItem" must "return a single Item" in {
      assert(getItem("SRV_X_RAY").nonEmpty)
      assert( getItem("FAKE").isEmpty)
    }

  }
}
