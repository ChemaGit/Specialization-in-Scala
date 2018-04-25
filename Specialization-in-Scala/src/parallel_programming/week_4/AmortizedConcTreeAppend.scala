package parallel_programming.week_4

import scala.annotation._

object AmortizedConcTreeAppend {
  
  /**
   * Conc Data Type
   */
  sealed trait Conc[+T] {
    def level: Int
    def size: Int
    def left: Conc[T]
    def right: Conc[T]    
  }  
  
  case object EmptyC extends Conc[Nothing] {
    def level = 0
    def size = 0
    def left = this
    def right = this
  }
  class SingleC[T](val x: T) extends Conc[T] {
    def level = 0
    def size = 1
    def left = this
    def right = this
  }
  case class <>[T](left: Conc[T], right: Conc[T]) extends Conc[T] {
    val level = 1 + math.max(left.level, right.level)
    val size = left.size + right.size    
  }  
  
  case class Append[T](left: Conc[T], right: Conc[T]) extends Conc[T] {
    val level = 1 + math.max(left.level, right.level)
    val size = left.size + right.size
  }  
  
  def appendLeaf[T](xs: Conc[T], y: T): Conc[T] = Append(xs, new SingleC(y))
  
  /**
   * Constant Time Appends in Conc-Trees
   */
  def appendLeaf[T](xs: Conc[T], ys: SingleC[T]): Conc[T] = xs match {
    case EmptyC => ys
    case xs: SingleC[T] => new <>(xs, ys)
    case _ <> _ => new Append(xs, ys)
    case xs: Append[T] => append(xs, ys)
  }  
  
     
  private def append[T](xs: Append[T], ys: Conc[T]): Conc[T] = {
    if (xs.right.level > ys.level) new Append(xs, ys)
    else {
      val zs = new <>(xs.right, ys)
      xs.left match {
        //case ws @ Append(_, _) => append(ws, zs)
        //case ws if ws.level <= zs.level => ws <> zs
        case ws => new Append(ws, zs)
      }
    }
  }  
  
	def main(args: Array[String]) {
		
	}
}