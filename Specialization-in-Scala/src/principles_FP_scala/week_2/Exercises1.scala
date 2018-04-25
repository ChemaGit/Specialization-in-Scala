package principles_FP_scala.week_2

object Exercises1 {
  val tolerance = 0.0001
	def main(args: Array[String]) {
    println(sqrt(25))		
    println("***********************")
    println(sqrtB(25))
	}
	
  def abs(x: Double): Double =
    if(x < 0) -x else x
  
	/**
	 * Finding a fixed point of a function
	 */
	def isCloseEnough(x: Double, y: Double) =
	  abs((x - y) / x) / x < tolerance
	
	def fixedPoint(f: Double => Double)(firstGuess: Double) = {
    def iterate(guess: Double): Double = {
      val next = f(guess)
      println(next)
      if(isCloseEnough(guess, next)) next
      else iterate(next)
    }
    iterate(firstGuess)
  }
  def sqrt(x: Double) = fixedPoint(y => (y + x / y) / 2)(1.0)
  
	/**********************************************************/
  
	def fixedPointB(f: Double => Double)(firstGuess: Double) = {
    def iterate(guess: Double): Double = {
      val next = f(guess)
      println(next)
      if(isCloseEnough(guess, next)) next
      else iterate(next)
    }
    iterate(firstGuess)
  }  
	def averageDamp(f: Double => Double)(x: Double) = (x + f(x)) / 2
  def sqrtB(x: Double) = fixedPointB(averageDamp(y => x / y))(1.0)    
  
}