package com.medidata


import java.time.LocalDate

import com.medidata.Checkout.{Patient, Practitioner, Service, ServiceRequest}
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}


class CheckoutTests extends FlatSpec with Matchers with BeforeAndAfter {

  var checkout:Checkout = _

  val patient = Patient(LocalDate.of(1940,10,11), mhInsurance = true)
  val practitioner = Practitioner(mhPractitioner = true)

  before{
    checkout = Checkout()
  }

  "loadRequest" must "load a service request and transform it into an Invoice" in {
    val result = checkout.loadRequest(ServiceRequest(patient,practitioner, List(
      Service("SRV_DIAGNOSIS"),
      Service("SRV_X_RAY"),
      Service("SRV_BLOOD_TEST"),
      Service("SRV_VACCINE", extras = List("PRO_VACCINE_1","PRO_VACCINE_2")),
      Service("SRV_X_RAY")
    )))

    assert(result.lines.nonEmpty)
    assert(result.lines.lengthCompare(7) == 0)
  }

  it must "assure that the service exists" in {
    intercept[Exception] {
      checkout.loadRequest(ServiceRequest(patient, practitioner, List(Service("FAKE_SERVICE"))))
    }
  }

  it must "assure that no products are requested without a service" in {
    intercept[Exception] {
      checkout.loadRequest(ServiceRequest(patient, practitioner, List(Service("PRO_VACCINE_1"))))
    }
  }


  "getTotal" must "must not must not apply any discount on a 36 years old person and without insurance" in {
    val request = checkout.loadRequest(ServiceRequest(
        patient.copy(dateOfBirth= LocalDate.of(1981,11, 10), mhInsurance = false),
        practitioner,
        List(Service("SRV_BLOOD_TEST"))
    ))
    checkout.getTotal(request) shouldBe BigDecimal(78.00)

    val request2 = checkout.loadRequest(ServiceRequest(
        patient.copy(dateOfBirth= LocalDate.of(1981,11, 10), mhInsurance = false),
        practitioner,
        List(
          Service("SRV_X_RAY"),
          Service("SRV_BLOOD_TEST")
        )
    ))
    checkout.getTotal(request2) shouldBe BigDecimal(78.00) + 150.00
  }

  it must "apply a 15% discount on blood tests when the person has insurance " +
    "and has been diagnosed by a MediaHealth practitioner" in {
    val request = checkout.loadRequest(ServiceRequest(
      patient.copy(dateOfBirth= LocalDate.of(1981,11, 10), mhInsurance = true),
      practitioner.copy(mhPractitioner = true),
      List(Service("SRV_BLOOD_TEST"))
    ))
    checkout.getTotal(request) shouldBe BigDecimal(78.00) - 78.00 * 15 / 100
  }

  it must "not apply discount on blood tests when the person has insurance " +
    "and hasn't been diagnosed by a MediaHealth practitioner" in {
    val request = checkout.loadRequest(ServiceRequest(
      patient.copy(dateOfBirth= LocalDate.of(1981,11, 10), mhInsurance = true),
      practitioner.copy(mhPractitioner = false),
      List(Service("SRV_BLOOD_TEST"))
    ))
    checkout.getTotal(request) shouldBe 78.00
  }

  it must "apply a 90% discount on people over 70" in {
    val request = checkout.loadRequest(ServiceRequest(
      patient.copy(dateOfBirth= LocalDate.of(1940,11, 10)),
      practitioner,
      List(Service("SRV_ECG"))
    ))
    checkout.getTotal(request) shouldBe BigDecimal(200.40) - 200.40 * 90 / 100
  }

  it must "apply a 90% discount on people over 70, plus 15% on blood tests with insurance" in {
    val request = checkout.loadRequest(ServiceRequest(
      patient.copy(dateOfBirth= LocalDate.of(1940,11, 10), mhInsurance = true),
      practitioner.copy(mhPractitioner = true),
      List(
        Service("SRV_ECG"),
        Service("SRV_BLOOD_TEST")
      )
    ))

    val totalPreAgeDiscount = BigDecimal(200.40) + 78.00 - 78.00 * 15 / 100
    checkout.getTotal(request) shouldBe totalPreAgeDiscount - totalPreAgeDiscount * 90 / 100
  }

  it must "apply a 60% discount on people between 65 and 70" in {
    val request = checkout.loadRequest(ServiceRequest(
      patient.copy(dateOfBirth= LocalDate.of(1953,1,1)),
      practitioner,
      List(Service("SRV_ECG"))
    ))
    checkout.getTotal(request) shouldBe BigDecimal(200.40) - 200.40 * 60 / 100
  }

  it must "apply a 40% discount on people between 0 and 5" in {
    val request = checkout.loadRequest(ServiceRequest(
      patient.copy(dateOfBirth= LocalDate.of(2018,1,1)),
      practitioner,
      List(Service("SRV_ECG"))
    ))
    checkout.getTotal(request) shouldBe BigDecimal(200.40) - 200.40 * 40 / 100
  }

  it must "throw an exception when the patient age is wrong" in {
    val request = checkout.loadRequest(ServiceRequest(
      patient.copy(dateOfBirth= LocalDate.of(2028,1,1)),
      practitioner,
      List(Service("SRV_ECG"))
    ))
    intercept[Exception] {
      checkout.getTotal(request)
    }
  }


  "printInvoice" must "print an invoice" in {
    val result = checkout.loadRequest(ServiceRequest(patient,practitioner, List(
      Service("SRV_DIAGNOSIS"),
      Service("SRV_X_RAY"),
      Service("SRV_ECG"),
      Service("SRV_BLOOD_TEST"),
      Service("SRV_VACCINE", extras = List("PRO_VACCINE_1","PRO_VACCINE_2")),
      Service("SRV_X_RAY")
    )))
    println(checkout.printInvoice(result))

    checkout.printInvoice(result) shouldBe a [String]
  }

}
