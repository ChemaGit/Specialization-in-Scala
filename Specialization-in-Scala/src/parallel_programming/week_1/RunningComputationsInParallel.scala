package parallel_programming.week_1

object RunningComputationsInParallel {
  
  def sumSegment(a: Array[Int], p: Double, s: Int, t: Int): Int = {
    var i = s
    var sum: Int = 0
    while(i < t) {
      sum = sum + power(a(i), p)
      i = i + 1
    }
    sum
  }
  
  def power(x: Int, p: Double): Int = math.exp(p * math.log(math.abs(x))).toInt
  
  /**
   * How do we do this function in parallel?
   */
  def pNorm(a: Array[Int], p: Double): Int =
    power(sumSegment(a, p, 0, a.length), 1/p)
    
  /**
   * All we need to do is invoke sum segment twice. 
   * This is still sequential computation.
   * How do we make it  parallel?  
   */
  def pNormTwoPart(a: Array[Int], p: Double): Int = {
    val m = a.length / 2
    val (sum1, sum2) = (sumSegment(a, p, 0, m), sumSegment(a, p, m, a.length))
    power(sum1 + sum2, 1/p)
  }
  
  /**
   * All we need to do is to wrap the pair into the parallel construct!
   * Given a platform that supports parallel execution, the computations
   * with parallel may run up to twice as fast as the one without.
   */
  def pNormTwoPartInParallel(a: Array[Int], p: Double): Int = {
    val m = a.length / 2
    val (sum1, sum2) = parallel(sumSegment(a, p, 0, m), sumSegment(a, p, m, a.length))
    power(sum1 + sum2, 1/p)    
  }
  
  /**
   * How to process four array segments in parallel?
   * We divide the array into four segments.
   */
  def pNormTwoPartInParallelFour(a: Array[Int], p: Double): Int = {
    val m1 = a.length / 4
    val m2 = a.length / 2
    val m3 = 3 * a.length / 4
    val ((sum1, sum2), (sum3, sum4)) = parallel(parallel(sumSegment(a, p, 0, m1), sumSegment(a, p, m1, m2)),
                                       parallel(sumSegment(a, p, m2, m3), sumSegment(a, p, m3, a.length)))
    power(sum1 + sum2 + sum3 + sum4, 1/p)    
  }  
  
  /**
   * A recursive algorithm for an unbounded number of threads.
   */
  def pNormRec(a: Array[Int], p: Double): Int =
    power(segmentRec(a, p, 0, a.length), 1/p)
    
  /**
   * Like sumSegment but parallel  
   */
  def segmentRec(a: Array[Int], p: Double, s: Int, t: Int): Int = {
    if(t - s < 1 )//threshold: 1 is an example
      sumSegment(a, p, s, t) //small segment: do it sequentially
    else {
      val m = s + (t - s) / 2
      val (sum1, sum2) = parallel(segmentRec(a, p, s, m),
                                  segmentRec(a, p, m, t))
      sum1 + sum2                                  
    }
  }
  
	def main(args: Array[String]) {
		val a: Array[Int] = Array(1,2,3,4,5,6,7,8,9,10)
		println(pNormTwoPartInParallelFour(a, 5.0))
		println(pNormRec(a, 5.0))
	}
}