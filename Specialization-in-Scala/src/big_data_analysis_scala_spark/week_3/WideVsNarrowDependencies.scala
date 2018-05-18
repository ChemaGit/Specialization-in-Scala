package big_data_analysis_scala_spark.week_3

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext

object WideVsNarrowDependencies {
  
  val sconf = new SparkConf
  val sc = new SparkContext(sconf)   
  
  /**
   * Some transformations significantly more expensive(latency) than others.
   * requiring lots of data to be transferred over the network, sometimes unnecesarily.
   */
  
  /**
   * Lineages
   * Directed Acyclic Graph(DAG) is a lineage graph representing the computations done on the RDD.
   * DAG is what Spark analyzes to do optimizations.
   */
  
  /**
   * RDDs are represented as:
   * 1. Partitions: Atomic pieces of the dataset. One or many per compute node.
   * 2. Dependencies: Model relationship between this RDD and its partitions
   *    with the RDDs it was derived from.
   * 3. A function: for computing the dataset based on its parent RDDs.
   * 4. Metadata: about its partitioning scheme and data placement.   
   */
  
  /**
   * RDD dependencies encode when data must move across the network.
   * Transformations cause shuffles and can have two kinds of dependencies:
   * 1. Narrow Dependencies.
   * 		Each partition of the parent RDD is used by at most one partition of 
   *    the child RDD. Fast! No shuffle necessary.
   *    map, filter, union, join with co-partitioned inputs,
   *    mapValues, flatMap, mapPartions, mapPartitionsWithIndex
   * 2. Wide Dependencies.
   *    Each partition of the parent RDD may be depended on by multiple child partitions.
   *    Slow! Requires all or some data to be shuffled over the network.
   *    groupByKey, join with inputs not co-partitioned, cogroup,
   *    groupWith, leftOuterJoin, rightOuterJoin, reduceByKey,
   *    combineByKey, distinct, intersection, repartition, coalesce
   */
  
  /**
   * dependendencies method on RDDs
   * dependencies ==> return a sequence of Depency objects, 
   * which are acutually the dependencies used by Spark's scheduler
   * to know how this RDD depends on other RDDs. The sorts of dependency objects
   * the dependencies method may return include:
   * 1. Narrow dependency objects: OneToOneDependency, PruneDepndency, RangeDependency.
   * 2. Wide dependency objects: ShuffleDependency.
   */
  val wordsRdd = sc.parallelize(List("a","c","x","b","j","b","d","a","c","b"))  
  val pairs = wordsRdd.map(c => (c, 1))
                      .groupByKey()
                      .dependencies
  // pairs: Seq[org.apache.spark.Dependency[_]]
  // List(org.apache.spark.ShuffleDependency@4294a25d)   
 
  /**
   * toDebugString method on RDDs.
   * toDebugString ==> prints out a visualization of the RDD's lineage, and
   * other information pertinent to scheduling. For example, indentations in the 
   * output separate groups of narrow transformations that may be pipelined 
   * together with wide transformations that require shuffles. 
   * These groupings are called stages.                      
   */
  val wordsRddB = sc.parallelize(List("a","c","x","b","j","b","d","a","c","b"))  
  val pairsB = wordsRddB.map(c => (c, 1))
                      .groupByKey()
                      .toDebugString
  //pairs: String =
  //(8) ShuffledRDD[219] at groupByKey at <console>:38 []
  // +-(8) MapPartitionsRDD[218] at map at <console>:37 []
  //    | ParallelCollectionRDD[217] at parallelize at <console>:36 []
                      
  /**
   * Lineages graphs are the key to fault tolerance in Spark.
   * Recomputing missing partitions fast for narrow dependencies. 
   * But slow for wide dependencies.                      
   */
                      
	def main(args: Array[String]) {
    sc.setLogLevel("ERROR")
    
		sc.stop()			
	}
}