package parallel_programming.week_3

/**
 * Non-Parallelizable Operations
 * foldLeft, foldRight, reduceLeft, reduceRight, scanLeft and scanRight
 * must process the elements sequentially.
 */

object DataParallelOperations {
  
  /**
   * Implement the sum method
   * with the fold operation
   */
  def sum(xs: Array[Int]): Int = {
    xs.par.fold(0)(_ + _)
  }
  
  /**
   * Implement the max method
   * with the fold operation
   */
  def max(xs: Array[Int]): Int = {
    xs.par.fold(Int.MinValue)(math.max)
  }
  
  def play(a: String, b: String): String = List(a, b).sorted match {
    case List("paper", "scissors") => "scissors"
    case List("paper", "rock") => "paper"
    case List("rock", "scissors") => "rock"
    case List(a, b) if a == b => a
    case List("", b) => b
  }
  
	def main(args: Array[String]) {
		/**
		 * In Scala, most collection operations can become data-parallel
		 * The .par call converts a sequential collection to a parallel collection.
		 */
	  val p = (1 until 1000).par
	  .filter(n => n % 3 == 0)
	  .count(n => n.toString == n.toString.reverse)		
	  println(p)
	  println("****************************")
	  val arr = Array("paper", "rock", "paper", "scissors").par.fold("")(play)
	  /**
	   * Why does this happen?
	   * The play operator es commutative, but not associative
	   */
	  play(play("paper", "rock"), play("paper", "scissors")) == "scissors"
	  play("paper", play("rock", play("paper", "scissors"))) == "paper" 
	  
	  /**
	   * Given an array of characters, use fold to return the vowel count
	   */
	  val l: Array[Char] = Array('E','P','F','L')
	  //l.par.fold(0)( (c:Char, count: Int, ) => if(c.isDigit) count + 1 else count) //does not compile
	  //l.foldLeft(0)((c:Char, count: Int) => if(c.isDigit) count + 1 else count) //does not compile
	  /**
	   * Using the aggregate Operation
	   * Count the number of vowels in a character array
	   */
	  l.par.aggregate(0)( (count, c) => if(c.isDigit) count + 1 else count, _ + _)
	}
}