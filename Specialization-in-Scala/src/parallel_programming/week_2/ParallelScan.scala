package parallel_programming.week_2

import parallel_programming.week_1._

object ParallelScan {
  
  def scanLeft2[A](inp: Array[A], a0: A, f: (A, A) => A, out: Array[A]): Unit = {
    out(0) = a0
    var a = a0
    var i = 0
    while(i < inp.length) {
      a = f(a, inp(i))
      i = i + 1
      out(i) = a
    }
  }
  
  /**
   * Can you define result of scanLeft using map and reduce?
   */
  def reduceSeg1[A](inp: Array[A], left: Int, right: Int, a0: A, f: (A,A) => A): A = {
    var a = a0
    var i = left
    while(i < right) {
      a = f(a, inp(i))
      i = i + 1      
    }
    a
  }
  
  def mapSeg[A, B](inp: Array[A], left: Int, right: Int, fi: (Int,A) => B, out: Array[B]): Unit = {
    ???
  }
  
  def scanLeft1[A](inp: Array[A], a0: A, f: (A, A) => A, out: Array[A]): Unit = {
    val fi = {
      (i: Int, v: A) => reduceSeg1(inp, 0, i, a0, f)
    }
    mapSeg(inp, 0, inp.length, fi, out)
    val last = inp.length - 1
    out(last + 1) = f(out(last), inp(last))
  }
  
  /**
   * Tree definitions
   */
  //Trees storing our input collection only have values in leaves
  sealed abstract class Tree[A]
  case class Leaf[A](a: A) extends Tree[A]
  case class Node[A](l: Tree[A], r: Tree[A]) extends Tree[A]
  //Trees storing intermediate values also have (res) values in nodes
  sealed abstract class TreeRes[A] {
    val res: A
  }
  case class LeafRes[A](override val res: A) extends TreeRes[A]
  case class NodeRes[A](l: TreeRes[A], override val res: A, r: TreeRes[A]) extends TreeRes[A]
  
  /**
   * Intermediate tree for array reduce
   */
  sealed abstract class TreeResA[A] {
    val res: A
  }
  case class LeafA[A](from: Int, to: Int, override val res: A)extends TreeResA[A]
  case class NodeA[A](l: TreeResA[A], override val res: A, r: TreeResA[A])extends TreeResA[A] 
  
  /**
   * Can you define reduceRes that transforms 
   * Tree into TreeRes
   */
  def reduceRes[A](t: Tree[A], f: (A,A) => A): TreeRes[A] = t match{
    case Leaf(v) => LeafRes(v)
    case Node(l, r) => {
      val (tL, tR) = (reduceRes(l, f), reduceRes(r, f))
      NodeRes(tL, f(tL.res, tR.res), tR)
    }
  }
  
  /**
   * Parallel reduce that preserves the computation tree(upsweep)
   */
  def upsweep[A](t: Tree[A], f: (A,A) => A): TreeRes[A] = t match {
    case Leaf(v) => LeafRes(v)
    case Node(l, r) => {
      val (tL, tR) = parallel(upsweep(l, f), upsweep(r, f))
      NodeRes(tL, f(tL.res, tR.res), tR)
    }
  }
  
  /**
   * Using tree with results to create the final collection
   */
  def downsweep[A](t: TreeRes[A], a0: A, f: (A,A) => A): Tree[A] = t match {
    case LeafRes(a) => Leaf(f(a0, a))
    case NodeRes(l, _, r) => {
      val (tL, tR) = parallel(downsweep[A](l, a0, f), downsweep[A](r, f(a0, l.res), f))
      Node(tL, tR)
    }
  }
  
  /**
   * scanLeft on trees
   */
  def prepend[A](x: A, t: Tree[A]): Tree[A] = t match {
    case Leaf(v) => Node(Leaf(x), Leaf(v))
    case Node(l, r) => Node(prepend(x, l), r)
  }
  def scanLeft[A](t: Tree[A], a0: A, f: (A,A) => A): Tree[A] = {
    val tRes = upsweep(t, f)
    val scan1 = downsweep(tRes, a0, f)
    prepend(a0, scan1)
  }
  
  /**
   * Upsweep on array
   */
  def upsweep[A](inp: Array[A], from: Int, to: Int, f: (A, A) => A): TreeResA[A] = {
    val threshold = 100000
    if(to - from < threshold)
      LeafA(from, to, reduceSeg1(inp, from + 1, to, inp(from), f))
    else {
      val mid = from + (to - from) / 2
      val (tL, tR) = parallel(upsweep(inp, from, mid, f), upsweep(inp, mid, to, f))
      NodeA(tL, f(tL.res, tR.res), tR)
    }
  }
  /**
   * Downsweep on array
   */
  def scanLeftSeg[A](inp: Array[A], left: Int, right: Int,a0: A, f: (A,A) => A, out: Array[A]) = {
    if(left < right) {
      var i = left
      var a = a0
      while(i < right) {
        a = f(a, inp(i))
        i = i + 1
        out(i) = a
      }
    }
  }  
  def downsweep[A](inp: Array[A], a0: A, f: (A,A) => A, t: TreeResA[A], out: Array[A]): Unit = t match {
    case LeafA(from, to, res) => scanLeftSeg(inp, from, to, a0, f, out)
    case NodeA(l, _, r) => {
      val (_, _) = parallel(downsweep(inp, a0, f, l, out), downsweep(inp, f(a0, l.res), f, r, out))
    }
  }
  /**
   * Finally: parallel scan on the array
   */
  def scanLeft[A](inp: Array[A], a0: A, f: (A,A) => A, out: Array[A]) = {
    val t = upsweep(inp, 0, inp.length, f)
    downsweep(inp, a0, f, t, out) //fills out[1..inp.length]
    out(0) = a0 //prepends a0
  }
	def main(args: Array[String]) {
	  //map: apply function to each element.
		val l = List(1,3,8).map(x => x * x) == List(1, 9, 64)
		//fold: combine elements with a given operation
		val l1 = List(1,3,8).fold(100)((s,x) => s + x) == 112
		//scanLeft: list of the folds of all list prefixes
		val l2 = List(1,3,8).scanLeft(100)((s,x) => s + x) == List(100, 101, 104, 112)
		val l3 = List(1,3,8).scanLeft(100)(_ + _) == List(100, 101, 104, 112)
		//scanRight is different from scanLeft, even if f is associative.
		val l4 = List(1,3,8).scanRight(100)(_ + _) == List(112, 111, 108, 100) 
		println("****************************************")
		val t1 = Node(Node(Leaf(1), Leaf(3)), Node(Leaf(8), Leaf(50)))
		val plus = (x: Int, y: Int) => x + y
		val res0 = reduceRes(t1, plus)
		println(res0) //NodeRes(NodeRes(LeafRes(1),4,LeafRes(3)),62,NodeRes(LeafRes(8),58,LeafRes(50)))
		val res1 = downsweep(res0, 100, plus)
		println(res1) //Node(Node(Leaf(101),Leaf(104)),Node(Leaf(112),Leaf(162)))
	}
}