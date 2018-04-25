package principles_FP_scala.week_3

object MainList {
  
  def singleton[T](elem: T) = new ConsL[T](elem, new NilL[T])
  
  def nth[T](n: Int, xs: ListL[T]): T =
    if(xs.isEmpty) throw new IndexOutOfBoundsException
    else if(n == 0) xs.head
    else nth(n - 1, xs.tail)
    
  def nth_b[T](n: Int, list: ListL[T]): T = {
    def loop(cont: Int, xs: ListL[T]): T =
      if(xs.isEmpty) throw new IndexOutOfBoundsException
      else if(cont == n) xs.head
      else loop(cont + 1, xs.tail)
    loop(0, list)  
  }
  
  def main(args: Array[String]) {
    val list = new ConsL("MotherFucker", new ConsL("Mooron", new ConsL("Don't Be such a Fool", new NilL)))
    
    println(nth(1, list))
    println(nth_b(2, list))
  }
}

trait ListL[T] {
  def isEmpty: Boolean
  def head: T
  def tail: ListL[T]
  
  override def toString = (head + " " + tail)
}

class ConsL[T](val head: T, val tail: ListL[T])extends ListL[T] {
  def isEmpty = false
}

class NilL[T] extends ListL[T] {
  def isEmpty: Boolean = true
  def head: Nothing = throw new NoSuchElementException("Nil.head")
  def tail: Nothing = throw new NoSuchElementException("Nil.tail")
}