package net.zhenglai.ds

/**
  * Created by zhenglai on 8/15/16.
  */
case class MyList[A](list: List[A]) {

  def sortBy[B](f: A => B)(implicit ord: Ordering[B]): List[A] = list.sortBy(f)(ord)


  def sortBy2[B: Ordering](f: A => B): List[A] = list.sortBy(f)(implicitly[Ordering[B]])

}
