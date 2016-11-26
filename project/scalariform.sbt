

//
//import scalariform.formatter.preferences._
//
//import com.typesafe.sbt.SbtScalariform
//import com.typesafe.sbt.SbtScalariform.ScalariformKeys._
//
//lazy val BuildConfig = config("build") extend Compile
//lazy val BuildSbtConfig = config("buildsbt") extend Compile
//
//val foo: Int => Int = {
//  case 1 => 1
//  case     2 => 2
//}
//
//SbtScalariform.scalariformSettings
//
//inConfig(BuildConfig)(SbtScalariform.scalariformSettings)
//
//inConfig(BuildSbtConfig)(SbtScalariform.scalariformSettings)
//
//scalaSource in BuildConfig := baseDirectory.value / "project"
//
//scalaSource in BuildSbtConfig := baseDirectory.value
//
//includeFilter in (BuildConfig, format) := ("*.scala": FileFilter)
//
//includeFilter in (BuildSbtConfig, format) := ("*.sbt": FileFilter)
//
//format in BuildConfig := {
//  val x = (format in BuildSbtConfig).value
//  (format in BuildConfig).value
//}
//
//preferences := preferences.value.
//  setPreference(AlignSingleLineCaseStatements, true).
//  setPreference(AlignParameters, true)