name in Global := "scala-mania"

organization in Global := "net.zhengla"

version in Global := "1.0"

scalaVersion in Global := "2.11.8"

licenses := Seq(("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0")))
homepage := scmInfo.value map (_.browseUrl)
scmInfo := Some(ScmInfo(
  url("https://github.com/zhenglaizhang/scala-mania"),
  "scm:git:git@github.com:zhenglaizhang/scala-mania.git")
)
developers := List(
  Developer("zhenglaizhang", "Zhenglai Zhang", "zhenglaizhang@hotmail.com", url("http://zhenglaizhang.net"))
)


// deps versions
val AKKA_VERSION = "2.4.14"
val AKKA_HTTP_VERSION = "10.0.0"
val SCALAZ_VERSION = "7.2.5"
val CATS_VERSION = "0.7.0"

lazy val root = project.in(file("."))
  .settings(name := (name in Global).value)
  .settings(akkaActorDeps)
  .settings(akkaHttpDeps)
  .settings(scalazDeps)
  .settings(commonScalacOptions)
  .aggregate(lib, ws, crawler, doc)

lazy val lib = project
  .settings(commonScalacOptions)
  .settings(testCommonDeps)

lazy val ws = project
  .settings(commonScalacOptions)

lazy val crawler = project
  .settings(commonScalacOptions)

lazy val doc = project

// https://mvnrepository.com/artifact/org.scala-lang.modules/scala-async_2.11

resolvers in Global ++= Seq(
  Resolver.mavenLocal,
  "twitter-repo" at "http://maven.twttr.com",
  //  "typesafe-repo" at "http://repo.typesafe.com/typesafe/releases/"
  Resolver.typesafeRepo("release")
)

lazy val commonScalacOptions = Seq(
  scalacOptions ++= Seq(
    "-deprecation"
    , "-encoding", "UTF-8"
    //    , "-feature" // Emit warning and location for usages of features that should be imported explicitly
    , "language:_"
    , "target:jvm-1.8"
    //    , "-unchecked" // Enable additional warnings where generated code depends on assumptions
    ////    , "-Xfatal-warnings"
    //    , "-Xfuture" // Turn on future language features
    //    , "-Xlint" // Enable specific warnings (see `scalac -Xlint:help`)
    //    , "-Xcheckinit" // Check init specific warnings
    //    , "-Yno-adapted-args" // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver
    //    //  , "-Ylog-classpath" // Enable problematic classpath logging
    //    , "-Ywarn-dead-code" // Warn when dead code is identified
    //    , "-Ywarn-inaccessible" // Warn about inaccessible types in method signatures
    //    , "-Ywarn-infer-any" // Warn when a type argument is inferred to be `Any`
    //    , "-Ywarn-nullary-override" // Warn when non-nullary `def f()' overrides nullary `def f'
    //    , "-Ywarn-nullary-unit" // Warn when nullary methods return Unit
    //    , "-Ywarn-numeric-widen" // Warn when numerics are widened
    //    , "-Ywarn-unused" // Warn when local and private vals, vars, defs, and types are unused
    //    , "-Ywarn-unused-import" // Warn when imports are unused
    //    , "-Ywarn-value-discard" // Warn when non-Unit expression results are unused
  )
)

lazy val commonJavacOptions = Seq()

lazy val catsDependencySettings = libraryDependencies ++= Seq(
  "org.typelevel" %% "cats" % CATS_VERSION
)

lazy val shapelessDependencySettings = libraryDependencies ++= Seq(
  "com.chuusai" %% "shapeless" % "2.3.1"
)

lazy val scalazDeps = libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % SCALAZ_VERSION,
  "org.scalaz" %% "scalaz-effect" % SCALAZ_VERSION,
  //  "org.scalaz" %% "scalaz-typelevel" % scalazVersion,
  "org.scalaz" %% "scalaz-scalacheck-binding" % SCALAZ_VERSION //% "test"
)


lazy val akkaActorDeps = Seq(
  libraryDependencies ++= {
    Seq(
      "com.typesafe.akka"
    ).flatMap { group =>
      Seq(
        "akka-actor",
        "akka-agent",
        "akka-camel",
        "akka-cluster",
        "akka-cluster-metrics",
        "akka-cluster-sharding",
        "akka-cluster-tools",
        "akka-contrib",
        "akka-multi-node-testkit",
        "akka-osgi",
        "akka-persistence",
        "akka-persistence-tck",
        "akka-remote",
        "akka-slf4j",
        "akka-stream",
        "akka-stream-testkit",
        "akka-testkit",
        "akka-distributed-data-experimental",
        "akka-typed-experimental",
        "akka-persistence-query-experimental"
      ).map(group %% _ % AKKA_VERSION)
    }
  }
)

lazy val akkaHttpDeps = Seq(
  libraryDependencies ++= Seq(
    "com.typesafe.akka"
  ) flatMap { group =>
    Seq(
      "akka-http-core",
      "akka-http",
      "akka-http-testkit",
      "akka-http-spray-json",
      "akka-http-jackson",
      "akka-http-xml"
    ).map(group %% _ % AKKA_HTTP_VERSION)
  }
)

lazy val testAkkaDeps = libraryDependencies ++= {
  Seq(
    "com.typesafe.akka" %% "akka-testkit" % AKKA_VERSION % "test",
    "com.typesafe.akka" %% "akka-http-testkit" % AKKA_VERSION % "test"
  )
}

lazy val testCommonDeps = libraryDependencies ++= {
  Seq(
    "org.scalacheck" %% "scalacheck" % "1.13.2" % "test",
    "org.scalatest" %% "scalatest" % "3.0.0" % "test"
  )
}

// finagle
lazy val finageDeps = Nil

// code formatting
import scalariform.formatter.preferences._
import com.typesafe.sbt.SbtScalariform
import com.typesafe.sbt.SbtScalariform.ScalariformKeys

SbtScalariform.scalariformSettings

ScalariformKeys.preferences := ScalariformKeys.preferences.value
  .setPreference(AlignParameters, true)
  .setPreference(AlignArguments, true)
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(DoubleIndentClassDeclaration, true)
  .setPreference(DanglingCloseParenthesis, Force)

includeFilter in scalariformFormat := "*.scala" || "*.sc" || "*.sbt"

libraryDependencies += "org.scala-lang.modules" % "scala-async_2.11" % "0.9.5"

libraryDependencies += "com.typesafe" % "config" % "1.3.0"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.3"

/*
Akka provides a logger for SL4FJ. This module is available in the 'akka-slf4j.jar'. It has one single dependency; the slf4j-api jar. In runtime you also need a SLF4J backend, we recommend Logback:
 */
//lazy val logback = "ch.qos.logback" % "logback-classic" % "1.0.13"

libraryDependencies += "org.iq80.leveldb" % "leveldb" % "0.7"

libraryDependencies += "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8"

libraryDependencies += "joda-time" % "joda-time" % "2.9.4"

libraryDependencies += "org.joda" % "joda-convert" % "1.8"

libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.13.2" // % "test"

libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.0"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.0" % "test"

libraryDependencies += "com.syncthemall" % "boilerpipe" % "1.2.2"


scalacOptions += "-feature" // -language:higherKinds

initialCommands in console := "import scalaz._, Scalaz._"

initialCommands in console in Test := "import scalaz._, Scalaz._, scalacheck.ScalazProperties._, scalacheck.ScalazArbitrary._,scalacheck.ScalaCheckBinding._"