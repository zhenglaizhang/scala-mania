import net.zhenglai.lib.State
import net.zhenglai.lib.State._

type Stack = List[Int]

/*
pop就是一个State实例。它的状态行为函数是partial function：把一个现成的List[Int]拆分成新的值和状态即把第一个元素去掉放到值里
 */
def pop = State[Stack, Int] { case x :: xs => (x, xs) }

//push就是一个State实例。它的状态行为函数把i压到一个现成的List[Int]上，跟值没有任何关系
def push(i: Int) = State[Stack, Unit] { case xs => ((), i :: xs) }

def stackRun: State[Stack, Int] = {
  for {
    _ <- push(12)
    a <- pop
    b <- pop
  } yield a + b
}

/*
在stackRun里我们没有在任何地方提到状态Stack，但看看运行结果(a,s)：不但返回值是正确的，而且Stack状态也默默地发生了转变
 */
val (a, s) = stackRun.run(List(10, 11, 12))

def stackRun2: State[Stack, Int] = {
  for {
    _ <- push(13)
    a <- pop
    _ <- setState(List(8, 9)) //临时将状态设置成List(8,9)。
    b <- pop
    s1 <- getState
  } yield (a + b)
} //> stackRun: => ch6.state.State[ch6.state.Stack,Int]

val (a1, s1) = stackRun2.run(List(10, 11, 12)) //> a  : Int = 21
//| s  : ch6.state.Stack = List(9)