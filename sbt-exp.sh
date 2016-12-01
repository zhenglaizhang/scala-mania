# You can also run sbt in batch mode, specifying a space-separated list of sbt commands as arguments.
sbt> sbt clean compile "testOnly TestA TestB"



# To speed up your edit-compile-test cycle, you can ask sbt to automatically recompile or run tests whenever you save a source file.
sbt> ~ compile
