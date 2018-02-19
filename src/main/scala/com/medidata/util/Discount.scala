package com.medidata.util

import java.time.LocalDate
import java.time.temporal.ChronoUnit.YEARS
import com.medidata.util.Inventory.Item


trait Discount {

  protected val insuredItems: List[(String, BigDecimal)] =
    List(("SRV_BLOOD_TEST", 15.00))


  protected val getAgeDiscount: (Long) => BigDecimal = {
      case a if a >= 70             => 90.00
      case a if a >= 65 && a < 70   => 60.00
      case a if a >= 5  && a < 65   => 0.00
      case a if a <  5  && a >= 0   => 40.00
      case _                        => throw new Exception("Invalid age")
    }


  def getAge(dateOfBirth:LocalDate): Long =
    YEARS.between(dateOfBirth, LocalDate.now())


  def ageDiscount(item:Item, age: Long): Item =
    item.copy(price = item.price - getAgeDiscount(age) * item.price / 100)


  def insuranceDiscount(item:Item, insured:Boolean, mhPractitioner: Boolean): Item = {
    insuredItems.find(_._1 == item.code) match {
      case Some(i) =>
        if(insured && mhPractitioner)
          item.copy(price = item.price - (item.price * i._2) / 100)
        else item

      case None => item
    }

  }

}

