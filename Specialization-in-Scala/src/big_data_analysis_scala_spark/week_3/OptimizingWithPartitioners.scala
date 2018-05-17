package big_data_analysis_scala_spark.week_3

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.RangePartitioner

object OptimizingWithPartitioners {
  
  val sconf = new SparkConf
  val sc = new SparkContext(sconf)  
  
  /**
   * Partitioning can bring substantial performance gains, especially in the face of shuffles.
   */
  
  /**
   * Optimization using range partitioning
   * Using range partitioners we can optimize our earlier use of reduceByKey so
   * that it does not involve any shuffling over the network at all!
   * almost a 9x speedup over the groupByKey example.
   * almost a 3x speedup over the reduceByKey without partitioner example.
   */
  case class CFFPurchase(customerid: Int, destination: String, price: Double)
  val purchases = List(CFFPurchase(100, "Geneva", 22.25),
                        CFFPurchase (300, "Zurich", 42.10),
                        CFFPurchase(100, "Fribourg", 12.40),
                        CFFPurchase (200, "St. Gallen", 8.20),
                        CFFPurchase(100, "Lucerne", 31.60),
                        CFFPurchase (300, "Basel", 16.20))  
  val purchasesRdd = sc.parallelize(purchases)  
  val pairs = purchasesRdd.map(p => (p.customerid, p.price))
  
  val tunedPartitioner = new RangePartitioner(8, pairs)  
  val partitioned = pairs.partitionBy(tunedPartitioner).persist()
  
  val purchasesPerCust = partitioned.map( p => (p._1, (1, p._2)))
  val purchasesPerMonth = purchasesPerCust.reduceByKey((v1, v2) => (v1._1 + v2._1, v1._2 + v2._2)).collect()  
  
  
  /**
   * Partitioning Data: partitionBy, Another Example
   */
  
  /*
  		val sc = new SparkContext( ... )
  		val userData = sc.sequenceFile[UserID, Userlnfo]("hdfs:// ... ").persist()
  		
  		def processNewlogs(logFileName: String) {
  			val events = sc.sequenceFile[UserID, Linklnfo](logFileName)
  			val joined = userData.join(events) //ROD of (UserID, (Userlnfo, Linklnfo))
  			val offTopicVisits = joined.filter {
  				case (userld, (userlnfo, linklnfo)) = > //Expand the tuple
  							!userlnfo.topics.contains(linklnfo.topic)
  			}.count()
  			println(''Number of visits to non-subscribed topics: '' + offTopicVisits)
  		}
  		
  		It will be very inefficient!
  		The join operation, called each time processNewLogs is invoked,
      does not know anything about how the keys are partitioned in the datasets.
      
      Just use partitionBy on the big userData RDD at the start of the program!
      
      val userData = sc.sequenceFile[UserID, Userlnfo]("hdfs:// ... ")
                                     .partitionBy(new HashPartitioner(100)) // Create 100 partitions
                                     .persist()
                                     
      Now that userData is pre-partitioned, Spark will shuffle only the events
      RDD, sending events with each particular UserID to the machine that
      contains the corresponding hash partition of userData.                                     
   */
  
  /**
   * How do I know a shuffle will occur?
   * Rule of thumb: a shuffle can occur when the resulting RDD depends on
   * other elements from the same RDD or another RDD.
   * 
   * You can also figure out whether a shuffle has been planned/executed via:
   * 1. The return type of certain transformations, e.g.,
   *    org.apache.spark.rdd.RDD[(String, Int)]= ShuffledRDD[l3661]
   *    
   * 2. Using function toDebugString to see its execution plan:
   *    partitioned.reduceByKey((v1, v2) => (v1 ._1 + v2._1, v1 ._2 + v2._2)).toDebugString
   *    res9: String =
   *    (8) MapPartitionsRDD[l6221] at reduceByKey at <console>:49 []
   *    | ShuffledRDD[l6151] at partitionBy at <console>:48 []
   *    | CachedPartitions: 8; MemorySize: 117541.8 MB; DiskSize: 0.0 B   
   *    
   * Operations that might cause a shuffle
   * - cogroup, groupWith, join, leftOuterJoin, rightOuterJoin
   *   groupByKey, reduceByKey, combineByKey, distinct, intersection
   *   repartition, coalesce    
   */
  
  /**
   * Avoiding a Network Shuffle By Partitioning
   * There are a few ways to use operations that might cause a shuffle and to still avoid much or all network shuffling.
   * Two examples:
   * 1. reduceByKey running on a pre-partitioned RDD will cause the values
   *    to be computed locally, requiring only the final reduced value has to
   *    be sent from the worker to the driver.
   * 2. join called on two RDDs that are pre-partitioned with the same
   *    partitioner and cached on the same machine will cause the join to be
   *    computed locally, with no shuffling across the network.
   */
  
  /**
   * Shuffles Happen: Key Takeaways
   * How your data is organized on the cluster, and what operations you're doing with it matters!
   * We've seen speedups of lOx on small examples just by trying to ensure
   * that data is not transmitted over the network to other machines.
   */
  
  
	def main(args: Array[String]) {
    sc.setLogLevel("ERROR")
    
		sc.stop()			
	}
}