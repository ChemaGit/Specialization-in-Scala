package functional_program_design_scala_week_1

object FunctionalRandomGenerators {
  
  trait Generator[+T] {
    self =>
    
    def generate: T
    
    def map[S](f: T => S): Generator[S] = new Generator[S] {
      def generate = f(self.generate)
    }
    
    def flatMap[S](f: T => Generator[S]): Generator[S] = new Generator[S] {
      def generate = f(self.generate).generate
    }
  }
  
  val integers = new Generator[Int] {    
    def generate = scala.util.Random.nextInt()
  }
  
  /**
   * Booleans Generators
   */
  
		val boole = for (x <- integers) yield x > 0
		val booleanos = integers.map(x => x > 0)
		val bool = new Generator[Boolean] {
		  def generate = ( (x: Int) => x > 0)(integers.generate)
		}
		//The best way
    val boolean = new Generator[Boolean] {
      def generate = integers.generate > 0
    }		  
    
    val booleans = integers.map(_ >= 0)
  
  /**
   * Pairs Generators
   */
  val pairssss = new Generator[(Int, Int)] {
    def generate = (integers.generate, integers.generate)
  }
  
  def pair[T, U](t: Generator[T], u: Generator[U]) = t.flatMap{
    x => u.map{y => (x, y)}
  }
  
  def pairss[T, U](t: Generator[T], u: Generator[U]) = t.flatMap{
    x => new Generator[(T, U)] {def generate = (x, u.generate)}
  }
  
  def pairsss[T, U](t: Generator[T], u: Generator[U]) = new Generator[(T, U)] {
    def generate = (new Generator[(T, U)] {
      def generate = (t.generate, u.generate)
    }).generate
  }
  
  //the best way
  def pairs[T, U](t: Generator[T], u: Generator[U]) = new Generator[(T, U)] {
    def generate = (t.generate, u.generate)
  }
  
  /**
   * Generator Examples
   */
  def single[T](x: T): Generator[T] = new Generator[T] {
    def generate = x
  }
  
  def choose(lo: Int, hi: Int): Generator[Int] = 
    for(x <- integers)yield lo + x % (hi - lo)
    
  def oneOf[T](xs: T*): Generator[T] =
    for(idx <- choose(0, xs.length)) yield xs(idx)
  
  /**
   * A List Generator
   */
  def lists: Generator[List[Int]] = for {
    isEmpty <- booleans
    list <- if(isEmpty) emptyLists else nonEmptyLists
  }yield list
  
  def emptyLists = single(Nil)
  
  def nonEmptyLists = for {
    head <- integers
    tail <- lists
  }yield head :: tail
  
  /**
   * A Tree Generator
   */
  trait Tree
  case class Inner(left: Tree, right: Tree)extends Tree
  case class Leaf(x: Int) extends Tree 
    
  def leafs: Generator[Leaf] = for {
    x <- integers
  } yield Leaf(x)
  
  def inners: Generator[Inner] = for {
    l <- trees
    r <- trees
  } yield Inner(l, r)
  
  def trees: Generator[Tree] = for {
    isLeaf <- booleans
    tree <- if(isLeaf) leafs else inners
  } yield tree
  
  /**
   * Unit test
   */
  def test[T](g: Generator[T], numTimes: Int = 100)
    (test: T => Boolean): Unit = {
      for (i <- 0 until numTimes) {
        val value = g.generate
          assert(test(value), "test failed for " + value)
      }
      println("passed " + numTimes + " tests")
    }  
  
  
	def main(args: Array[String]) {    
   def forAll { (l1: List[Int], l2: List[Int]) =>
     l1.size + l2.size == (l1 ++ l2).size
   }  
    val exam = List("Ava Addams", "Kendra Lust", "Brandi Love") 
    println("=>" + oneOf(exam))
    println("What????")
    println(trees.generate)
    
    test(pairs(lists, lists)) {
      case (xs, ys) => (xs ++ ys).length > xs.length
    }    
    
	}
}