package parallel_programming.week_3

object SplittersAndCombiners {
  
  /**
   * The iterator contract
   * next can be called only if hasNext returns true
   * after hasNext returns false, it will always return false
   */
  trait Iterator[A] {
    def next(): A
    def hasNext: Boolean
  }
  
  //def iterator: Iterator[A] // On every collection
  
  /**
   * How would you implement foldLeft on an interator?
   */
  /*def foldLeft[B](z: B)(f: (B, A) => B): B = {
    var result = z
    while(hasNext) result = f(result, next())
    result
  }*/
  
  /**
   * Splitter contract
   * after calling split, the original splitter is left in an undefined state
   * the resulting splitters traverse disjoint subsets of the original splitter
   * remaining is an estimate on the number of remaining elements
   * split is an efficient method - O(log n) or better
   */
  trait Splitter[A] extends Iterator[A] {
    def split: Seq[Splitter[A]]
    def remaining: Int
  }
  //def splitter: Splitter[A] // On every parallel collection
  /**
   * How would you implement fold on a splitter?
   */
  /*def fold(z: A)(f: (A, A) => A): A = {
    if(remaining < threshold) foldLeft(z)(f)
    else {
      val children = for(child <- split) yield task {child.fold(z)(f)}
      children.map(_.join()).foldLeft(z)(f)
    }
  }*/
  
  /**
   * Builder contract
   * calling result returns a collection of type Repr, containing the
   * elements that were previously added with +=
   * calling result leaves the Builder in an undefined state
   */
  trait Builder[A, Repr] {
    def += (elem: A): Builder[A, Repr]
    def result: Repr
    def filter(x: A => Boolean): Repr
  }
  //def newBuilder: Builder[A, Repr] // On every collection
  /**
   * How would you implement the filter method using newBuilder?
   */
  /*def filter(p: T => Boolean): Repr = {
    val b = newBuilder
    for(x <- this) if (p(x)) b += x
    b.result
  }*/
  
  /** 
   * Combiner contract
   * calling combine returns a new combiner that contains elements of input combiners
   * calling combine leaves both original Combiners in an undefined state
   * combine is an efficient method - O(log n) or better
   */
  trait Combiner[A, Repr] extends Builder[A, Repr] {
    def combine(that: Combiner[A, Repr]): Combiner[A, Repr]
  }
  //def newCombiner: Combiner[T, Repr] //On every parallel collection
  
  /**
   * How would you implement a parallel filter method using splitter and newCombiner
   */
  
	def main(args: Array[String]) {
		
	}
}