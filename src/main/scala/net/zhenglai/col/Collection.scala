package net.zhenglai.col

/*
Variance arises when generics are combined with subtyping. Variance defines how subtyping of the contained type relates to subtyping of the container type.

Scala has declaration site variance annotations
 */
trait Collection[+T] {

  def add[U >: T](other: U): Collection[U]

}
