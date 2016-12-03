logLevel := Level.Warn

// or put this in ~/.sbt/0.13/global.sbt
// Scala compiler plugin, which will flag errors in your Scalactic code at compile time
// resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"
addSbtPlugin("com.artima.supersafe" % "sbtplugin" % "1.1.1")

addSbtPlugin("org.scalariform"    % "sbt-scalariform"     % "1.6.0" )


addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.13")

/*
We have already seen build.sbt in the project’s base directory. Other sbt files appear in a project subdirectory.

project can contain .scala files, which are combined with .sbt files to form the complete build definition.
 */
