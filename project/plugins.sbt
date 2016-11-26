logLevel := Level.Warn

// or put this in ~/.sbt/0.13/global.sbt
// Scala compiler plugin, which will flag errors in your Scalactic code at compile time
// resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"
addSbtPlugin("com.artima.supersafe" % "sbtplugin" % "1.1.1")

addSbtPlugin("org.scalariform"    % "sbt-scalariform"     % "1.6.0" )


