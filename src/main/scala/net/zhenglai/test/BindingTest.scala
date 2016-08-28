package net.zhenglai.test

// This object contains the bindings/scope tests
object Test {

  def main(arg : Array[String]) : Unit = {
    testSamePackage()
    testWildcardImport()
    testExplicitImport()
    testInlineDefinition()
  }

  // This looks for a binding 'x' within the same package (test) as this scope.
  def testSamePackage() {
    println(x)  // 在另外文件的test package. prints: Externally bound x object in package test
  }

  // This defines a new scope with an 'x' binding that we can import with a wildcard.
  object Wildcard {
    def x = "Wildcard Import x"
  }

  // This function will print the value in the binding 'x' after importing from the Wildcard object
  // using a wildcard import.
  def testWildcardImport() {
    import Wildcard._
    println(x)  // prints: Wildcard Import x
  }

  // This defines another binding of 'x' that we can import explicitly.
  object Explicit {
    def x = "Explicit Import x"
  }

  def testExplicitImport() {
    import Explicit.x
    import Wildcard._
    println(x)  // .x优先于._  prints: Explicit Import x
  }

  // This defines an inline binding for x.  Note that with all the imports, there are no ambiguous naming conflicts.
  def testInlineDefinition() {
    val x = "Inline definition x" //即使写在最前，本地binding x还是最优先
    import Explicit.x
    import Wildcard._
    println(x)  // prints:  Inline definition x
  }
}
