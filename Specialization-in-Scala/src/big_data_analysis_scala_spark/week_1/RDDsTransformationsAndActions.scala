package big_data_analysis_scala_spark.week_1

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext

object RDDsTransformationsAndActions {
  /**
   * Laziness/eagerness is how we can limit network
   * communication using the programming model.
   */
  
  /**
   * Transformers: return a new collections as results => map, filter, flatMap, groupBy...
   * Accessors: return single values as results => reduce, fold, aggregate.
   * 
   * Spark defines transformations and actions on RDDs.
   * Transformations: return new RDDs as results. They are "lazy", their result RDD is not immediately computed.
   * 
   * Actions: compute a result based on an RDD, and either returned or 
   * saved to an external storage system.(e.g., HDFS, ...)
   * They are "eager", their result is immediately computed.
   */
  
  /**
   * COMMON TRANSFORMATIONS [LAZY]
   * map => map[B](f: A=> B): RDD[B] Apply function to each element in the RDD and return an ROD of the result.
	 * flatMap => flatMap[B](f: A=> TraversableOnce[B]): RDD[B] Apply a function to each element in the RDD and return
	 * an RDD of the contents of the iterators returned.
	 * filter => filter(pred: A=> Boolean): RDD[A] Apply predicate function to each element in the ROD and
	 * return an RDD of elements that have passed the predicate condition, pred.
	 * distinct => distinct(): RDD[B] Return RDD with duplicates removed.
   */
  
  /**
   * TRANSFORMATIONS ON TWO RDDs [LAZY]
   * Two-RDD transformations combine two RDDs are combined into one.
   * union => union(other: RDD[T]): RDD[T] Return an RDD containing elements from both RDDs.
   * intersection => intersection(other: RDD[T]): RDD[T] Return an RDD containing elements only found in both RDDs.
   * subtract => subtract(other: RDD[T]): RDD[T] Return an RDD with the contents of the other RDD removed.
   * cartesian => cartesian[U](other: RDD[U]): RDD[(T, U)] Cartesian product with the other RDD.
   */
  
  /**
   * COMMON ACTIONS [EAGER]
   * collect => collect(): Array[T] Return all elements from RDD.
   * count => count(): Long Return the number of elements in the RDD.
   * take => take(num: Int): Array[T] Return the first num elements of the RDD. 
   * reduce => reduce(op: (A, A) => A): A Combine the elements in the RDD together using op function and return result.
   * foreach => foreach(f: T => Unit): Unit Apply function to each element in the RDD.
   */
  
  /**
   * OTHER USEFUL RDD ACTIONS [EAGER]
   * takeSample => takeSample(withRepl: Boolean, num: Int): Array[T] Return an array with a random sample of num elements of
	 * the dataset, with or without replacement.
   * takeOrdered => takeOrdered(num: Int)(implicit ord: Ordering[T]): Array[T] Return the first n elements of the ROD using either
   * their natural order or a custom comparator.
   * saveAsTextFile => saveAsTextFile(path: String): Unit  Write the elements of the dataset as a text file in
   * the local filesystem or HDFS.
   * saveAsSequenceFile => saveAsSequenceFile(path: String): Unit
   * Write the elements of the dataset as a Hadoop SequenceFile
   * in the local filesystem or HDFS.
   */
  
	def main(args: Array[String]) {
	  val sconf = new SparkConf
	  val sc = new SparkContext(sconf)
	  
	  sc.setLogLevel("ERROR")
	  
	  val largeList: List[String] = List("pepe", "maria", "chema", "madrid", "sevilla", "valencia")
	  val wordsRdd = sc.parallelize(largeList) //RDD[String]
	  val lengthsRdd = wordsRdd.map(f => f.length) //RDD[Int]
	  //it didn't happen anything on the cluster at this point
	  
	  //if we add an action....
	  val totalChars = lengthsRdd.reduce( (a,b) => a + b)
	  
	  /**
	   * Determine the number or errors that were logged in December 2016
	   * dates come in the form YYYY-MM-DD:HH:MM:SS and errors are logged with the prefix
	   * that includes the word "error"
	   */
	  val lastYearsLogs = sc.textFile("dir", 8) //RDD[String]
	  val numDecErrorLogs = lastYearsLogs.filter(lg => lg.contains("2016-12") && lg.contains("error")).count()
	  
	  /**
	   * Example: The execution of filter is deferred until the take action is applied
	   * Spark will not compute intermediate RDDs. Instead, as soon as 10 elements of the
     * filtered RDD have been computed, firstLogsWi thErrors is done. At this point Spark
     * stops working, saving time and space computing elements of the unused result of filter.
	   */
	  val firstlogsWithErrors = lastYearsLogs.filter(_.contains("error")).take(10)
		
	  sc.stop()
	}
}