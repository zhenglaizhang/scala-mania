/*
A traffic light data type
*/

import scalaz._
import Scalaz._

object h11 {
  implicit val TrafficLightEqual: Equal[TrafficLight] = Equal.equal(_ == _)

  sealed trait TrafficLight

  case object Red extends TrafficLight

  case object Green extends TrafficLight

  case object Yellow extends TrafficLight

  /*
  Red === Yellow
  scala> Red === Yellow
  <console>:18: error: could not find implicit value for parameter F0: scalaz.Equal[Product with Serializable with TrafficLight]
                Red === Yellow

  So apparently Equal[TrafficLight] doesn’t get picked up because Equal has nonvariant subtyping: Equal[F]. If I turned TrafficLight to a case class then Red and Yellow would have the same type, but then I lose the tight pattern matching from sealed #fail.
  */
}



case class TrafficLight(name: String)
val red = TrafficLight("red")
val yellow = TrafficLight("yellow")
val green = TrafficLight("green")

implicit val trafficLightEqual: Equal[TrafficLight] = Equal.equal[TrafficLight](_ == _)
red === yellow
