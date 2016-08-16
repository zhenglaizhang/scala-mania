package net.zhenglai.util.file

/**
  * Created by zhenglai on 8/16/16.
  */
object RemoveBlanks {


  def apply(path: String, compressWhiteSpace: Boolean = false): Seq[String] = {
    for {
      line <- scala.io.Source.fromFile(path).getLines.toSeq
      if !line.matches("""^\s*$""")
      line2 = if (compressWhiteSpace) line replaceAll("\\s+", " ") else line
    } yield line2
  }

}
