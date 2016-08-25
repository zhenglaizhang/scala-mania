/*
Scala的函数（function）还是值得提的。函数可以当作标准的对象使用：可以当作另一个函数的输入参数或者结果值。接受函数作为输入参数或者返回另一函数作为结果的函数被称之为高阶函数（high order function）。在Scala编程里匿名函数（anonymous function or lamda function)或函数文本（function literal）的使用也很普遍。
 */

val formatResult = (name: String, n: Int, f: Int => Int) => {
  val msg = "The %s of %d is %d."
  msg.format(name, n, f(n))
}

formatResult("double", 3, _ * 6)

// 传入函数formatResult的输入参数f可以是一个普通的函数如factorial,abs。也可用函数文本，只要它的类型是Int => Int就可以了
println(formatResult("absolute value", -42, Math.abs))
println(formatResult("increment", 7, (x: Int) => x + 1))
println(formatResult("increment2", 7, (x) => x + 1))
println(formatResult("increment3", 7, x => x + 1))
println(formatResult("increment4", 7, _ + 1))
println(formatResult("increment5", 7, x => { val r = x + 1; r }))