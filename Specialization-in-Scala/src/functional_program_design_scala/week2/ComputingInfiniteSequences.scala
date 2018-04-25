package functional_program_design_scala.week2

object ComputingInfiniteSequences {
  
  /**
   * The infinite stream of all natural numbers
   * starting from a given number
   */
  def from(n: Int): Stream[Int] = n #:: from(n + 1)  
  
  /**
   * The stream of all multiples of 4
   * starting from a given number
   */
  def multFour(n: Int): Stream[Int] = 
    from(n).map(x => x * 4)
    
  /**
   * The Sieve of Eratothenes to calculate prime numbers
   * Start with all integers from 2, the first prime number
   * Eliminate all multiples of 2
   * The first element of the resulting list is 3, a prime number
   * Eliminate all multiples of 3
   * Iterate forever. At each step, the first number in the list is a
   * prime number and we eliminate all its multiples.  
   */
  def sieve(s: Stream[Int]): Stream[Int] =
    s.head #:: sieve(s.tail.filter(p => p % s.head != 0))
    
  /**
   * Back to square roots
   * With streams we can now express the concept of a converting
   * sequence without having to worry about when to terminate it:  
   */
  def isGoodEnough(guess: Double, x: Double) =
    math.abs((guess * guess - x) / x) < 0.0001
    
  def sqrtStream(x: Double): Stream[Double] = {
    def improve(guess: Double) = (guess + x / guess) / 2
    lazy val guesses: Stream[Double] = 1 #:: (guesses.map(i => improve(i)))
    guesses
  }
  
  /**
   * The stream of multiples of a given number
   */
  def multOfN(n: Int): Stream[Int] =
    from(1).map(f => f * n)
    
  /**
   * The stream of multiples of a given number
   */
  def multOfNs(n: Int): Stream[Int] =
    from(1).filter(f => f % n == 0)    
  
	def main(args: Array[String]) {
		//The stream of all natural numbers
    val nats = from(0)
    println(nats.take(10).toList)
    //The stream of all multiples of four
    val mult = multFour(1)
    println(mult.take(10).toList)
    //The sieve of Eratosthenes
    val primes = sieve(from(2))
    println(primes.take(10).toList)
    //Square Roots
    val square = sqrtStream(4).filter(s => isGoodEnough(s, 4))
    println(square.take(5).toList)
    //The stream of multiples of a given number
    val m = multOfN(5)
    println(m.take(10).toList)
    val m2 = multOfNs(5)
    println(m2.take(10).toList)            
	}
}