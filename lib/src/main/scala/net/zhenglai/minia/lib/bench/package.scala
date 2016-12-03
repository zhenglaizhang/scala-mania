package net.zhenglai.minia.lib

package object bench {
  /*
  call by name params are evaluated each time they are referenced.
  however,there is no language construct to show they are call by name
  (Cpp has &)...
   */
  def time[T](block: => T): (Long, T) = {
    val t0 = System.nanoTime()
    val result = block
    val elapsedMs = (System.nanoTime() - t0) / 1000000
    (elapsedMs, result)
  }
}
