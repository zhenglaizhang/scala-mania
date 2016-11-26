package net.zhenglai.entity

/**
 * Created by zhenglai on 8/16/16.
 */

object Address {

  def apply(zip: String) = //
    new Address(
      "[unknown]", Address.zipToCity(zip), Address.zipToState(zip), zip
    )

  def zipToCity(zip: String) = "Anytown"

  def zipToState(zip: String) = "CA"
}

case class Address(street: String, city: String, state: String, zip: String) {
  def this(zip: String) =
    this("[unknown]", Address.zipToCity(zip), Address.zipToState(zip), zip)
}

