import net.zhenglai.ds._

Stream(1, 2, 3)
// 看看，Stream(1,2,3)就是一个声明。我们通过List转换才真正产生了实例。
Stream(1, 2, 3).toList_1
Stream(1, 2, 3).toListFast
Stream(1, 2, 3).uncons
Stream(1, 2, 3).isEmpty
Stream(1, 2, 3).take(2).toList_1
Stream(1, 2, 3).drop(2).toList_1

(Stream(1, 2, 3, 4, 5) takeWhile { _ < 3 }).toListFast //> res8: List[Int] = List(1, 2)
(Stream(1, 2, 3, 4, 5) dropWhile { _ < 3 }).toListFast //> res9: List[Int] = List(4, 5)
Stream(1, 2, 3, 4, 5).tail //> res10: ch5.stream.Stream[Int] = ch5.stream$Stream$$anon$2@337d0578
(Stream(1, 2, 3, 4, 5).tail).toListFast //> res11: List[Int] = List(2, 3, 4, 5)
Stream(1, 2, 3, 4, 5).headOption //> res12: Option[Int] = Some(1)

Stream(1, 2, 3).append(Stream(4, 5, 6)).toListFast

(Stream(1, 2, 3, 4, 5) map { _ + 10 }).toList //> res15: List[Int] = List(11, 12, 13, 14, 15)
(Stream(1, 2, 3, 4, 5) flatMap { x => Stream(x + 10) }).toList
//> res16: List[Int] = List(11, 12, 13, 14, 15)
(Stream(1, 2, 3, 4, 5) flatMap_1 { x => Stream(x + 10) }).toList
//> res17: List[Int] = List(11, 12, 13, 14, 15)
(Stream(1, 2, 3, 4, 5) filter { _ < 3 }).toList //> res18: List[Int] = List(1, 2)
(Stream(1, 2, 3, 4, 5) filter_1 { _ < 3 }).toList //> res19: List[Int] = List(1, 2)

// 根据List的特性，每个操作都会立即完成，产生一个结果List，然后接着下一个操作。我们试着约化：
List(1, 2, 3, 4) map (_ + 10) filter (_ % 2 == 0) map (_ * 3)
/*
这个运算遍历（traverse）了List三次。一次map操作产生了中间List(11，12，13，14)，二次操作filter产生了List(12,14)，三次操作map产生最终结果List(36,42)。实际上我们如果把遍历这个List的方式变一下：变成每次走一个元素，连续对这个元素进行三次操作，直到走完整个List。这样我们在一个遍历过程就可以完成全部三个操作。Stream恰好是一个元素一个元素走的，因为下面的元素处于延后计算状态。
 */
Stream(1, 2, 3, 4).map(_ + 10).filter(_ % 2 == 0)
(11 #:: Stream(2, 3, 4).map(_ + 10)).filter(_ % 2 == 0)
Stream(2, 3, 4).map(_ + 10).filter(_ % 2 == 0)
(12 #:: Stream(3, 4).map(_ + 10)).filter(_ % 2 == 0)
12 #:: Stream(3, 4).map(_ + 10).filter(_ % 2 == 0)
12 #:: (13 #:: Stream(4).map(_ + 10)).filter(_ % 2 == 0)
12 #:: Stream(4).map(_ + 10).filter(_ % 2 == 0)
//12 #:: (14 #:: Stream().map(_ + 10)).filter(_ % 2 == 0)
//12 #:: 14 #:: Stream().map(_ + 10).filter(_ % 2 == 0)
12 #:: 14 #:: Stream()

