By default, sbt works **purely by convention**. sbt will find the following automatically:

* Sources in the base directory
* Sources in src/main/scala or src/main/java
* Tests in src/test/scala or src/test/java
* Data files in src/main/resources or src/test/resources
* jars in lib

By default, sbt will build projects with the same version of Scala used to run sbt itself.



### Interactive mode

* Interactive mode

  ```shell
  $ sbt
  sbt> compile
  sbt> run <argument>* # run the main class in the project in the same virtual machine as sbt
  sbt> package # create a jar file containing files in src/main/resources and classes from src/main/{scala,java}
  sbt> reload # Reloads the build definition (build.sbt, project/*.scala, project/*.sbt files).
  sbt> exit # or CTRL +D (Unix) or CTRL + Z (Windows)
  ```

* Batch mode

  *  By specifying a space-separated list of sbt commands as arguments, who will be run in sequence. For sbt commands that take arguments, pass the command and arguments as one argument to sbt by enclosing them in quotes

    ```shell
    $ sbt clean compile "testOnly TestA TestB"
    ```

* Continuous build and test

  * To speed up your edit-compile-test cycle, you can ask sbt to automatically recompile or run tests whenever you save a source file

    ```shell
    sbt> ~ compile	
    ```

  * Press enter to stop watching for changes.You can use the `~` prefix with either interactive mode or batch mode.



### Build Defs

* `build.sbt` may also be interspersed with `val`s, `lazy val`s, and `def`s. Top-level `object`s and`class`es are not allowed in `build.sbt`. Those should go in the `project/` directory as full Scala source files.

* On the left, `name`, `version`, and `scalaVersion` are *keys*. A key is an instance of `SettingKey[T]`, `TaskKey[T]`, or `InputKey[T]` where `T` is the expected value type. 

* Keys have a method called `:=`, which returns a `Setting[T]`. The `:=` method on key `name` returns a `Setting`, specifically a `Setting[String]`. `String` also appears in the type of `name` itself, which is `SettingKey[String]`. In this case, the returned `Setting[String]` is a transformation to add or replace the `name` key in sbt’s map, giving it the value `"hello"`.

* `.sbt` file can contain `val`s and `def`s in addition to settings. All such definitions are evaluated before settings regardless of where they are defined in the file. `val`s and `def`s must be separated from settings by blank lines.

  > **Note:** Typically, lazy vals are used instead of vals to avoid initialization order problems.

There are three flavors of key:

- `SettingKey[T]`: a key for a value computed once (the value is computed when loading the project, and kept around).
- `TaskKey[T]`: a key for a value, called a *task*, that has to be recomputed each time, potentially with side effects.
  - A `TaskKey[T]` is said to define a *task*. Tasks are operations such as `compile` or `package`. They may return `Unit` (`Unit` is Scala for `void`), or they may return a value related to the task, for example `package`is a `TaskKey[File]` and its value is the jar file it creates.
  - Each time you start a task execution, for example by typing `compile` at the interactive sbt prompt, sbt will re-run any tasks **involved exactly once**.
  - sbt’s map describing the project can keep around a fixed string value for a setting such as name, but it has to keep around some executable code for a task such as `compile` — even if that executable code eventually returns a string, it has to be re-run every time.
  - *A given key always refers to either a task or a plain setting.* That is, **“taskiness” (whether to re-run each time) is a property of the key, not the value.**
- `InputKey[T]`: a key for a task that has command line arguments as input. 

Using `:=`, you can assign a value to a setting and a computation to a task. For a setting, the value will be computed once at project load time. For a task, the computation will be re-run each time the task is executed.

#### Types for tasks and settings[ ](http://www.scala-sbt.org/0.13/docs/Basic-Def.html#Types+for+tasks+and+settings)

From a type-system perspective, the `Setting` created from a task key is slightly different from the one created from a setting key. `taskKey := 42` results in a `Setting[Task[T]]` while `settingKey := 42` results in a `Setting[T]`. For most purposes this makes no difference; the task key still creates a value of type `T` when the task executes.

The `T` vs. `Task[T]` type difference has this implication: a setting can’t depend on a task, because a setting is evaluated only once on project load and is not re-run.



#### Keys in sbt interactive mode

In sbt’s interactive mode, you can type the name of any task to execute that task. This is why typing `compile` runs the `compile` task. `compile` is a task key.

If you type the name of a setting key rather than a task key, the value of the setting key will be displayed. Typing a task key name executes the task but doesn’t display the resulting value; to see a task’s result, use `show ` rather than plain ``. The convention for keys names is to use `camelCase` so that the command line name and the Scala identifiers are the same.

To learn more about any key, type `inspect ` at the sbt interactive prompt. Some of the information `inspect` displays won’t make sense yet, but at the top it shows you the setting’s value type and a brief description of the setting.

#### Built-in Keys[ ](http://www.scala-sbt.org/0.13/docs/Basic-Def.html#Built-in+Keys)

