package big_data_analysis_scala_spark.week_2

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext

object ReductionsOperations {
  val sconf = new SparkConf
  val sc = new SparkContext(sconf)
  
  /**
   * Reduction Operations:
   * walk through a collection and combine neigboring elements of the
   * collection together to produce a single combined result.
   */  
  case class Taco(kind: String, price: Double)
  val tacoOrder = List(Taco("Carnitas", 2.25),
                       Taco("Corn", 1.75),
                       Taco("Barbacoa", 2.50),
                       Taco("Chicken", 2.00))
  val cost = tacoOrder.foldLeft(0.0)((sum, taco)=> sum + taco.price) //foldLeft is not parallelizable.
  
  /**
   * def fold(z: A)(f: (A, A)=> A): A
   * It enables us to parallelize using a single function f by enabling us
   * to build parallelizable reduce trees.
   */
  
  /**
   * aggregate[B](z: => B)(seqop: (B, A)=> B, combop: (B, B) => B): B
   * Properties of aggregate
   * 1. Parallelizable.
   * 2. Possible to change the return type.
   */
  
  /**
   * Reduction Operation s on RDDs
   * Scala collections                   Spark
   * fold                                fold
   * foldLeft/foldRight                  reduce
   * reduce                              aggregate
   * aggregate
   * 
   * In Spark, aggregate is a more desirable reduction operator a majority of the time.
   */
  
  //I might only care about title and timestamp, for example. In this case, it'd save a lot of
  //time/memory to not have to carry around the full-text of each article {text) in our
  //accumulator!
  case class WikipediaPage(
    title: String,
    redirectTitle: String,
    timestamp: String,
    lastContributorUsername: String,
    text: String)
  //Hence, why accumulate is often more desirable in Spark than in Scala collections!  
  
  
	def main(args: Array[String]) {
    sc.setLogLevel("ERROR")
    
		sc.stop()
	}
}