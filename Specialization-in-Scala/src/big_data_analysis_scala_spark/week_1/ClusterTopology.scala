package big_data_analysis_scala_spark.week_1

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext

object ClusterTopology {
  
  val scf = new SparkConf
  val sc = new SparkContext(scf)
  
  case class Person(name: String, age: Int)
  
	def main(args: Array[String]) {
		val allPeople = sc.textFile("dir", 4).map(f => f.split(",")).map(f => (f(0),f(1)))
		val people = allPeople.map{case(n, a) => new Person(n, a.toInt)}
		people.foreach(println) //What happens?
		//On the driver: Nothing, Why?
		//foreach is an action, with return type Unit. Therefore, it is eagerly executed
		//on the executors, not the driver. Any call to println are happening on the stdout of 
		//worker nodes and are thus not visible in the stdout of the driver node.
		people.take(10).foreach(println) //Where will the Array[Person] representing first10 end up?
		//The driver program. In general, executing an action involves communication between worker
		//nodes and the node running the driver program.
		
		/**
		 * It's on you to know where your code is executing!
		 * Spark uses laziness to save time and memory!!!!
		 */
		sc.stop()
	}
  /**  
  *  Execution of a Spark program:
  *  1. The driver program runs the Spark application, which
  *     creates a SparkContext upon start-up.
  *  2. The SparkContext connects to a cluster manager (e.g.,
  *     Mesos/YARN) which allocates resources.
  *  3. Spark acquires executors on nodes in the cluster, which
  *     are processes that run computations and store data for
  *     your application.
  *  4. Next, driver program sends your application code to the
  *     executors.
  *  5. Finally, SparkContext sends tasks for the executors to
  *     run.  
  */
}