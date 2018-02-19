package com.medidata.util


trait Inventory {

  import Inventory.ItemType._
  import Inventory._

  protected val inventory : List[(String,String,BigDecimal,ItemType.Value)] =
    List(
      ("SRV_DIAGNOSIS", "Diagnosis", 60.00, SERVICE),
      ("SRV_X_RAY", "X-Ray", 150.00, SERVICE),
      ("SRV_BLOOD_TEST", "Blood test", 78.00, SERVICE),
      ("SRV_ECG", "ECG", 200.40, SERVICE),
      ("SRV_VACCINE", "Vaccination", 27.50, SERVICE),
      ("PRO_VACCINE_1", "Vaccine 1", 15.00, PRODUCT),
      ("PRO_VACCINE_2", "Vaccine 2", 15.00, PRODUCT)
    )

  def getAll:List[Item] =
    inventory.map(toItem)

  def getItem(code:String):Option[Item] =
    inventory.find(_._1 == code).map(toItem)

  def getServices:List[Item] =
    inventory.filter(_._4 == SERVICE).map(toItem)

  def getService(code:String):Option[Item] =
    inventory.filter(_._4 == SERVICE).find(_._1 == code).map(toItem)

  def getProducts: List[Item] =
    inventory.filter(_._4 == PRODUCT).map(toItem)

  def getProduct(code:String):Option[Item] =
    inventory.filter(_._4 == PRODUCT).find(_._1 == code).map(toItem)

}

object Inventory {

  object ItemType extends Enumeration{
    val SERVICE: Value  = Value("SERVICE")
    val PRODUCT: Value  = Value("PRODUCT")
  }

  case class Item(code:String, name:String, price:BigDecimal, itemType:ItemType.Value)

  def toItem: ((String, String, BigDecimal, ItemType.Value)) => Item =
    ( x:(String, String, BigDecimal, ItemType.Value)) => (Item.apply _).tupled(x)


}
