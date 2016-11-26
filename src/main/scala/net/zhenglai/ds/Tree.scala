package net.zhenglai.ds

sealed trait Tree[+A] {
  def size: Int = this match {
    case Leaf(_)      => 1
    case Branch(l, r) => 1 + l.size + r.size
  }

  def leafSize: Int = this match {
    case Leaf(_)      => 1
    case Branch(l, r) => l.leafSize + r.leafSize
  }

  def branchSize: Int = this match {
    case Leaf(_)      => 0
    case Branch(l, r) => 1 + l.branchSize + r.branchSize
  }

  def depth: Int = this match {
    case Leaf(_)      => 0
    case Branch(l, r) => 1 + (l.depth max r.depth)
  }

  def maxValue: Int = this match {
    case Leaf(x: Int) => x
    case Branch(l, r) => l.maxValue max r.maxValue
  }

  // TODO

  /*
  可以从以上这些函数得出一下共性。把共性抽象出来用fold来实现：
  函数fold分别收到两个方法f,g：f用来处理Leaf，g用来处理Branch。看看用fold来实现上面的函数：
   */
  def fold[B](f: A => B)(g: (B, B) => B): B = this match {
    case Leaf(n)      => f(n)
    case Branch(l, r) => g(l.fold(f)(g), r.fold(f)(g))
  }

  def sizeByFold = fold(a => 1)(1 + _ + _)

  def depthByFold = fold(a => 0)((x, y) => 1 + (x max y))

  def map[B](f: A => B): Tree[B] = this match {
    case Leaf(x)      => Leaf(f(x))
    case Branch(l, r) => Branch(l.map(f), r.map(f))
  }

  def flatMap[B](f: A => Tree[B]): Tree[B] = this match {
    case Leaf(a)      => f(a)
    case Branch(l, r) => Branch(l.flatMap(f), r.flatMap(f))
  }
}

object Tree {
  def size[A](t: Tree[A]): Int = t match {
    case Leaf(_)      => 1
    case Branch(l, r) => 1 + size(l) + size(r)
  }

  def max(t: Tree[Int]): Int = t match {
    case Leaf(n)      => n
    case Branch(l, r) => max(l) max max(r)
  }

  def depth[A](t: Tree[A]): Int = t match {
    case Leaf(_)      => 0
    case Branch(l, r) => 1 + (depth(l) max depth(r))
  }

  def map[A, B](t: Tree[A])(f: A => B): Tree[B] = t match {
    case Leaf(x)      => Leaf(f(x))
    case Branch(l, r) => Branch(map(l)(f), map(r)(f))
  }

  def maxValueByFold(t: Tree[Int]) = t.fold(a => a)((x, y) => 0 + (x max y))
}

final case class Leaf[A](value: A) extends Tree[A]

final case class Branch[A](left: Tree[A], right: Tree[A]) extends Tree[A]

/*
Pattern matching again provides a convenient way of operating over elements of our ADT.
 */
