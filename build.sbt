name := "scala-mania"

version := "1.0"

scalaVersion := "2.11.8"



libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.2.5"

libraryDependencies ++= Seq(
  "com.chuusai" %% "shapeless" % "2.3.1"
)

// https://mvnrepository.com/artifact/org.scala-lang.modules/scala-async_2.11
libraryDependencies += "org.scala-lang.modules" % "scala-async_2.11" % "0.9.5"


resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"


libraryDependencies +=
  "com.typesafe.akka" % "akka-actor_2.11" % "2.4.9-RC2"

libraryDependencies += "com.typesafe" % "config" % "1.3.0"

