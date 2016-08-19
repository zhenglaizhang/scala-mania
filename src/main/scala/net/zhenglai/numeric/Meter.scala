package net.zhenglai.numeric

case class Meter(value: Double) extends AnyVal {
  def toFeet: Foot = Foot(value * 0.3048)
}

case class Foot(value: Double) extends AnyVal {
  def toMeter: Meter = Meter(value / 0.3048)
}
