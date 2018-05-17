package big_data_analysis_scala_spark.week_3

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext

object Shuffling {
  
  val sconf = new SparkConf
  val sc = new SparkContext(sconf)   
  
  /**
   * what happens when you have to do a groupBy or a groupByKey. Remember our data is distributed!
   * We typically have to move data from one node to another to be "grouped with" its key. Doing this is called "shuffling".
   */
  val pairs = sc.parallelize(List((1, "one"), (2, "two"), (3, "three")))
  pairs.groupByKey()
  // res2: org.apache.spark.rdd.RDD[(Int, Iterable[String])]
  // = ShuffledRDD[16] at groupByKey at <console>:37  
  
  /**
   * Shuffles Happen
   * Shuffles can be an enormous hit to because it means that Spark must send
   * data from one node to another. Why? Latency!
   */
  case class CFFPurchase(customerid: Int, destination: String, price: Double)
  val purchases = List(CFFPurchase(100, "Geneva", 22.25),
                        CFFPurchase (300, "Zurich", 42.10),
                        CFFPurchase(100, "Fribourg", 12.40),
                        CFFPurchase (200, "St. Gallen", 8.20),
                        CFFPurchase(100, "Lucerne", 31.60),
                        CFFPurchase (300, "Basel", 16.20))  
  val purchasesRdd = sc.parallelize(purchases)                        
  //val purchasesRdd = sc.textFile("dir").map(line => line.split(",")).map(r => new CFFPurchase(r(0).toInt,r(1),r(2).toDouble))
  //Goal: calculate how many trips, and how much money was spent by
  //each individual customer over the course of the month.
  val purchasesPerMonth = purchasesRdd.map(p => (p.customerid, p.price)) // Pair RDD
                                      .groupByKey() // groupByKey returns RDD[K, Iterable[V] ]
                                      .map(p => (p._1, (p._2.size, p._2.sum)))
                                      .collect()
  //Note: groupByKey results in one key-value pair per key. And this
  //single key-value pair cannot span across multiple worker nodes.   
  //groupByKey ==> Shuffles data across network
  //Too much network communication kills performance. 
                                      
  /**
   * we can reduce before we shuffle. This could greatly reduce the
   * amount of data we have to send over the network. 
   * def reduceByKey(func: (V, V) => V): RDD[(K, V)]
   * can be thought of as a combination of first
   * doing groupByKey and then reduce-ing on all the values grouped per key.
   * It's more efficient     
   * 
   * By reducing the dataset first, the amount of data sent over the network
   * during the shuffle is greatly reduced.
   * This can result in non-trival gains in performance!                                
   */
  val purchasesPerMonthReduce =
  purchasesRdd.map(p => (p.customerid, (1, p.price))) // Pair ROD
              .reduceByKey({case(v1, v2) => (v1._1 + v2._1, v1._2 + v2._2)}) 
              .collect()
                                      
	def main(args: Array[String]) {
    sc.setLogLevel("ERROR")
    
		sc.stop()		
	}
}