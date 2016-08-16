package net.zhenglai.numeric

/**
  * Created by zhenglai on 8/16/16.
  */
class Dollar(val value: Float) extends AnyVal {

  override def toString = "$%.2f".format(value)

}