The built-in keys are just fields in an object called [Keys](http://www.scala-sbt.org/0.13/sxr/sbt/Keys.scala.html). A `build.sbt` implicitly has an `import sbt.Keys._`, so `sbt.Keys.name` can be referred to as `name`.



To depend on third-party libraries, there are two options. The first is **to drop jars in `lib/` (unmanaged dependencies) and the other is to add managed dependencies,**

The `libraryDependencies` key involves two complexities: `+=` rather than `:=`, and the `%` method. `+=` appends to the key’s old value rather than replacing it, this is explained in [more kinds of setting](http://www.scala-sbt.org/0.13/docs/More-About-Settings.html). The `%`method is used to construct an Ivy module ID from strings



### Scopes

* In truth, each key can have an associated value in more than one context, called a “**scope**.”
* if you have multiple projects in your build definition, a key can have a different value in each project.
* the `compile` key may have a different value for your main sources and your test sources, if you want to compile them differently.
* the `packageOptions` key (which contains options for creating jar packages) may have different values when packaging class files (`packageBin`) or packaging source code (`packageSrc`).
* *There is no single value for a given key name*, because the value may differ according to scope.However, there is a single value for a given *scoped* key.
* If you think about sbt processing a list of settings to generate a key-value map describing the project, as[discussed earlier](http://www.scala-sbt.org/0.13/docs/Basic-Def.html), the keys in that key-value map are *scoped* keys. Each setting defined in the build definition (for example in `build.sbt`) applies to a scoped key as well.
* Often the scope is implied or has a default, but if the defaults are wrong, you’ll need to mention the desired scope in `build.sbt`.



#### Scope axes

* A *scope axis* is a type, where each instance of the type can define its own scope (that is, each instance can have its own unique values for keys).

There are three scope axes:

- Projects

  - If you [put multiple projects in a single build](http://www.scala-sbt.org/0.13/docs/Multi-Project.html), each project needs its own settings. That is, keys can be scoped according to the project.

    The project axis can also be set to “entire build”, so a setting applies to the entire build rather than a single project. Build-level settings are often used as a fallback when a project doesn’t define a project-specific setting.

- Configurations

  - A *configuration* defines a flavor of build, potentially with its own classpath, sources, generated packages, etc. The configuration concept comes from Ivy, which sbt uses for managed dependencies [Library Dependencies](http://www.scala-sbt.org/0.13/docs/Library-Dependencies.html), and from [MavenScopes](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html#Dependency_Scope).

    Some configurations you’ll see in sbt:

    - `Compile` which defines the main build (`src/main/scala`).
    - `Test` which defines how to build tests (`src/test/scala`).
    - `Runtime` which defines the classpath for the `run` task.

    By default, all the keys associated with compiling, packaging, and running are scoped to a configuration and therefore may work differently in each configuration. The most obvious examples are the task keys`compile`, `package`, and `run`; but all the keys which *affect* those keys (such as `sourceDirectories` or `scalacOptions` or `fullClasspath`) are also scoped to the configuration.

- Tasks

  - Settings can affect how a task works. For example, the `packageSrc` task is affected by the `packageOptions` setting.

    To support this, a task key (such as `packageSrc`) can be a scope for another key (such as `packageOptions`).

    The various tasks that build a package (`packageSrc`, `packageBin`, `packageDoc`) can share keys related to packaging, such as `artifactName` and `packageOptions`. Those keys can have distinct values for each packaging task.

### Global scope[ ](http://www.scala-sbt.org/0.13/docs/Scopes.html#Global+scope)

Each scope axis can be filled in with an instance of the axis type (for example the task axis can be filled in with a task), or the axis can be filled in with the special value `Global`.

`Global` means what you would expect: the setting’s value applies to all instances of that axis. For example if the task axis is Global, then the setting would apply to all tasks



### Delegation[ ](http://www.scala-sbt.org/0.13/docs/Scopes.html#Delegation)

A scoped key may be undefined, if it has no value associated with it in its scope.

For each scope, sbt has a fallback search path made up of other scopes. Typically, if a key has no associated value in a more-specific scope, sbt will try to get a value from a more general scope, such as the`Global` scope or the entire-build scope.

This feature allows you to set a value once in a more general scope, allowing multiple more-specific scopes to inherit the value.

You can see the fallback search path or “delegates” for a key using the `inspect` command, as described below. Read on.



#### Referring to scoped keys when running sbt[ ](http://www.scala-sbt.org/0.13/docs/Scopes.html#Referring+to+scoped+keys+when+running+sbt)

sbt displays (and parses) scoped keys like this:

```shell
{<build-uri>}<project-id>/config:intask::key
```

- `{<build-uri>}/<project-id>` identifies the project axis. The `<project-id>` part will be missing if the project axis has “entire build” scope.

- `config` identifies the configuration axis.

- `intask` identifies the task axis.

- `key` identifies the key being scoped.

- `*` can appear for each axis, referring to the `Global` scope.

  If you omit part of the scoped key, it will be inferred as follows:

  - the current project will be used if you omit the project.
  - a key-dependent configuration will be auto-detected if you omit the configuration or task.



Keys have an overloaded method called in used to set the scope. The argument to in can be an instance of any of the scope axes. So for example, though there’s no real reason to do this, you could set the `name`scoped to the `Compile` configuration:

```
name in Compile := "hello"
```

or you could set the name scoped to the `packageBin` task (pointless! just an example):

```
name in packageBin := "hello"
```

or you could set the `name` with multiple scope axes, for example in the `packageBin` task in the `Compile`configuration:

```
name in (Compile, packageBin) := "hello"
```

or you could use `Global` for all axes:

```
name in Global := "hello"
```

(`name in Global` implicitly converts the scope axis `Global` to a scope with all axes set to `Global`; the task and configuration are already `Global` by default, so here the effect is to make the project `Global`, that is, define `*/*:name` rather than `{file:/home/hp/checkout/hello/}default-aea33a/*:name`)

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



