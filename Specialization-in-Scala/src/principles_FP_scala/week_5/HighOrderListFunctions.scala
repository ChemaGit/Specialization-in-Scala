package principles_FP_scala.week_5

object HighOrderListFunctions {
  
  def scaleList(xs: List[Double], factor: Double): List[Double] = xs match {
    case List() => xs
    case y :: ys => y :: factor :: scaleList(ys, factor)
  }
  
  /**
   * Using map, scaleList can be written more concisey
   */
  def scaleListB(xs: List[Double], factor: Double) = 
    xs map (x => x * factor)
  
  def squareList(xs: List[Int]): List[Int] = xs match {
    case List() => xs
    case y :: ys => y * y :: squareList(ys)
  }
  
  def squareListB(xs: List[Int]) =
    xs map (x => x * x)
  
  def posElems(xs: List[Int]): List[Int] = xs match {
    case List() => xs
    case y :: ys => if(y > 0) y :: posElems(ys) else posElems(ys)
  }
  
  /**
   * Using filter, posElems can be written more concisely
   */
  def posElemsB(xs: List[Int]): List[Int] =
    xs filter (x => x > 0)
  
  def pack[T](xs: List[T]): List[List[T]] = xs match {
    case List() => List()
    case x :: xs1 => 
      val (first, rest) = xs span (y => y == x)
      first :: pack(rest)
  }
  
  def encode[T](xs: List[T]): List[(T, Int)] =
    pack(xs) map(ys => (ys.head, ys.length))
  
	def main(args: Array[String]) {
		val xs = List(9,6,-5,2,7)
		val fruit = List("apple","pear","orange","pineapple")		
		val data = List("a","a","a","a","b","c","c","a")
		
		println(xs filter(x => x > 0))
		println(xs filterNot(x => x > 0))
		println(xs partition(x => x > 0))
		
		println(xs takeWhile(x => x > 0))
		println(xs dropWhile(x => x > 0))
		println(xs span(x => x > 0))
		
		println(pack(data))
		println(encode(data))
	}
}