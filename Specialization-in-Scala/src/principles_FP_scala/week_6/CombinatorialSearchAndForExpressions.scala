package principles_FP_scala.week_6

object CombinatorialSearchAndForExpressions {
  
  /**
   * A number is prime if the only divisors of n are 1 and n itself  
   */
  def isPrime(n: Int): Boolean =
    (2 until n).forall(d => n % d != 0)  
  
  /**
   * With for expressions.
   * Given a positive integer n, find all the pairs of positive integers(i, j)
   * such that 1 <= j < i < n, and i + j is prime.
   */
  def combinatorialSearch(n: Int) =
    for {
      i <- 1 until n
      j <- 1 until i
      if isPrime(i + j)
    } yield (i, j)
    
  /**
   * With high order functions.
   * Given a positive integer n, find all the pairs of positive integers(i, j)
   * such that 1 <= j < i < n, and i + j is prime.
   */    
  def combinatorialSearchB(n: Int) = 
    ((1 until n) map (i => (1 until i) map (j => (i, j)))).flatten.filter{case (x,y) => isPrime(x + y)}
    
  /**
   * Another way
   * With high order functions.
   * Given a positive integer n, find all the pairs of positive integers(i, j)
   * such that 1 <= j < i < n, and i + j is prime.
   */    
  def combinatorialSearchC(n: Int) = 
    ((1 until n) flatMap (i => (1 until i) map (j => (i, j)))).filter{case (x,y) => isPrime(x + y)}    
  
  /**
   * With high order functions.
   * To compute the scalar product of two vectors
   */
  def scalarProduct(xs: Vector[Double], ys: Vector[Double]): Double =
    (xs.zip(ys).map(xy => xy._1 * xy._2)).sum
    
  /**
   * With high order functions.
   * An alternative way to write this is with a pattern matching function  
   */
  def scalarProductB(xs: Vector[Double], ys: Vector[Double]): Double =
    (xs.zip(ys).map{case(x, y) => x * y}).sum    
    
  /**
   * With for expressions.
   * An alternative way to write this is with a pattern matching function  
   */
  def scalarProductC(xs: Vector[Double], ys: Vector[Double]): Double =
    (for ((x, y) <- xs zip ys) yield x * y).sum
  
	def main(args: Array[String]) {
    val nums2 = Vector(4.0, 6, 8, -10)
		val nums = Vector(1, 2.0, 3, -88)
		println(scalarProductC(nums, nums2))      
    println("*************")  
		val primes = List(combinatorialSearch(10))
		println(primes.mkString(","))
		val primesB = List(combinatorialSearchB(10))
		println(primesB.mkString(","))
		val primesC = List(combinatorialSearchC(10))
		println(primesC.mkString(","))		
	}
}