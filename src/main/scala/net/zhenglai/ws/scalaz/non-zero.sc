import net.zhenglai.lib.NonZero
import net.zhenglai.lib.ToNonZeroOps._

10.isNonZero
0.isNonZero
(-2).isNonZero

// 试试其它即兴类型

implicit val stringNZInstance: NonZero[String] = NonZero.create {
  case null | "" => false
  case _         => true
}

implicit val boolNZInstance: NonZero[Boolean] = NonZero.create(b => b)

implicit def listNZInstance[A]: NonZero[List[A]] = NonZero.create {
  case Nil => false
  case _   => true
}

null.asInstanceOf[String].isNonZero
"".isNonZero

true.isNonZero

false.isNonZero

Nil.asInstanceOf[List[Int]].isNonZero
List().isNonZero
List(1, 2, 3).isNonZero
('a' to 'b').toList.isNonZero
