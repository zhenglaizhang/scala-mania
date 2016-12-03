By default, sbt works **purely by convention**. sbt will find the following automatically:

* Sources in the base directory
* Sources in src/main/scala or src/main/java
* Tests in src/test/scala or src/test/java
* Data files in src/main/resources or src/test/resources
* jars in lib

By default, sbt will build projects with the same version of Scala used to run sbt itself.




### Tips about Scala REPL
* `CTRL + L` to clear the screen, sbt REPL, also Scala REPL
* `alias scala="scala -Dscala.color=true"` to enable colorized scala REPL
* Past repl transcript, e.g `scala> 12+12`, ended with `CTRL + D`
* REPL Here Documents (3 flavors), e.g.

```scala
:paste < EOF
object C { val c = 21 }
class C { val c = C.c * 2 }
EOF
new C().c


:paste <| EOF
	|def double(i: Int) = {
	|	 i * 2
	|}
	|double(12)
EOF

:paste <~ EOF
	def double(i: Int) = {
		i * 2
	}
	double(12)
EOF	
```

* type `"hello".s` | `"hello".split` then tab once, or `"hello".split` then tab twice to see signatures



### SBT console

* Like Scala REPL, but introduces the project on the `classpath`
* `-testQuick` - incrementally compile modified code and only run test classes that were affected by the change
* `+task` - Perform `task` for each compiler version in crossScalaVersion, life saver for lib author, e.g.

```bash
$ sbt +compile
$ sbt +publish
```

* `++ x.y task` - Perform `task` using version x.y of Scala compiler

```bash
$ sbt "++2.11.8 compile"
$ sbt "++2.12 compile" # goes to fetch the specific version and compile against that
``` 



### Examining Dependencies

* Direct vs Transitive dependencies
* Transitive dependencies needs different version of some artifacts...
* SBT (and Activator) create XML files showing project dependencies whenever they run the `update`  task
* Works with all projects, including Play proejcts

```bash
$  /Applications/Firefox.app/Contents/MacOS/firefox `find target/resolution-cache/reports/ -name \*compile.xml | grep -v javadoc`

```


### External SBT Projects

* What's wrong with SBT sub-projects?
	* All live in the same git project
	* All sub-projects must use the same git branch
	* Merge conflicts maximized
	* Everyone has equal access to the entire code base
* Use `git subtree` to stitch together git projects into one

```scala
// Project in _model directory
lazy val base_model = project.in(file("."))

// Dependant project in model directory
lazy val model = Project("model", file("."))
	.dependsOn(ProjectRef(uri("../_model"), "base_model"))
	
// Console app: build.sbt
lazy val root = (project in file("."))
	.dependsOn(
		ProjectRef(uri("../_model"), "base_model"), // adding this to make IDEA happy
		ProjectRef(uri("../model"), "model")
	)
	
// Play webapp: build.sbt
lazy val root = (project in file("."))
	.enablePlugins(PlayScala)
	.dependsOn(
		ProjectRef(uri("../_model"), "base_model"), // adding this to make IDEA happy
		ProjectRef(uri("../model"), "model")
	)
```
	


### TMUX

* Edit in one pane, view compiler output in another
* Wish one standard `tmux.conf` file



### Intelij IDEA

* Terminal plugin - `run, bash, git, sbt, etc`
* Markdown plugin
* Regex Tester plugin
* Project pane is `'of no use'`
* Split-pane editing - moving files, resizing. Right click on editor tab, and chain `splits window` any times as you go
* Open from the top navigation bar
* Locates current file in project pane, `Auto scroll to/from Source`
* Local history
* External libs `~/.iv2` dir, place breakpoins there and DEBUG that!



