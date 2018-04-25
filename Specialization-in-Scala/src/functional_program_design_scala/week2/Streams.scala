package functional_program_design_scala.week2

object Streams {
  
  def isPrime(n: Int): Boolean =
    (2 until n).forall(d => n % d != 0)  
    
  def secondPrime(from: Int, to: Int) = nthPrime(from, to, 2)
  
  def nthPrime(from: Int, to: Int, n: Int): Int =
    if(from >= to) throw new Error("no prime")
    else if(isPrime(from))
      if(n == 1) from else nthPrime(from + 1, to, n - 1)
    else nthPrime(from + 1, to, n)  
  
    def nthPrimeShorter(from: Int, to: Int, n: Int): Int = {
      ((from to to).filter(isPrime))(n - 1)
    }
    
  /**
   * listRange will produce a list with end - start elements and return it.  
   */
  def listRange(lo: Int, hi: Int): List[Int] =
    if(lo >= hi) Nil
    else lo :: listRange(lo + 1, hi)
    
  /**
   * STREAMS  
   * streamRange returns a single object of type Stream
   * with start as head element.
   * The other elements are only computed when they are needed, 
   * where needed means that someone calls tail on the stream.
   */
  def streamRange(lo: Int, hi: Int): Stream[Int] =
    if(lo >= hi) Stream.empty
    else Stream.cons(lo, streamRange(lo + 1, hi))
      
  def nthPrimeStream(from: Int, to: Int, n: Int): Int = 
    ((from to to).toStream.filter(isPrime))(n - 1)
    
  /**
   * x :: xs always produces a list, never a stream
   * There is however an alternative operator #:: which produces a stream.
   * x #:: xs == Stream.cons(x, xs)
   * #:: can be used in expressions as well as patterns  
   */
  
  def streamRangeB(lo: Int, hi: Int): Stream[Int] = {
    print(lo + " ")
    if(lo >= hi) Stream.empty
    else Stream.cons(lo, streamRangeB(lo + 1, hi))
  }
	def main(args: Array[String]) {
		println(nthPrime(100, 300, 2))
		
		//Streams example
		val xs = Stream.cons(1, Stream.cons(2, Stream.empty))
		val s = Stream(1, 2, 3)
		val s1 = (1 to 10000).toStream
		println(xs)
		println(s)
		println(s1)	
		println(nthPrimeStream(100, 300, 2))
	  streamRangeB(1, 10).take(12).toList
	  println
	  streamRangeB(1, 10).take(3)
	}
}