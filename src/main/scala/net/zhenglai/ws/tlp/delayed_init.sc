

/*
The trait accepts a single argument, which is non-strictly evaluated (due to =>), and returns Unit.
Ok, so the println is actualy in the constructor of the Main class!". And this would usually be true, but not in this case, since we inherited the DelayedInit trait - as App extends it:

trait DelayedInit {
  def delayedInit(x: => Unit): Unit
}

 it does not contain any implementation - all the work around it is actually performed by the compiler, which will treat all classes and objects inheriting DelayedInit in a special way (note: trait’s will not be rewriten like this).

* imagine your class/object body is a function, doing all these things that are in the class/object body,

* the compiler creates this function for you, and will pass it into the delayedInit(x: => Unit) method (notice the call-by-name in the parameter).

 */
object Main extends App {
  println("Hello!")
}

Main.main(Array())

// the compiler emits:
object Main2 extends DelayedInit {
  def delayedInit(x: => Unit = {
                    println("Hello!")
                  }) = ??? // impl is left for us to fill in
}