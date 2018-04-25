package parallel_programming.week_2

import parallel_programming.week_1._

object DataOperationsAndParallelMapping {
  
  /**
   * Sequential pointwise exponent written from scratch
   */
  def normsOf(inp: Array[Int], p: Double, left: Int, right: Int, out: Array[Double]): Unit = {
    var i = left
    while(i < right) {
      out(i) = Math.pow(inp(i), p)
      i += 1
    }
  }
  
  /**
   * Parallel pointwise exponent written from scratch
   */
  def normsOfPar(inp: Array[Int], p: Double, left: Int, right: Int, out: Array[Double]): Unit = {
    val threshold = 10000
    if(right - left < threshold) {
      var i = left
      while(i < right) {
        out(i) = Math.pow(inp(i), p)
        i += 1
      }      
    } else {
      val mid = left + (right - left) / 2
      parallel(normsOfPar(inp, p, left, mid, out), normsOfPar(inp, p, mid, right, out))
    }
  }  
  
  /**
   * Sequential map of an array producing an array
   */
  def mapASegSeq[A, B](inp: Array[A], left: Int, right: Int, f: A => B, out: Array[B]) = {
    //Writes out out(i) for left <= i <= right - 1
    var i = left
    while(i < right) {
      out(i) = f(inp(i))
      i += 1
    }    
  }
  
  /**
   * Parallel map of an array producing an array
   */
  def mapASegPar[A, B](inp: Array[A], left: Int, right: Int, f: A => B, out: Array[B]): Unit = {
    //Writes out out(i) for left <= i <= right - 1
    val threshold = 10000
    if(right - left < threshold)
      mapASegSeq(inp, left, right, f, out)
    else {
      val mid = left + (right - left) / 2
      parallel(mapASegPar(inp, left, mid, f, out), mapASegPar(inp, mid, right, f, out))
    }
  }  
  
  /**
   * Parallel map on immutable trees
   * Assume that our trees are balanced:
   * we can explore branches in parallel.
   */
  sealed abstract class Tree[A] {val size: Int}
  case class Leaf[A](a: Array[A]) extends Tree[A] {
    override val size = a.size
  }
  case class Node[A](l: Tree[A], r: Tree[A]) extends Tree[A] {
    override val size = l.size + r.size
  }
  /**
   * Speedup and performance similar for the array
   * The computation depth equals the height of the tree.
   */
  def mapTreePar[A: Manifest, B: Manifest](t: Tree[A], f: A => B): Tree[B] =
    t match {
      case Leaf(a) => {
        val len = a.length;
        val b = new Array[B](len)
        var i = 0
        while(i < len) {
          b(i) = f(a(i))
          i += 1
        }
        Leaf(b)
      }
      case Node(l, r) => {
        val (lb, rb) = parallel(mapTreePar(l, f), mapTreePar(r, f))
        Node(lb, rb)
      }
    }
  
	def main(args: Array[String]) {
		//map: apply function to each element
	  val r = List(1,3,8).map(x => x * x) == List(1,9,64)
	  //fold: combine elements with a given operation
	  val r1 = List(1,3,8).fold(100)((s, x) => s + x) == 112
	  //scan: combine folds of all list prefixes
	  val r2 = List(1,3,8).scan(100)((s, x) => s + x) == List(100, 101, 104, 112)
	  println(r + " " + r1 + " " + r2)
	  println("*****************")
	  val in = Array(2,3,4,5,6)
	  val out: Array[AnyVal] = Array(0,0,0,0,0)
	  val f = (x: Int) => x * x
	  mapASegSeq(in, 1, 3, f, out)
	  println(out.mkString(","))
	  println("*****************")
	  /**
	   * Example of using MapASegPar: pointwise exponent
	   */
	  val p: Double = 1.5
	  def f1(x: Int): Double = Math.pow(x, p)
	  mapASegSeq(in, 0, in.length, f1, out) //sequential
	  mapASegPar(in, 0, in.length, f1, out) //parallel
	}
}