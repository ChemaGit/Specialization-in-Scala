package principles_FP_scala.week_5

object PairsAndTuples {
  def msort(xs: List[Int]): List[Int] = {
    val n = xs.length / 2
    if(n == 0) xs
    else {
      def merge(xs: List[Int], ys: List[Int]): List[Int] = 
        xs match {
        case List() => ys
        case x :: xs1 => ys match {
          case List() => xs
          case y :: ys1 => if(x < y) x :: merge(xs1, ys)
                           else y :: merge(xs, ys1)
        }
      }
      
      val (fst, snd) = xs splitAt n
      merge(msort(fst), msort(snd))
    }
  }
  
  /**
   * A better way to write a merge function.
   */
  def merge(xs: List[Int], ys: List[Int]): List[Int] = {
    (xs, ys) match {
      case (List(), ys) => ys
      case (xs, List()) => xs
      case (x :: xs1, y :: ys1) =>
        if(x < y) x :: merge(xs1, ys)
        else y :: merge(xs, ys1)
    }
  }
  
	def main(args: Array[String]) {
		val l = List(3, 2, 1, 4, 7, 5, 6)
		println(msort(l).mkString(","))
	}
}