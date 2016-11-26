import scala.collection.mutable
import scala.language.dynamics

/*
Scala allows us to have Dynamic Types, right inside of a Staticly/Strictly Typed language!
 */

val jsonString =
  """
{
    "name": "Konrad",
    "favLangs": ["Scala", "Go", "SML"]
}"""
val json = new Json(jsonString)

def parse(s: String) = {
  Map("name" -> "name-value", "favLangs" -> "Chinese")
}

class Json(s: String) extends Dynamic {
  def selectDynamic(name: String): Option[String] =
    parse(s).get(name)
}

//val name: Option[String] = json.name

new Json(jsonString).name
new Json(jsonString).favLangs
new Json(jsonString).notexists

object Json extends Dynamic {
  def applyDynamicNamed(name: String)(args: (String, Any)*) = {
    args.foreach(arg => {
      println(
        s"""
                  |Creating a $name, for:\n ${arg._1}":"${arg._2}"
       """.stripMargin
      )

    })
  }
}

Json.node(nickname = "nick", fullName = "Zhenglai Zhang")

object OhMy extends Dynamic {
  def applyDynamic(methodName: String)(args: Any*): Unit = {
    println(
      s"""
                |methodName: $methodName
                |args: ${args.mkString(",")}
       """.stripMargin
    )
  }
}

/*
So the signature of applyDynamic takes the method name and it’s arguments. So obviously we’d have to access them by their order. Very nice for building up some strings etc.
 */
OhMy.dynamicMethod("with", "some", 1337)

object MagicBox extends Dynamic {
  private var box = mutable.Map[String, Any]()

  def updateDynamic(name: String)(value: Any) = {
    box(name) = value
  }

  def selectDynamic(name: String) = box(name)
}

/*
trait Dynamic, does absolutely nothing by itself - it’s "empty", just a marker interface. Obviously all the heavylifting (call-site-rewriting) is done by the compiler here
 */
MagicBox.banana = "banananananna"
MagicBox.banana
MagicBox.newVal = "test"
MagicBox.newVal

// java.util.NoSuchElementException: key not found: unknown
//MagicBox.unknown
