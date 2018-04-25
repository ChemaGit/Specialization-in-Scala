package principles_FP_scala.week_6

object OtherCollections {
  
  /**
   * To compute the scalar product of two vectors
   */
  def scalarProduct(xs: Vector[Double], ys: Vector[Double]): Double =
    (xs.zip(ys).map(xy => xy._1 * xy._2)).sum
    
  /**
   * An alternative way to write this is with a pattern matching function  
   */
  def scalarProductB(xs: Vector[Double], ys: Vector[Double]): Double =
    (xs.zip(ys).map{case(x, y) => x * y}).sum    
    
  /**
   * A number is prime if the only divisors of n are 1 and n itself  
   */
  def isPrime(n: Int): Boolean =
    (2 until n).forall(d => n % d != 0)
  
	def main(args: Array[String]) {
    val nums2 = Vector(4.0, 6, 8, -10)
		val nums = Vector(1, 2.0, 3, -88)
		println(scalarProduct(nums, nums2))
		val people = Vector("Bob", "James", "Peter")
		val xs: Array[Int] = Array(1,2,3)
		val ys: String = "Hello World"
		val zip = xs.zip(people)
		
		println(xs.exists(x => x > 3))
		println(xs forall (x => x > 0))
		println((xs.zip(people)).mkString(","))
		println((zip.unzip).toString())
		println( (xs map (x => 2 * x)).mkString(","))
		println(ys filter (_.isUpper))
		println( (xs.flatMap((x: Int) => x + "2")).mkString("") )
		println(xs.sum)
		println(xs.product)
		println(xs.max)
		println(xs.min)
		
		val r: Range = 1 until 5 //until - exclusive
		val s: Range = 1 to 5 //to - inclusive
		val z: Range = 1 to 10 by 3 //by - way of each step
		val p: Range = 6 to 1 by -2
		
		println( (r.flatMap(x => z.map(y => (x,y))).mkString("")))
		
		println(r)
		println(s)
		println(z)
		println(p)
		println(isPrime(5))
		println(isPrime(54897))
		println(isPrime(17))
		println(ys.flatMap(c => List('.', c)))
	}
}