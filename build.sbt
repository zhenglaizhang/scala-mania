name := "scala-mania"

version := "1.0"

scalaVersion := "2.11.8"

val scalazVersion = "7.2.5"


resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % scalazVersion,
  "org.scalaz" %% "scalaz-effect" % scalazVersion,
  //  "org.scalaz" %% "scalaz-typelevel" % scalazVersion,
  "org.scalaz" %% "scalaz-scalacheck-binding" % scalazVersion //% "test"
)

libraryDependencies += "org.typelevel" %% "cats" % "0.7.0"

libraryDependencies ++= Seq(
  "com.chuusai" %% "shapeless" % "2.3.1"
)

// https://mvnrepository.com/artifact/org.scala-lang.modules/scala-async_2.11
libraryDependencies += "org.scala-lang.modules" % "scala-async_2.11" % "0.9.5"

libraryDependencies += "com.typesafe.akka" % "akka-actor_2.11" % "2.4.9-RC2"

libraryDependencies += "com.typesafe" % "config" % "1.3.0"


// akka persistence module
libraryDependencies += "com.typesafe.akka" %% "akka-persistence" % "2.4.9-RC2"

libraryDependencies += "org.iq80.leveldb" % "leveldb" % "0.7"

libraryDependencies += "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8"

libraryDependencies += "joda-time" % "joda-time" % "2.9.4"

libraryDependencies += "org.joda" % "joda-convert" % "1.8"

libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.13.2" // % "test"

libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.0"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.0" % "test"



scalacOptions += "-feature" // -language:higherKinds

initialCommands in console := "import scalaz._, Scalaz._"

initialCommands in console in Test := "import scalaz._, Scalaz._, scalacheck.ScalazProperties._, scalacheck.ScalazArbitrary._,scalacheck.ScalaCheckBinding._"