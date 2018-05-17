package big_data_analysis_scala_spark.week_3

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.RangePartitioner

object Partitioning {
  
  val sconf = new SparkConf
  val sc = new SparkContext(sconf) 
  
  /**
   * PARTITIONS
   * The data within an RDD is split into several partitions.
   * Properties of partitions:
   * 1. Partitions never span multiple machines, i. e. , tuples in the same partition are guaranteed to be on the same machine.
   * 2. Each machine in the cluster contains one or more partitions.
   * 3. The number of partitions to use is configurable. By default, it equals the total number of cores on all executor nodes.
   * 
   * Two kinds of partitioning available in Spark:
   * Hash partitioning
   * Range partitioning
   * 
   * Customizing a partitioning is only possible on Pair RDDs.
   */
  
  /**
   * Hash partitioning
   */
  case class CFFPurchase(customerid: Int, destination: String, price: Double)
  val purchases = List(CFFPurchase(100, "Geneva", 22.25),
                        CFFPurchase (300, "Zurich", 42.10),
                        CFFPurchase(100, "Fribourg", 12.40),
                        CFFPurchase (200, "St. Gallen", 8.20),
                        CFFPurchase(100, "Lucerne", 31.60),
                        CFFPurchase (300, "Basel", 16.20))  
  val purchasesRdd = sc.parallelize(purchases)
  val purchasesPerMonth = purchasesRdd.map(p => (p.customerid, p.price)) // Pair RDD
                                      .groupByKey() // groupByKey returns RDD[K, Iterable[V] ]
                                      .map(p => (p._1, (p._2.size, p._2.sum)))
                                      .collect()
  //groupByKey first computes per tuple (k, v) its partition p:
  //p = k.hashCode() % numPartitions
  //Then, all tuples in the same partition p are sent to the machine hosting p.
  //Intuition: hash partitioning attempts to spread data evenly across partitions based on the key.   
                                      
  /**
   * Range partitioning     
   * Pair RDDs may contain keys that have an ordering defined. 
   * For such RDDs, range partitioning may be more efficient.    
   * Using a range partitioner, keys are partitioned according to:
   * 1. an ordering for keys
   * 2. a set of sorted ranges of keys
   * 
   * Property: tuples with keys in the same range appear on the same machine.                            
   */
                                      
  /**
   * Hash Partitioning: Example      
   * Consider a Pair RDD, with keys [8, 96, 240, 400, 401, 800], and a
   * desired number of partitions of 4.
   * suppose that hashCode () is the identity (n.hashCode () == n). 
   * hash partitioning distributes the keys as follows among the partitions:
   * partition 0: [8, 96, 240, 400, 800]
   * partition 1: [401]
   * partition 2: []
   * partition 3: []   
   * The result is a very unbalanced distribution which hurts performance.                            
   */
  
  /**
   * Range Partitioning: Example   
   * Using range partitioning the distribution can be improved significantly:
   * - Assumptions: (a) keys non-negative, (b) 800 is biggest key in the RDD
   * - Set of ranges: [1 , 200], [201 , 400], [401 , 600], [601 , 800]  
   * In this case, range partitioning distributes the keys as follows among the partitions:
   * partition 0: [8, 96]
   * partition 1: [240, 400]
   * partition 2: [401]
   * partition 3: [800]
   * The resulting partitioning is much more balanced.                                  
   */
                                      
  /**
   * Partitioning Data
   * Two ways:
   * 1. Call partitionBy on an RDD, providing an explicit Partitioner.
   * 2. Using transformations that return RDDs with specific partitioners.                                      
   */
                                      
  /**
   * Creating a RangePartitioner requires:
   * 1. Specifying the desired number of partitions.
   * 2. Providing a Pair RDD with ordered keys. This RDD is sampled to
   *    create a suitable set of sorted ranges.                                      
   */
  //Invoking partitionBy creates an RDD with a specified partitioner.                                      
  val pairs = purchasesRdd.map(p => (p.customerid, p.price)) 
  val tunedPartitioner = new RangePartitioner(8, pairs)
  val partitioned = pairs.partitionBy(tunedPartitioner).persist() //The result of partitionBy must be persisted
  
  /**
   * Partitioning Data Using Transformations
   * - Partitioner from parent RDD:
   * Pair RDDs that are the result of a transformation on a partitioned Pair
   * RDD typically is configured to use the hash partitioner that was used to construct it.
   * - Automatically-set partitioners:
   * Some operations on RDDs automatically result in an RDD with a known partitioner - for when it makes sense.
   * For example, by default, when using sortByKey, a RangePartitioner is
   * used. Further, the default partitioner when using groupByKey, is a
   * HashPartitioner, as we saw earlier.
   * - Operations on Pair RDDs that hold to (and propagate) a partitioner:
   * cogroup, foldByKey, groupWith, combineByKey
   * join, partitionBy, leftOuterJoin, sort
   * rightOuterJoin, mapValues(if parent has a partitioner)
   * groupByKey, flatMapValues(if parent has a partitioner)
   * reduceByKey, filter(if parent has a partitioner)
   * 
   * All other operations will produce a result without a partitioner. Why?
   * rdd.map((k: String, v: Int)=> ("doh!", v))
   * Because it's possible for map to change the key.
   * 
   * mapValues still do transformations without changing the keys,
   * thereby preserving the partitioner.
   */
  
	def main(args: Array[String]) {
    sc.setLogLevel("ERROR")
    
		sc.stop()				
	}
}