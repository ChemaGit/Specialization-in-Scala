package principles_FP_scala.week_4

import principles_FP_scala.week_3.EmptyE
import principles_FP_scala.week_3.NonEmptyE

trait ListScala[+T] {
  def isEmpty: Boolean
  def head: T
  def tail: ListScala[T]
  def prepend[U >: T] (elem: U): ListScala[U] = new Cons(elem, this)
}

class Cons[T](val head: T, val tail: ListScala[T]) extends ListScala[T] {
  def isEmpty = false
}  

object Nil extends ListScala[Nothing] {
  def isEmpty: Boolean = true
  def head: Nothing = throw new NoSuchElementException("Nil.head")
  def tail: Nothing = throw new NoSuchElementException("Nil.tail")
}

object ListScala {
  // List() = Nil
  def apply[T] = Nil
  //List(1) = List.apply(1)
  def apply[T](x1: T): ListScala[T] = new Cons(x1, Nil)
  // List(1, 2) = List.apply(1, 2)
  def apply[T](x1: T, x2: T):ListScala[T] = new Cons(x1,new Cons(x2, Nil))  
}

object test extends App{
  val x: ListScala[String] = Nil
  def f(xs: ListScala[NonEmptyE], x: EmptyE) = xs prepend x
  
  def insert(x: Int, xs: List[Int]): List[Int] = xs match {
    case List() => List(x)
    case y :: ys => if(x <= y) x :: xs else y :: insert(x, ys)
  }
  
  def isort(xs: List[Int]): List[Int] = xs match {
    case List() => List()
    case y :: ys => insert(y, isort(ys))
  }
  
  val l = List(3, 2, 1, 7, 5, 6)
  
  println(l.mkString(","))
  println(isort(l).mkString(","))
}