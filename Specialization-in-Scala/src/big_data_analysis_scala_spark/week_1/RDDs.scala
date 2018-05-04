package big_data_analysis_scala_spark.week_1

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext

object RDDs {
  /**
   * RDDs seem a lot like immutable sequential or parallel Scala collections.
   * map[B](f: A=> B): List[B] // Scala List
	 * map[B](f: A=> B): RDD[B] // Spark RDD
   * flatMap[B](f: A=> TraversableOnce[B]): List[B] // Scala List
   * flatMap[B](f: A=> TraversableOnce[B]): RDD[B] // Spark RDD
   * filter(pred: A=> Boolean): List[A] // Scala List
   * filter(pred: A=> Boolean): RDD[A] // Spark RDD
   * reduce(op: (A, A)=> A): A// Scala List
   * reduce(op: (A, A)=> A): A// Spark RDD
   * fold(z: A)(op: (A, A)=> A): A// Scala List
   * fold(z: A)(op: (A, A)=> A): A// Spark RDD
   * aggregate[B](z: => B)(seqop : (B, A)=> B, combop : (B, B) => B): B // Scala
   * aggregate[B](z: B)(seqop: (B, A)=> B, combop: (B, B) => B): B // Spark RDD
   */
  abstract class RDD[T] {
    def map[U](f: T => U): RDD[U]
    def flatMap[U](f: T => TraversableOnce[U]): RDD[U]
    def filter(f: T => Boolean): RDD[T]
    def reduce(f: (T, T) => T): T    
  }

  
	def main(args: Array[String]) {
	  val sconf = new SparkConf()
	  val sc = new SparkContext(sconf)
    sc.setLogLevel("ERROR")

    val myDir = "/loudacre/activations"
    val encyclopedia = sc.textFile(myDir)
	  	  
	  /**
		* Example:
    * Given, val encyclopedia: RDD[String], say we want to search all of
    * encyclopedia for mentions of EPFL, and count the number of pages that
    * mention EPFL.	  
		*/
    val result = encyclopedia.filter(page => page.contains("EPFL")).count()	
    
    /**
     * "Word count" is the Hello world! of programming with large-scala data
     */
    //Create a RDD
    val rdd = sc.textFile("dir", 4)
    //separate lines into words and include something to count
    val rddSplit = rdd.flatMap(line => line.split(" "))
    //include something to count
    val count = rddSplit.map(word => (word, 1))
    //sum up the 1s in the pairs
    val sum = count.reduceByKey{(v1, v2) => v1 + v2}
	  
	  sum.take(10).foreach(println)
    
    sc.stop()

	  
	  /**
	   * => parallelize: convert a local Scala collection to an RDD .
     * => textFile: read a text file from HDFS or a local file system and return an RDD of String
	   */
    
	}
}