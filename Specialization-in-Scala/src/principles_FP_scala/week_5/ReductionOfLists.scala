package principles_FP_scala.week_5

object ReductionOfLists {
  
  def sum(xs: List[Int]): Int = xs match {
    case List() => 0
    case y :: ys => y + sum(ys)
  }
  
  /**
   * Using reduceLeft, we can simplify
   */
  
  def sumA(xs: List[Int]) = (0 :: xs) reduceLeft((x, y) => x + y)
  def product(xs: List[Int]) = (1 :: xs) reduceLeft((x, y) => x * y)
  
  /**
   * Sum and product can be also expressed in a shorter way like this
   */
  def sumB(xs: List[Int]) = (0 :: xs) reduceLeft(_ + _)
  def productA(xs: List[Int]) = (1 :: xs) reduceLeft(_ * _)
  
  /**
   * foldLeft is like reduceLeft but takes an additional parameter as accumulator
   * So sum and product can also be defined as follows
   */
  def sumC(xs: List[Int]) = (xs foldLeft 0) (_ + _)
  def productB(xs: List[Int]) = (xs foldLeft 1) (_ * _)
  
  /**
   * Here is another formulation of concat
   */
  def concatRight[T](xs: List[T], ys: List[T]): List[T] = (xs foldRight ys) (_ :: _)
  //def concatLeft[T](xs: List[T], ys: List[T]): List[T] = (xs foldLeft ys) (_ :: _) it isn't working
  
  /**
   * Function for reversing lists which has a linear cost
   */
  def reverse[T](xs: List[T]): List[T] = (xs foldLeft List[T]())((xs, x) => x :: xs)
  
  def mapFun[T, U](xs: List[T], f: T => U): List[U] = (xs foldRight List[U]())((x: T, ys: List[U]) => f(x) :: ys)
  
  def lengthFun[T] (xs: List[T]): Int = (xs foldRight 0)((xs, x:Int) => x + 1)
  
	def main(args: Array[String]) {
		val a = List(1,2,3,4,5)
		val b = List(6,7,8,9,10)
		println(concatRight(a,b))
		println(reverse(a))
		println(mapFun(a, ( (x: Int) => x * x * x)) )
		println(lengthFun(a))
		
	}
}