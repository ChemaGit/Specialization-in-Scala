package big_data_analysis_scala_spark.week_4

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql._
import org.apache.spark.sql.types._
import org.apache.spark.sql.functions._

object Datasets {
  
  val sconf = new SparkConf
  val sc = new SparkContext(sconf) 
  
  val spark: SparkSession =
    SparkSession
      .builder()
      .appName("My App")
      .config("spark.master", "local")
      .getOrCreate()  
  
  // For implicit conversions like converting RDDs to DataFrames    
  import spark.implicits._  
  
  /**
   * Let's say we've just done the following computation on a DataFrame
   * representing a data set of Listings of homes for sale; we've computed the
   * average price of for sale per zipcode.
   */
  case class Listing(street: String, zip: Int, price: Int) 
  val listingsDF = sc.textFile("dir").map(line => line.split(",")).map(f => new Listing(f(0), f(1).toInt, f(2).toInt))
                   .toDF
  val mostExpensive = listingsDF.groupBy($"zip").max("price") 
  val lessExpensive = listingsDF.groupBy($"zip").min("price")  
  
  val averagePricesDF = listingsDF.groupBy($"zip").avg("price")  
  val averagePrices = averagePricesDF.collect() // averagePrices: Array[org.apache.spark.sql.Row]  
  //Oh right, I have to cast things because Rows don't have type information
  //associated with them. How many columns were my result again? And what were their types?
  
  val averagePricesAgain = averagePrices.map {row => (row(0).asInstanceOf[String], row(1).asInstanceOf[Int])} // java.lang.ClassCastException
  
  averagePrices.head.schema.printTreeString()
  // root
  // |-- zip: integer (nullable = true)
  // |-- avg(price): double (nullable = true)
  
  val averagePricesAgainB = averagePrices.map{row => (row(0).asInstanceOf[Int], row(1).asInstanceOf[Double])}
  // mostExpensiveAgain: Array[(Int, Double)]  
  
  /**
   * Wouldn't it be nice if we could have both Spark SQL optimizations and type safety?
   * DATASET
   * What is a Dataset
   * - Datasets can be thought of as typed distributed collections of data.
   * - Dataset API unifies the DataFrame and RDD APls. Mix and match!
   * - Datasets require strucutred/semi-structured data. Schemas and
   *   Encoders core part of Datasets.
   *   
   * Think of Datasets as a compromise between RDDs & DataFrames.
   * You get more type information on Datasets than on DataFrames, and you
   * get more optimizations on Datasets than you get on RDDs.
   */
  //the average home price per zipcode with Datasets.
  val listingsDS = sc.textFile("dir").map(line => line.split(",")).map(f => new Listing(f(0), f(1).toInt, f(2).toInt))
                   .toDS()
  //We can freely mix APIs
  val avgDS = listingsDS.groupByKey(l => l.zip) // looks like groupByKey on RDDs
                        .agg(avg($"price").as[Double]) // looks like our DataFrame operators!
                        
  /**                        
   * - Datasets are a something in the middle between DataFrames and RDDs
   * - You can still use relational DataFrame operations as we learned in previous sessions on Datasets. 
   * - Datasets add more typed operations that can be used as well.
   * - Datasets let you use higher-order functions like map, flatMap, filter again!    
   * 
   * Datasets can be used when you want a mix of functional and relational
   * transformations while benefiting from some of the optimizations on DataFrames.                    
   */
                        
  /**
   * Creating Datasets
   * - From a DataFrame: just use the toDS convenience method.
   *   myDF.toDS // requires import spark.implicits._      
   * - to read in data from JSON from a file, which can be done with 
   *   the read method on the SparkSession object and then converted to a Dataset:  
   * - From an RDD: Just use the toDS convenience method
   *   myRDD.toDS  // requires import spark.implicits._
   * - From common Scala types: Just use the toDS convenience method.                      
   */
  case class Person(id: Int, age: Int, country: String)                        
  val myDS = spark.read.json("peoplel.json").as[Person]   
  
  val myDSFromScalaType = List("yay","ohnoes","hooray!").toDS()  // requires import spark.implicits._
  
  /**
   * Typed Columns
   */
                        
	def main(args: Array[String]) {
		
	}
}