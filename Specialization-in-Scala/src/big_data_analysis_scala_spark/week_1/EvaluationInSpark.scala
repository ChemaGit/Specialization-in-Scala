package big_data_analysis_scala_spark.week_1

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext

object EvaluationInSpark {
  val sconf = new SparkConf()
  val sc = new SparkContext(sconf)
  
  /**
   * ITERATION, EXAMPLE: LOGISTIC REGRESSION
   * Logistic regression is an iterative algorithm typically used for classification.
   * Like other classification algorithms, the classifier's weights are iteratively updated
   * based on a training dataset.
   * 
   * Points is being re-evaluated upon every iteration!
   * That's unnecessary! What can we do about this?
   */
  case class Point(x: Double, y: Double)
  
  def parsePoint(p: String): Point = ???
  
  val points = sc.textFile("dir", 4).map(parsePoint).persist() //Now, points is evaluated once and is cached in memory.
  val numIterations: Int = ???                                 //It is then re-used on each iteration. 
  /*var w = Vector.zeros(d)
  for(i <- 1 to numIterations) {
    val gradient = points.map{p => (1 / (1 + exp(-p.y * w.dot(p.x))) - 1) * p.y * p.y}.reduce(_ + _)
    w -= alpha * gradient
  }*/
  
  /*
   * By default, RDDs are recomputed each time you run an action on them.
   * This can be expensive (in time) if you need to use a dataset more than once.
   * Spark allows you to control what is cached in memory.
   * To tell Spark to cache an RDD in memory, simply call 
   * persits() or cache() on it.
   * 
   * Here, we cache logsWithErrors in memory.
   * After firstLogsWithErrors is computed, Spark will store the contents of
   * logsWithErrors for faster access in future operations if we would like to reuse it.
   */
  val lastYearsLogs = sc.textFile("dir", 8) //RDD[String]
  val logsWithErrors = lastYearsLogs.filter(_.contains("error")).persist()
  val firstLogsWithErrors = logsWithErrors.take(10)
  val numErrors = logsWithErrors.count() //Now, computing the count on logsWithErrors is much faster.
  
	def main(args: Array[String]) {
    /**
     * There are many ways to configure how your data is persisted.
     * cache() => shorthand for using the default storage level, which is in memory only as regular Java objects.
     * persits => persistence can be customized with this method. Pass the storage level you'd like as a parameter to persist.
     * LEVEL								SPACE USED		CPU TIME		IN MEMORY		ON DISK
     * MEMORY_ONLY					High					Low					Y						N			=> Default
     * MEMORY_ONLY_SER			Low						High				Y						N
     * MEMORY_AND_DISK			High					Medium			Some				Some
     * MEMORY_AND_DISK_SER	Low						High				Some				Some
     * DISK_ONLY						Low						High				N						Y
     * 
     * One of the most common performance bottlenecks of newcomers
     * to Spark arises from unknowingly re-evaluating several transformations
     * when caching could be used.
     */
		
	}
}