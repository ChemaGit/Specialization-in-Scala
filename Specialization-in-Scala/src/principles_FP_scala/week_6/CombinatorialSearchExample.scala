package principles_FP_scala.week_6

object CombinatorialSearchExample {
  
  def show(queens: List[Int]) = {
    val lines =
      for(col <- queens.reverse)
      yield Vector.fill(queens.length)("* ").updated(col, "X ").mkString
    ("\n" + (lines.mkString("\n")))    
  }
  
  def isSafe(col: Int, queens: List[Int]): Boolean = {
    val row = queens.length
    val queensWithRow = (row - 1 to 0 by -1) zip queens
    queensWithRow forall {
      case (r, c) => col != c && math.abs(col - c) != row - r
    }
  }
  
  def queens(n: Int) = {
    def placeQueens(k: Int): Set[List[Int]] = {
      if(k == 0) Set(List())
      else
        for {
          queens <- placeQueens(k - 1)
          col <- 0 until n
          if isSafe(col, queens)
        }yield col :: queens
    }
    placeQueens(n)
  }
  
	def main(args: Array[String]) {
		val fruit = Set("apple", "banana", "pear")
		val s = (1 to 6).toSet
		println(s.mkString(","))
		println(fruit.filter(_.startsWith("app")))
		println(s.map(_ + 2))
		println(s.nonEmpty)
		println(s.map(_ / 2))
		println(s.contains(5))
		println(queens(8).map(show).mkString("\n"))
	}
}