// first of all:
//  to support various projects the build file is so huge
//  in future, I would like to use this file as template and start from portion of it
import scalariform.formatter.preferences._
import com.typesafe.sbt.SbtScalariform
import com.typesafe.sbt.SbtScalariform.ScalariformKeys

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

// set the main class for packaging the main jar
// 'run' will still auto-detect and prompt
// change Compile to Test to set it for the test jar
// mainClass in (Compile, packageBin) := Some("net.zhenglai.web.Main")

// set the main class for the main 'run' task
// change Compile to Test to set it for 'test:run'
// mainClass in (Compile, run) := Some("net.zhenglai.web.Main")

// add <base>/input to the files that '~' triggers on
// watchSources <+= baseDirectory map { _ / "input" }

// disable using the Scala version in output paths and artifacts as no cross compiilation plan
crossPaths := false

// fork a new JVM for 'run' and 'test:run'
fork := true

// max error scalac stops (or sbt logger silently ignore more errors?)
// set maxErrors := 1
// just before doing ~run
maxErrors := 30

// time between polling for file changes when using continuous execution
pollInterval := 1000

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
  .settings(jodaDeps)
  .settings(commonScalacOptions)
  .aggregate(lib, ws, crawler, doc)

lazy val lib = project
  .settings(commonScalacOptions)
  .settings(testCommonDeps)

lazy val ws = project
  .settings(commonScalacOptions)
  .settings(testCommonDeps)

lazy val crawler = project
  .settings(commonScalacOptions)

lazy val doc = project

// https://mvnrepository.com/artifact/org.scala-lang.modules/scala-async_2.11

resolvers in Global ++= Seq(
  Resolver.mavenLocal,
  "twitter-repo" at "http://maven.twttr.com",
  //  "typesafe-repo" at "http://repo.typesafe.com/typesafe/releases/"
  "Artima Maven Repository" at "http://repo.artima.com/releases",
  Resolver.typesafeRepo("release")
)

lazy val commonScalacOptions = Seq(
  scalacOptions ++= Seq(
    "-deprecation"
    , "-feature" // Emit warning and location for usages of features that should be imported explicitly
    , "-encoding", "UTF-8"
    //    , "-feature" // Emit warning and location for usages of features that should be imported explicitly
    //    , "language:_"
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
    //    ,s"-P:artima-supersafe:config-file:${baseDirectory.value in Global}/project/supersafe.cfg"
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
    "org.scalatest" %% "scalatest" % "3.0.1" % "test",
    "org.scalactic" %% "scalactic" % "3.0.1" % "test"
  )
}

// finagle
lazy val finageDeps = Nil


// formatting
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

lazy val jodaDeps = libraryDependencies ++= {
  Seq(
    "joda-time" % "joda-time" % "2.9.4",
    "org.joda" % "joda-convert" % "1.8"
  )
}

libraryDependencies += "com.syncthemall" % "boilerpipe" % "1.2.2"

// initial stategments when entering 'console', 'console-quick', or 'console-project'
initialCommands :=
  """
    |import System.{ currentTimeMillis => now }
    |def time[T](f: => T): T = {
    | val start = now
    | try { f } finally { println("Elapsed: " + (now - start)/1000.0 + " s") }
  """.stripMargin

// set initial commands when entering 'console' only
initialCommands in console := "import scalaz._, Scalaz._"


initialCommands in console in Test := "import scalaz._, Scalaz._, scalacheck.ScalazProperties._, scalacheck.ScalazArbitrary._,scalacheck.ScalaCheckBinding._"

// define the repository to publish to
publishTo := Some(if (isSnapshot.value) sonatypeSnapshots else sonatypeReleases)

publishMavenStyle := true
publishArtifact in Test := false
publishArtifact in(Compile, packageSrc) := true

lazy val sonatypeSnapshots = "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
lazy val sonatypeReleases = "Sonatype OSS Releases" at "https://oss.sonatype.org/service/local/staging/deploy/maven2"


// set Ivy logging to be at the highest level
// ivyLoggingLevel := UpdateLogging.Full

// disable updating dynamic revisions (including -SNAPSHOT versions)
// offline := true

// set the prompt (for this build) to include the project id.
// shellPrompt in ThisBuild := { state => Project.extract(state).currentRef.project + "> " }

// set the prompt (for the current project) to include the username
// shellPrompt := { state => System.getProperty("user.name") + "> " }

// disable printing timing information, but still print [success]
// showTiming := false

// disable printing a message indicating the success or failure of running a task
// showSuccess := false

// change the format used for printing task completion time
// timingFormat := {
// import java.text.DateFormat
// DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
// }
