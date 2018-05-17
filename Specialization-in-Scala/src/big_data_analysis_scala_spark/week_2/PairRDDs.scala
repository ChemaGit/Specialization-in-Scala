package big_data_analysis_scala_spark.week_2

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext

object PairRDDs {
  
  val sconf = new SparkConf
  val sc = new SparkContext(sconf) 
  
  /**
   * Distributed Key-Value Pairs ==> Pair RDDs
   * In single-node Scala, key-value pairs can be thought of as maps.
   * Most common in world of big data processing:
   * Operating on data in the form of key-value pairs.
   * 
   * We realized that most of our computations involved applying a map 
   * operation to each logical "record" in our input in order to
   * compute a set of intermediate key/value pairs, and then
   * applying a reduce operation to all the values that shared
   * the same key, in order to combine the derived data a
   * appropriately.
   * 
   * Example: In a JSON record, it may be desirable to create an RDD of properties of type: 
   * RDD[(String, Property)] where String is a key representing a value, and Property the
   * properties of that value.
   */  
  case class Property(street: String, city: String, state: String)
  //Where instances of Properties can be grouped by their respective cities and 
  //represented in a RDD of key-value pairs.
  
  /**
   * Pair RDDs allow you to act on each key in parallel or 
   * regroup data across the network.
   * RDD[(K, V)] // <== treated specially by Spark!
   * 
   * Some of the most important extension methods for RDDs containing pairs are:
   * def groupByKey(): RDD[(K, Iterable[V])]
   * def reduceByKey(func: (V, V) => V): RDD[(K, V)]
   * def join[W](other: RDD[(K, W)]): RDD[(K, (V, W))]
   * 
   * Creating a Pair RDD
   */  
  val largeList = List(('a', 2),('a', 5),('z', 7),('x', 1),('c', 2),('b', 2))
  val rdd = sc.parallelize(largeList) //Has type: org.apache.spark.rdd.RDD[(Char, Int)]
  val pairRdd = rdd.map(t => (t._1, t))
  /**
   * Once created, we can now use transformations specific to key-value pairs
   * such as reduceByKey, groupByKey, and join.
   */
  
	def main(args: Array[String]) {
    sc.setLogLevel("ERROR")
    
		sc.stop()		
	}
}