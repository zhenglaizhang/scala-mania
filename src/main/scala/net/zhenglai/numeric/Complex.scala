package net.zhenglai.numeric

/**
 * Created by zhenglai on 8/16/16.
 */
case class Complex(real: Double, imag: Double) {

  def unary_- : Complex = Complex(-real, imag)

  // testing purpose only
  def unary_! : Complex = Complex(-real, -imag)

  def -(other: Complex) = Complex(real - other.real, imag - other.imag)

}
