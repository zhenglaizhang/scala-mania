package net.zhenglai.ds

sealed trait Tree[+A]


object Tree {
  def size[A](t: Tree[A]): Int = t match {
    case Leaf(_) => 1
    case Branch(l, r) => 1 + size(l) + size(r)
  }

  def max(t: Tree[Int]): Int = t match {
    case Leaf(n) => n
    case Branch(l, r) => max(l) max max(r)
  }

  def depth[A](t: Tree[A]): Int = t match  {
    case Leaf(_) => 0
    case Branch(l ,r) => 1 + (depth(l) max depth(r))
  }

  def map[A, B](t: Tree[A])(f: A => B): Tree[B] = t match {
    case Leaf(x) => Leaf(f(x))
    case Branch(l, r) => Branch(map(l)(f), map(r)(f))
  }

  def fold[A, B](t: Tree[A])(f: A => B)(g: (B, B) => B): B = ???

  // TODO
}

final case class Leaf[A](value: A) extends Tree[A]

final case class Branch[A](left: Tree[A], right: Tree[A]) extends Tree[A]

/*
Pattern matching again provides a convenient way of operating over elements of our ADT.
 */
