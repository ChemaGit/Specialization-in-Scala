package parallel_programming.week_2

object Associativity {
  
  def f(u: Double, v: Double): Double =
    (u + v)/(1.0 + u*v)
  def err(lst:List[Double]): Double =
    lst.reduceLeft(f) - lst.reduceRight(f)
  def testAssoc: Double = {
    val r = new scala.util.Random
    val lst = List.fill(400)(r.nextDouble*0.002)
    err(lst)  
  }  
  
	def main(args: Array[String]) {
		println(testAssoc)
		println(testAssoc)
		println(testAssoc)
		println(testAssoc)
	}
}