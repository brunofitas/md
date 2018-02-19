package com.medidata

import java.time.LocalDate
import com.medidata.util.Inventory.Item
import com.medidata.util.{Discount, Inventory}


class Checkout extends Inventory with Discount {

  import Checkout._

  def loadRequest(req:ServiceRequest): Invoice = {
    Invoice(
      patient = req.patient,
      practitioner = req.practitioner,
      lines = {
        req.services.flatMap{ s =>
          getService(s.code).getOrElse(throw new Exception(s"Invalid service ${s.code}")) ::
            s.extras.map(p => getProduct(p).getOrElse(throw new Exception(s"Invalid product $p")))
        }
      }
    )
  }

  def produceResult(invoice: Invoice): List[(String, BigDecimal, BigDecimal, BigDecimal)] = {
    invoice.lines
      .map{
        line => {

          val afterInsuranceDiscount =
            insuranceDiscount(line, invoice.patient.mhInsurance, invoice.practitioner.mhPractitioner)

          val afterAgeDiscount =
            ageDiscount(afterInsuranceDiscount, getAge(invoice.patient.dateOfBirth))

          ( line.name,
            line.price.setScale(2, BigDecimal.RoundingMode.HALF_UP),
            afterInsuranceDiscount.price.setScale(2, BigDecimal.RoundingMode.HALF_UP),
            afterAgeDiscount.price.setScale(2, BigDecimal.RoundingMode.HALF_UP)
          )
        }
      }
  }


  def getTotal(invoice: Invoice):BigDecimal =
    produceResult(invoice).map(_._4).sum.setScale(2, BigDecimal.RoundingMode.HALF_UP)



  def printInvoice(invoice: Invoice):String = {

    val result = produceResult(invoice)

    val header =s"""
         |MediaHealth
         |
         |Services:                price      -ins      -age""".stripMargin

    val table = result.map( l => {
      val desc =   Array.concat( l._1.split(""), Array.fill(20)(" ").take(20 - l._1.length))
      val price0 = Array.concat( Array.fill(10)(" ").take(10 - l._2.toString.length), l._2.toString.split(""))
      val price1 = Array.concat( Array.fill(10)(" ").take(10 - l._3.toString.length), l._3.toString.split(""))
      val price2 = Array.concat( Array.fill(10)(" ").take(10 - l._4.toString.length), l._4.toString.split(""))

      s"${desc.mkString("")}${price0.mkString("")}${price1.mkString("")}${price2.mkString("")}"

    }).mkString("\n")

    val total = result.map(_._4).sum

    s"""$header
       |${Array.fill(50)("-").mkString("")}
       |${table}
       |${Array.fill(50)("-").mkString("")}
       |${Array.concat( Array.fill(50)(" ").take(50 - s"total: $total".length), s"total: $total".split("")).mkString("")}
     """.stripMargin

  }

}

object Checkout{

  def apply(): Checkout = new Checkout()

  case class Patient(dateOfBirth: LocalDate, mhInsurance: Boolean = false)
  case class Practitioner(mhPractitioner: Boolean = false)

  case class Service(code: String, extras:List[String] = Nil)
  case class ServiceRequest(patient:Patient, practitioner:Practitioner, services:List[Service] )

  case class Invoice(patient:Patient, practitioner:Practitioner, lines:List[Item])

}
