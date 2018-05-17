package big_data_analysis_scala_spark.week_2

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext

object Joins {
  
  val sconf = new SparkConf
  val sc = new SparkContext(sconf)  
  
  /**
   * Joins
   * They're used to combine multiple datasets
   * There are two kinds of joins:
   * 1. Inner joins (join)
   * 2. Outer joins (leftOuterJoin / rightOuterJoin)
   * 
   * The key difference between the two is what happens to the keys when
   * both RDDs don't contain the same key.
   */
  
  /**
   * Example: Let's pretend the CFF has two datasets. One RDD representing
   * customers and their subscriptions (abos), and another representing
   * customers and cities they frequently travel to (locations).
   */
  val as = List((101, ("Ruetli", "AG")), (102, ("Brelaz", "DemiTarif")),( 103, ("Gress", "DemiTarifVisa")), ( 104, ( "Schatten", "DemiTarif")))
  val abos = sc.parallelize(as)
  val ls = List((101, "Bern"), (101, "Thun"), (102, "Lausanne"), (102, "Geneve"),(102, "Nyon"), (103, "Zurich"), (103, "St-Gallen"), (103, "Chur"))
  val locations = sc.parallelize(ls)    
  
  /**
   * Inner joins
   * Inner joins return a new RDD containing combined pairs whose keys are present in both input RDDs.
   * def join[W](other: RDD[(K, W)]): RDD[(K, (V, W))]
   */
  val trackedCustomers = abos.join(locations) // trackedCustomers: RDD[(Int, ((String, Abonnement) , String) ) ]
  trackedCustomers.collect() .foreach(println)
  // (101, ((Ruetli, AG) , Bern) )
  // (101, ((Ruetli, AG) , Thun) )
  // (102, ((Brelaz, DemiTarif) , Nyon) )
  // (102, ((Brelaz, DemiTarif) , Lausanne) )
  // (102, ((Brelaz, DemiTarif) , Geneve) )
  // (103, ((Gress, DemiTarifVisa) , St-Gallen) )
  // (103, ((Gress, DemiTarifVisa) , Chur) )
  // (103, ((Gress, DemiTarifVisa) , Zurich) ) 
  // Customer 104 does not occur in the result, because there is no location data for this customer
  
  /**
   * Outer Joins (leftOuterJoin, rightOuterJoin)
   * Outer joins return a new RDD containing combined pairs whose keys don't have
   * to be present in both input RDDs.
   * def leftOuterJoin[W](other: RDD[(K, W)]): RDD[(K, (V, Option[W]))]
   * def rightOuterJoin[W](other: RDD[(K, W)]): RDD[(K, (Option[V], W))]
   */
  //Example: Let's assume the CFF wants to know for which subscribers the CFF
  //has managed to collect location information. E.g., it's possible that someone has
  //a demi-tarif, but doesn't use the CFF app and only pays cash for tickets.  
  val abosWithOptionallocations = abos.leftOuterJoin(locations)
  // abosWithOptionallocations: RDD[(Int, ((String, Abonnement) , Option[String] ) ) ]
  //Since we use a leftOuterJoin, keys are guaranteed to occur in the left source RDD.
  abosWithOptionallocations.collect().foreach(println)
  // (101,((Ruetli,AG),Some(Thun)))
  // (101,((Ruetli,AG),Some(Bern)))
  // (102,((Brelaz,DemiTarif),Some(Geneve)))
  // (102,((Brelaz,DemiTarif),Some(Nyon)))
  // (102,((Brelaz,DemiTarif),Some(Lausanne)))
  // (103,((Gress,DemiTarifVisa),Some(Zurich)))
  // (103,((Gress,DemiTarifVisa),Some(St-Ga llen)))
  // (103,((Gress,DemiTarifVisa),Some(Chur)))
  // (104,((Schatten,DemiTarif),None)) 
  
  //We want to combine both RDDs into one:
  //The CFF wants to know for which customers
  //(smartphone app users) it has subscriptions for.
  //E.g., it's possible that someone uses the mobile
  //app, but has no demi-tarif.
  val customersWithlocationDataAndOptionalAbos = abos.rightOuterJoin(locations)
  // RDD[(Int, (Option[(String, Abonnement)], String))]
  customersWithlocationDataAndOptionalAbos.collect().foreach(println)
  // (101 ,(Some((Ruetli,AG)),Bern))
  // (101 ,(Some((Ruetli,AG)),Thun))
  // (102,(Some((Brelaz,DemiTarif)),Lausanne))
  // (102,(Some((Brelaz,DemiTarif)),Geneve))
  // (102,(Some((Brelaz,DemiTarif)),Nyon))
  // (103,(Some((Gress,DemiTarifVisa)),Zurich))
  // (103,(Some((Gress,DemiTarifVisa)),St-Gallen))
  // (103,(Some((Gress,DemiTarifVisa)),Chur))
  //Note that, here, customer 104 disappears again because that customer doesn't have
  //location info stored with the CFF {the right RDD in the join).  
  
  /**  
  * Shuffles Happen
  * Shuffles can be an enormous hit to because it means that Spark must send
  * data from one node to another. Why? Latency!  
  */
  
  
	def main(args: Array[String]) {
    sc.setLogLevel("ERROR")
    
		sc.stop()				
	}
}