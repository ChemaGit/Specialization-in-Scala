package functional_program_design_scala.week2

object LazyEvaluation {
  
  def streamRange(lo: Int, hi: Int): Stream[Int] =
    if(lo >= hi) Stream.empty
    else Stream.cons(lo, streamRange(lo + 1, hi))  
  
  def isPrime(n: Int): Boolean =
    (2 until n).forall(d => n % d != 0)    
  
  def expr = {
    val x = {print("x"); 1}
    lazy val y = {print("y"); 2}
    def z = {print("z"); 3}
    z + y + x + z + y + x
  }
  
  def nthPrimeStreamLazy(from: Int, to: Int, n: Int): Int = 
    (streamRange(1000, 10000).filter(isPrime).apply(n - 1))
  
	def main(args: Array[String]) {
		lazy val x = expr
		println(expr)
		println(x)
		println
		expr
		println
		println(nthPrimeStreamLazy(1000, 10000, 2))
	}
}