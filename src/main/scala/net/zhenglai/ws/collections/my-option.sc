import net.zhenglai.ds._

Some(2) map (_ + 3)

//在管子里相加。结果还是保留在管子内
Some(2) map {
  _ + 3
} //> res0: ch4.exx.Option[Int] = Some(5)

None
// res2: net.zhenglai.ds.None.type = None

// 注意类型信息!!
val none = None: Option[Int] //> none  : ch4.exx.Option[Int] = None
//可以直接使用None而不会出异常
none map {
  _ + 3
} //> res1: ch4.exx.Option[Int] = None

//在管子里相加。结果还是保留在管子内
Some(2) flatMap { x => Some(x + 3) } //> res2: ch4.exx.Option[Int] = Some(5)
//可以直接使用None而不会出异常
none flatMap { x => Some(x + 3) } //> res3: ch4.exx.Option[Int] = None

Some(2) getOrElse 5 //> res4: Int = 2
none getOrElse 5 //> res5: Int = 5
Some(2) orElse Some(5) //> res6: ch4.exx.Option[Int] = Some(2)
none orElse Some(5) //> res7: ch4.exx.Option[Int] = Some(5)