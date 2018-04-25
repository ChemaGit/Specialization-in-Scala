package parallel_programming.week_2

import parallel_programming.week_1._

object FoldReduceOperations {
  /**
   * Folding (reducing) trees
   */  
  sealed abstract class Tree[A]
  case class Leaf[A](value: A) extends Tree[A]
  case class Node[A](left: Tree[A], right: Tree[A]) extends Tree[A]
  
  /**
   * reduce of this tree. 
   * Sequential definition
   */
  def reduce[A](t: Tree[A], f: (A, A) => A): A = t match {
    case Leaf(v) => v
    case Node(l, r) => f(reduce[A](l, f), reduce[A](r, f)) //Node -> f
  }
  
  /**
   * Parallel reduce of a tree
   * the depth complexity of such reduce is the height of the tree
   */
  def reducePar[A](t: Tree[A], f: (A, A) => A): A = t match {
    case Leaf(v) => v
    case Node(l, r) => {
      val (lv, rv) = parallel(reduce[A](l, f), reduce[A](r, f)) //Node -> f
      f(lv, rv)
    }
  }  
  
  /**
   * Order of elements in a tree
   * can use a list to describe the ordering of elements of a tree
   */
  def toList[A](t: Tree[A]): List[A] = t match {
    case Leaf(v) => List(v)
    case Node(l, r) => toList[A](l) ++ toList[A](r)
  }
  //Suppose we also have tree map
  def map[A, B](t: Tree[A], f: A => B): Tree[B] = t match {
    case Leaf(v) => Leaf(f(v))
    case Node(l, r) => Node(map[A, B](l, f), map[A, B](r, f))
  }
  /**
   * Can you express toList using map and reduce?
   */
  //toList(t) == reduce(map(t, List(_)), _ ++ _)  
  
  /**
   * Parallel array reduce
   */
  def reduceSeg[A](inp: Array[A], left: Int, right: Int, f: (A, A) => A): A = {
    val threshold = 100000
    if(right - left < threshold) {
      var res = inp(left);
      var i = left + 1
      while(i < right) {
        res = f(res, inp(i))
        i += 1
      }
      res
    } else {
      val mid = left + (right - left) / 2
      val (a1, a2) = parallel(reduceSeg(inp, left, mid, f), reduceSeg(inp, mid, right, f))
      f(a1, a2)
    }
  }
  
  def reduceArray[A](inp: Array[A], f: (A, A) => A): A =
    reduceSeg(inp, 0, inp.length, f)
  
	def main(args: Array[String]) {
		//fold: combine elements with a given operation.
	  val r = List(1, 3, 8).fold(100)((s, x) => s + x) == 112
	  val r1 = List(1, 3, 8).foldLeft(100)((s, x) => s - x) == ((100 - 1) - 3) -8 == 88
	  val r2 = List(1, 3, 8).foldRight(100)((s, x) => s - x) == 1 - (3 - (8 - 100)) == -94
	  val r3 = List(1, 3, 8).reduceLeft((s, x) => s - x) == (1 - 3) - 8 == -10
	  val r4 = List(1, 3, 8).reduceRight((s, x) => s - x) == 1 - (3 - 8) == 6
	  println("************************")
	  /**
	   * Running reduce
	   */
	  def tree = Node(Leaf(1), Node(Leaf(3), Leaf(8)))
	  def fMinus = (x: Int, y: Int) => x - y
	  def res = reduce[Int](tree, fMinus) // 6
	  println("************************")	  
	}
}