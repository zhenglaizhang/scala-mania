package net.zhenglai.concurrent.akka

import akka.actor.ActorSystem

/**
 * Created by zhenglai on 8/18/16.
 */
object AkkaClient {

  private val help = // <19>
    """Usage: AkkaClient [-h | --help]
      |Then, enter one of the following commands, one per line:
      |  h | help      Print this help message.
      |  c n string    Create "record" for key n for value string.
      |  r n           Read record for key n. It's an error if n isn't found.
      |  u n string    Update (or create) record for key n for value string.
      |  d n           Delete record for key n. It's an error if n isn't found.
      |  crash n       "Crash" worker n (to test recovery).
      |  dump [n]      Dump the state of all workers (default) or worker n.
      |  ^d | quit     Quit.
      | """.stripMargin
  private var system: Option[ActorSystem] = None

  def main(args: Array[String]): Unit = {
    processArgs(args)

    val sys = ActorSystem("AkkaClient")
    system = Some(sys)

    val numberOfWorkers = sys.settings.config.getInt("server.number-workers")

  }

  def processArgs(args: Seq[String]): Unit = args match {
    case Nil                                     =>
    case ("-h" | "--help" | "-H" | "-?") +: tail => exit(help, 0)
    case head +: tail                            => exit(s"Unknown input $head!\n $help", 1)
  }

  private def exit(message: String, status: Int): Nothing = {
    for (sys <- system) {
      sys.terminate()
    }
    println(message)
    sys.exit(status)
  }

}
