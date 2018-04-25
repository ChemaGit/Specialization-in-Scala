package parallel_programming.week_4

object ImplementingCombiners {
  
  /**
   * Builders are used in sequential collection methods
   */
  trait Builder[T, Repr] {
    def +=(elem: T): this.type
    def result: Repr
  }
  
  /**
   * Combiners
   * when Repr is a set or a  map, combine represents union
   * when Repr is a sequence, combine represents concatenation
   */
  trait Combiner[T, Repr] extends Builder[T, Repr]{
    def combine(that: Combiner[T, Repr]): Combiner[T, Repr]
  }
  
  /**
   * Is the method combine efficient?
   * Arrays cannot be efficiently concatenated.
   */
  def combine(xs: Array[Int], ys: Array[Int]): Array[Int] = {
    val r = new Array[Int](xs.length + ys.length)
    Array.copy(xs, 0, r, 0, xs.length)
    Array.copy(ys, 0, r, xs.length, ys.length)
    r
  }  
  
	def main(args: Array[String]) {
		
	}
}