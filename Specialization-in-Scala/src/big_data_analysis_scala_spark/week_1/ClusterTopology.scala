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
		people.take(10).foreach(println) //Where will the Array[Person] representing first10 end up?
	}
}