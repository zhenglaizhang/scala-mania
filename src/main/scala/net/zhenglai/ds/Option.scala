package net.zhenglai.ds

// TODO http://www.cnblogs.com/tiger-xc/p/4336280.html
/*
一个专门对付异常情况出现时可以有一致反应所使用的数据类型。Option可以使编程人员不必理会出现异常后应该如何处理结果，他只是获得了一个None值，但这个None值与他所期待的类型是一致的，他可以继续用处理这种类型数据的方法使用这个结果。不过遗憾的是我们通过None值只能知道某个计算没能得出结果，但到底发生了什么事Option并没有提供任何提示。这样我们也就无法向用户提供贴切的系统错误或着操作失误信息了。
 */
sealed trait Option[+A] {

  def flatMap_1[B](f: A => Option[B]): Option[B] = {
    map(f) getOrElse None
    // map(f) >>>> Option[Option[B]]
    // 如果 Option[B] = X >>>> getOrElse Option[X] = X = Option[B]
  }

  /*
上面的[B >: A]是指类型B是类型A的父类，结合+A变形，Option[B]就是Option[A]的父类：如果A是Apple，那么B可以是Fruit,那么上面的默认值类型就可以是Fruit,或者是Option[Fruit]了。=> B表示输入参数B是拖延计算的，意思是在函数内部真正参考（refrence）这个参数时才会对它进行计算。
   */
  def getOrElse[B >: A](default: => B): B = this match {
    case None    => default
    case Some(x) => x
  }

  def map[B](f: A => B): Option[B] = this match {
    case None    => None
    case Some(x) => Some(f(x))
  }

  def orElse_1[B >: A](ob: => Option[B]): Option[B] = {
    map(Some(_)) getOrElse None
    //this[Option[A]] Some(_) >>>> Option[A]
    //map(Some(_)) >>>> Option[Option[A]]
  }

  def filter_1(f: A => Boolean): Option[A] = {
    flatMap(a => if (f(a)) Some(a) else None)
  }

  def flatMap[B](f: A => Option[B]): Option[B] = this match {
    case None    => None
    case Some(x) => f(x)
  }

  def filter(f: A => Boolean): Option[A] = this match {
    case Some(x) if f(x) => this
    case _               => None
  }

  def orElse[B >: A](ob: => Option[B]): Option[B] = this match {
    case None => ob
    case _    => this
  }
}

case object Option {
  /*
Option数据类型使编程者无须理会函数的异常，可以用简洁的语法专注进行函数组合（function composition）。普及使用Option变成了泛函编程的重要风格。Scala是一种JVM编程语言，因而在用Scala编程时可能会调用大量的java库函数。那么我们如何保证在调用现有java库的同时又可以不影响泛函编程风格呢？我们需不需要在使用java函数时用null和Exception而在Scala中就用Option呢？答案是否定的！通过泛函编程的函数组合我们可以在不改变java源代码的情况下实现对java库函数的“升格”（lifting）。实际上我们现在泛函编程中的风格要求是在调用某个函数时，这个函数要能接受Option类型传入参数及返回Option类型值。用函数类型来表达就是：把 A => B 这样的函数编程“升格”成 Option[A] => Option[B]这样的函数：

先从类型匹配上分析：map(f) >>> Option[B]。这个占位符 _ 在这里代表输入参数，就是 this >>>>>> Opption[A]。所以类型匹配。实际上这个函数表达形式先明确了最后生成的结果函数是：给一个Option，返回一个Option，这不是典型的函数文本（lambda function）描述吗：oa => oa map f >>> _ map f 。
   */
  def lift[A, B](f: A => B): (Option[A] => Option[B]) = _ map f

  // 用for comprehension 两个参数
  def lift2[A, B, C](f: (A, B) => C): (Option[A], Option[B]) => Option[C] = {
    (oa: Option[A], ob: Option[B]) =>
      for {
        aa <- oa
        bb <- ob
      } yield f(aa, bb)
  }

  //用    flatMap款式  三个参数
  def lift3[A, B, C, D](f: (A, B, C) => D): (Option[A], Option[B], Option[C]) => Option[D] = {
    (oa: Option[A], ob: Option[B], oc: Option[C]) =>
      oa.flatMap(aa => ob.flatMap(bb => oc.map(cc => f(aa, bb, cc))))
  }
}

case class Some[A](value: A) extends Option[A]

case object None extends Option[Nothing]
