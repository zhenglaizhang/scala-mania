package net.zhenglai.web

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport
import org.scalajs.jquery.jQuery

import org.scalajs.dom
import dom.document

object WebApp extends JSApp {

  def appendPar(targetNode: dom.Node, text: String): Unit = {
    val parNode = document.createElement("p")
    val textNode = document.createTextNode(text)
    parNode.appendChild(textNode)
    targetNode.appendChild(parNode)
  }

  @JSExport
  def addClickedMessage(): Unit = {
    appendPar(document.body, "You clicked the button!")
  }

  @scala.scalajs.js.annotation.JSExport
  override def main() = {
    println("Hello Scala.js")
    //    appendPar(document.body, "Hello World")
    // or
    jQuery("body").append("<p>[message]</p>")
  }

}
