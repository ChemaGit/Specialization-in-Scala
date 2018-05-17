package big_data_analysis_scala_spark.week_2

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext

object TransformationsAndActionsOnPairRDDs {
  
  val sconf = new SparkConf
  val sc = new SparkContext(sconf)   
  
  /**
   * Recall groupBy from Scala collections.
   * 
   * Let's group the below list of ages into "child", "adult", and "senior" categories.
   */
  val ages = List(2, 52, 44, 23, 17, 14, 12, 82, 51, 64)
  val grouped = ages.groupBy {age =>
    if (age >= 18 && age < 65) "adult"
    else if (age < 18) "child"
    else "senior"
  }
  // grouped: scala.collection.immutable.Map[String,List[Int]] =
  // Map(senior -> List(82), adult-> List(52, 44, 23, 51, 64),
  //child-> List(2, 17, 14, 12))  
  
  /**
   * Important operations defined on Pair RDDs:
   * TRANSFORMATIONS
   * - groupByKey
	 * - reduceByKey
   * - mapValues
   * - keys
   * - Join
   * - left0uterJoin/right0uterJoin
   * 
   * ACTION
   * - countByKey
   */
  
  /**
   * groupByKey: can be thought of as a
   * groupBy on Pair RDDs that is specialized on grouping all values that have
   * the same key. As a result, it takes no argument.
   * def groupByKey(): RDD[(K, lterable[V])]
   */
  case class Event(organizer: String, name: String, budget: Int)
  val eventsRdd = sc.parallelize(List(new Event("pepe","bolos", 12000), new Event("Ramon","dardos",14000)))
                  .map(event => (event.organizer, event.budget))
  val groupedRdd = eventsRdd.groupByKey()  //Here the key is organizer. The call "does" nothing. It returns an unevaluated RDD
  groupedRdd.collect().foreach(println)
  // (Prime Sound, CompactBuffer(42000))
  // (Sportorg, CompactBuffer(23000, 12000, 1400))
  // .... 
  
  /**
   * reduceByKey: can be thought of as a combination of
   * groupByKey and reduce-ing on all the values per key. It's more efficient
   * though, than using each separately. (We'll see why later. )
   * def reduceByKey(func: (V, V) => V): RDD[(K, V)]
   * 
   * to calculate the total budget per organizer of all of their organized events.
   */
  val budgetsRdd = eventsRdd.reduceByKey(_+_)
  budgetsRdd.collect().foreach(println)
  // (Prime Sound, 42000)
  // (Sportorg, 36400)
  // (Innotech, 320000)
  // (Association Bal€lec, 50000)  
  
  /**
   * mapValues: mapValues (def mapValues [U](f: V => U): RDD[(K, U)]) can be thought of
   * as a short-hand for: rdd.map{case(x, y): (x, func(y))}
   * That is, it simply applies a function to only the values in a Pair RDD.
   */
  
  /**
   * countByKey: countByKey ( def countByKey(): Map[K, Long]) simply counts the number
   * of elements per key in a Pair RDD, returning a normal Scala Map
   * (remember, it's an action!) mapping from keys to counts.
   */
  //we can use each of these operations to compute the average budget per event organizer
  //Calculate a pair (as a key's value) containing (budget, #events)
  val intermediate = eventsRdd.mapValues ({case(b)=> (b, 1) }).
                               reduceByKey({case(v1, v2) => (v1._1 + v2._1, v1._2 + v2._2)})//intermediate: RDD[(String, (Int, Int))]
  val avgBudgets = intermediate.mapValues({case(budget, numberOfEvents) => budget / numberOfEvents})  
  avgBudgets.collect().foreach(println)
  // (Prime Sound,42000)
  // (Sportorg,12133)
  // (Innotech,106666)
  // (Association Balelec,50000)  
  
  /**
   * keys ( def keys: RDD[K]) Return an RDD with the keys of each tuple.
   */
  //count the number of unique visitors to a website using the keys transformation.
  case class Visitor(ip: String, timestamp: String, duration: String)
  val visits = sc.textFile("dir").map(line => line.split(",")).map(f => (f(0), 1))
  //val numUniqueVisits = visits.reduceByKey({case(v, v2) => (v + v2)}) 
  val numUniqueVisits = visits.keys.distinct().count()
  
  /**
   * PairRDDFunctions
   * For a list of all available specialized Pair RDD operations, see the Spark
   * API page for PairRDDFunctions (ScalaDoc):
   * http://spark.apache.org/docs/latest/api/scala/index.html#org.apache.spark.rdd.PairRDDFunctions
   */
  
  
	def main(args: Array[String]) {
    sc.setLogLevel("ERROR")
    
		sc.stop()				
	}
}