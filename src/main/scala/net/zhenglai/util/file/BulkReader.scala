package net.zhenglai.util.file

/**
 * Created by zhenglai on 8/16/16.
 * Traits can inherit classes
 * the default parent is `AnyRef` if not specified
 */
abstract class BulkReader {
  type In
  val source: In

  def read: String

}

class StringBulkReader(val source: String) extends BulkReader {
  type In = String

  def read: String = source
}

class FileBulkReader(val source: java.io.File) extends BulkReader {
  type In = java.io.File

  def read: String = ???
}