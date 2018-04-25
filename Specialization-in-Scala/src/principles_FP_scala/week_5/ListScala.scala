package principles_FP_scala.week_5

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
  
  def last[T](xs: List[T]): T = xs match {
    case List() => throw new Error("Last of empty List.")
    case List(x) => x
    case y :: ys => last(ys)
  }  
  
  def init[T](xs: List[T]): List[T] = xs match {
    case List() => throw new Error("Init of empty List.")
    case List(x) => List()
    case y :: ys => y :: init(ys)
  }
  
  def concat[T](xs: List[T], ys: List[T]): List[T] = xs match {
    case List() => ys    
    case z :: zs => z :: concat(zs, ys)
  }
  
  def reverse[T](xs: List[T]): List[T] = xs match {
    case List() => xs
    case y :: ys => reverse(ys) ++ List(y)
  }
  
  def removeAt[T](xs: List[T], n: Int): List[T] = {
    def go(nxs: List[T], cont: Int): List[T] = {
      if(cont == n) nxs.tail
      else if(nxs.tail.isEmpty) nxs
      else nxs.head :: go(nxs.tail, cont + 1)
    }
    go(xs, 0)
  }
  
  def removeAt(n: Int, xs: List[Int]) = (xs take n) ::: (xs drop n + 1)
  
  def flatten(xs: List[Any]): List[Any] = xs match {
    case List() => xs
    case y :: ys => y match {
      case List() => flatten(ys)
      case z :: zs => (z :: flatten(zs)) ::: flatten(ys)
    }
    case (z : Any) => z :: flatten(xs)
  }
  
  
  val f = List(List(1,1),  List(3, List(5, 8)))
  val l = List(3, 2, 1, 7, 5, 6)
  //println(removeAt(l,6).mkString(","))
  //println(l.mkString(","))
  //println(isort(l).mkString(","))
  println(flatten(f).mkString(","))
}