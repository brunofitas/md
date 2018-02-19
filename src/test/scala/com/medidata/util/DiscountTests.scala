package com.medidata.util

import java.time.{LocalDate, Period}

import com.medidata.util.Inventory.Item
import com.medidata.util.Inventory.ItemType.SERVICE
import org.scalatest.{FlatSpec, Matchers}


class DiscountTests extends FlatSpec with Matchers{

  new {} with Discount{

    "getAge" must "return an age from a date" in{
      assert(getAge(LocalDate.now().minus(Period.ofYears(88))) == 88)
      assert(getAge(LocalDate.now().minus(Period.ofYears(0))) == 0)
      assert(getAge(LocalDate.now().plus(Period.ofYears(10))) == -10)
    }

    "getAgeDiscount" must "return a suitable discount for every age" in {

      assert(getAgeDiscount(88) == 90)
      assert(getAgeDiscount(71) == 90)
      assert(getAgeDiscount(70) == 90)
      assert(getAgeDiscount(69) == 60)
      assert(getAgeDiscount(66) == 60)
      assert(getAgeDiscount(64) == 0)
      assert(getAgeDiscount(44) == 0)
      assert(getAgeDiscount(34) == 0)
      assert(getAgeDiscount(5) == 0)
      assert(getAgeDiscount(4) == 40)
      assert(getAgeDiscount(3) == 40)
      assert(getAgeDiscount(0) == 40)

      intercept[Exception] {
        getAgeDiscount(-10)
      }
    }

    "ageDiscount" must "return an updated Item price, according to age" in {
      assert(ageDiscount(Item("A","A", 100.00, SERVICE), 90) == Item("A","A", 10.00, SERVICE))
      assert(ageDiscount(Item("A","A", 200.40, SERVICE), 90) == Item("A","A", 20.04, SERVICE))
      assert(ageDiscount(Item("A","A", 100.00, SERVICE), 70) == Item("A","A", 10.00, SERVICE))
      assert(ageDiscount(Item("A","A", 100.00, SERVICE), 69) == Item("A","A", 40.00, SERVICE))
      assert(ageDiscount(Item("A","A", 100.00, SERVICE), 66) == Item("A","A", 40.00, SERVICE))
      assert(ageDiscount(Item("A","A", 100.00, SERVICE), 65) == Item("A","A", 40.00, SERVICE))
      assert(ageDiscount(Item("A","A", 100.00, SERVICE), 64) == Item("A","A", 100.00, SERVICE))
      assert(ageDiscount(Item("A","A", 100.00, SERVICE), 36) == Item("A","A", 100.00, SERVICE))
      assert(ageDiscount(Item("A","A", 100.00, SERVICE), 5)  == Item("A","A", 100.00, SERVICE))
      assert(ageDiscount(Item("A","A", 100.00, SERVICE), 4)  == Item("A","A", 60.00, SERVICE))
      assert(ageDiscount(Item("A","A", 100.00, SERVICE), 0)  == Item("A","A", 60.00, SERVICE))

      intercept[Exception] {
        ageDiscount(Item("A", "A", 100.00, SERVICE), -10)
      }
    }

    "insuranceDiscount" must "update discounted Items only when a patient " +
      "has insurance and comes from a media health practitioner" in {

      insuranceDiscount(
        Item("SRV_BLOOD_TEST", "Blood test", 78.00, SERVICE), insured = true, mhPractitioner = true
      ).price shouldBe 78.00 - 78.00 * 15 / 100

      insuranceDiscount(
        Item("SRV_BLOOD_TEST", "Blood test", 78.00, SERVICE), insured = false, mhPractitioner = true
      ).price shouldBe 78.00

      insuranceDiscount(
        Item("SRV_BLOOD_TEST", "Blood test", 78.00, SERVICE), insured = true, mhPractitioner = false
      ).price shouldBe 78.00

      insuranceDiscount(
        Item("NO_PROMOTION", "NO_PROMOTION", 78.00, SERVICE), insured = true, mhPractitioner = true
      ).price shouldBe 78.00

    }

  }
}
