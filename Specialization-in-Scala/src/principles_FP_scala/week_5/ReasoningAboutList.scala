package principles_FP_scala.week_5

object ReasoningAboutList {
  
  def factorial(n: Int): Int =
    if(n == 0) 1
    else n* factorial(n - 1)
    
  def power(n: Int, p: Int): Int =
    if (n == 0) 0
    else if(p == 0) 1
    else if (p == 1) n
    else n * power(n , p - 1)
    
  def concat[T](xs: List[T], ys: List[T]): List[T] = xs match {
      case List() => ys
      case x :: xs1 => x :: concat(xs1, ys)
  }
  
	def main(args: Array[String]) {
		println(power(0, 5))
		println(power(1, 0))
		println(power(2, 0))
		println(power(1, 5))
		println(power(2, 1))
		println(power(2, 5))
	}
}