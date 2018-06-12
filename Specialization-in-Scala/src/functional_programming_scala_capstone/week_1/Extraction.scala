package observatory

import java.time.LocalDate
import java.text.DecimalFormat
import java.nio.file.Paths
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql._
import org.apache.spark.rdd._
import org.apache.spark.sql.functions._

import org.apache.log4j.{Level, Logger}

/**
  * 1st milestone: data extraction
  */
object Extraction {
  Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
  
  val df = new DecimalFormat("0.00000000");
  
  
  //@transient lazy val conf: SparkConf = new SparkConf().setMaster("local").setAppName("observatory")
  //@transient lazy val sc: SparkContext = new SparkContext(conf)
  
  val spark: SparkSession = SparkSession.builder()
                                        .appName("Observatory")
                                        .config("spark.master", "local[16]")
                                        .config("spark.executor.memory", "1600m")
                                        .getOrCreate()                                        
                                        
  // For implicit conversions like converting RDDs to DataFrames                                        
  import spark.implicits._  
  
  def formatDouble(value: Double): Temperature = {
    val temp: Temperature = java.lang.Double.valueOf (df.format(value).replace(",", "."))
    temp
  }   
                                        
  /** @return The filesystem path of the given resource */
  def fsPath(resource: String): String =
    Paths.get(getClass.getResource(resource).toURI).toString 
    
  /** @return The read DataFrame along with its column names. */
  def readStations(resource: String): Dataset[Stations] = {
    spark.sparkContext.textFile(fsPath(resource))
                                .map(line => line.split(","))
                                .filter(f => f.length == 4)
                                .sortBy(f => (f(0), f(1)), true, 8)
                                .map(f => Stations( (f(0),f(1)), Location(f(2).toDouble,f(3).toDouble) ))                                
                                .toDS()  
  }        
  
  def readTemperatures(year: Year,resource: String): Dataset[Temperatures] = {    
    spark.sparkContext.textFile(fsPath(resource))
                                .map(line => line.split(","))
                                .filter(f => f.length == 5)
                                .filter(f => f(4).toDouble < 9999)
                                .sortBy(f => (f(0), f(1)), true, 8)
                                .map(f => Temperatures((f(0),f(1)),year, f(2).toInt, f(3).toInt,(f(4).toDouble - 32) / 1.8))
                                .toDS()
  }  
                                      
  /**
    * @param year             Year number
    * @param stationsFile     Path of the stations resource file to use (e.g. "/stations.csv")
    * @param temperaturesFile Path of the temperatures resource file to use (e.g. "/1975.csv")
    * @return A Spark RDD containing triplets (date, location, temperature)
    */  
  def locateTemperaturesSpark(year: Year, stationsFile: String, temperatursFile: String):Dataset[SelectClass] = {
    //read from file to DataFrame
    val stationsDS = readStations(stationsFile)
    val temperaturesDS = readTemperatures(year, temperatursFile)
    //we have to do a join 
    stationsDS.join(temperaturesDS,usingColumn ="id").as[JoinClass]
              .select("year","month", "day","loc","temperature").as[SelectClass]
  }
  
  /*def locateTemperaturesSpark(year: Year, stationsFile: String,
                              temperaturesFile: String): RDD[(LocalDate, Location, Temperature)] = {
    val stationsRDD = spark.sparkContext.textFile(fsPath(stationsFile))
    val temperaturesRDD = spark.sparkContext.textFile(fsPath(temperaturesFile))    

    val stations = stationsRDD
      .map(_.split(','))
      .filter(_.length == 4)
      .map(a => ((a(0), a(1)), Location(a(2).toDouble, a(3).toDouble)))
      .sortBy(f => f._1, true, 8)

    val temperatures = temperaturesRDD
      .map(_.split(','))
      .filter(_.length == 5)
      .filter(f => f(4).toDouble < 9999.8)
      .map(a => ((a(0), a(1)), (LocalDate.of(year, a(2).toInt, a(3).toInt), (a(4).toDouble - 32) / 1.8)))
      .sortBy(f => f._1, true, 8)

    stations.join(temperatures).mapValues(v => (v._2._1, v._1, v._2._2)).values
  }*/  
  
  /**
    * @param year             Year number
    * @param stationsFile     Path of the stations resource file to use (e.g. "/stations.csv")
    * @param temperaturesFile Path of the temperatures resource file to use (e.g. "/1975.csv")
    * @return A sequence containing triplets (date, location, temperature)
    */
  def locateTemperatures(year: Year, stationsFile: String, temperaturesFile: String): Iterable[(LocalDate, Location, Temperature)] = { 
    //locateTemperaturesSpark(year, stationsFile, temperaturesFile).collect.par
    //.map(j => (LocalDate.of(j.year,j.month,j.day),j.loc,j.temperature)).toVector 
    
    locateTemperaturesSpark(year, stationsFile, temperaturesFile).take(10000).par
    .map(j => (LocalDate.of(j.year,j.month,j.day),j.loc,j.temperature)).toVector     
  }
  
  /*def locateTemperatures(year: Year, stationsFile: String, temperaturesFile: String): Iterable[(LocalDate, Location, Temperature)] = {
    //locateTemperaturesSpark(year, stationsFile, temperaturesFile).take(10000).toSeq //collect().toSeq
    locateTemperaturesSpark(year, stationsFile, temperaturesFile).collect().toSeq
  }*/  
  
  /**
    * @param records A sequence containing triplets (date, location, temperature)
    * @return A sequence containing, for each location, the average temperature over the year.
    */  
  /*def locationYearlyAverageRecordsSpark(records: RDD[(LocalDate, Location, Temperature)]):RDD[(Location, Temperature)] = {              
    val rddMap = records.map({case(date, loc, temp) => (loc, (1,temp))}).sortBy(f => (f._1.lat,f._1.lon), true, 8)
    rddMap.reduceByKey({case(v, v1) => (v._1 + v1._1, v._2 + v1._2)}).map({case(loc,(cont, temp)) => (loc, formatDouble(temp/cont))})
  }*/
  
  /*def locationYearlyAverageRecordsSpark(records: RDD[(LocalDate, Location, Temperature)]): RDD[(Location, Temperature)] = {
    records
      .groupBy(_._2)
      .mapValues(values => values.map(value => (value._3, 1)))
      .mapValues(_.reduce((v1, v2) => (v1._1 + v2._1, v1._2 + v2._2)))
      .mapValues({case (temperature, count) => temperature / count})
  }*/  
  
  /*def locationYearlyAverageRecordsSpark(records: RDD[(LocalDate, Location, Temperature)]): Dataset[(Location, Temperature)] = {
    records.map(f => (f._2,f._3))
                     .toDS.as[(Location, Temperature)]
                     .orderBy("_1")                     
                     .groupBy("_1")                     
                     .agg(mean("_2").as[Double]).as[(Location, Temperature)]                           
  }*/
  
  def locationYearlyAverageRecordsSpark(records: RDD[(LocalDate, Location, Temperature)]): RDD[(Location, Temperature)] = {
    records.map({case(date, loc, temp) => (loc,(temp, 1))})
           .sortBy({case(loc, (temp, cont)) => ( (loc.lat, loc.lon), true, 8)})
           .reduceByKey({case(v, v1) => (v._1 + v1._1, v._2 + v1._2)})
           .mapValues({case(temp, cont) => formatDouble(temp/cont)})                       
  }  
  
  /**
    * @param records A sequence containing triplets (date, location, temperature)
    * @return A sequence containing, for each location, the average temperature over the year.
    */
  def locationYearlyAverageRecords(records: Iterable[(LocalDate, Location, Temperature)]): Iterable[(Location, Temperature)] = { 
    val rdd = spark.sparkContext.parallelize(records.toSeq)
    locationYearlyAverageRecordsSpark(rdd).collect().par.toVector    
  }
  
  /*def locationYearlyAverageRecords(records: Iterable[(LocalDate, Location, Double)]): Iterable[(Location, Double)] = {   
    records.map(f => (f._2,f._3))
           .toSeq
           .sortBy(f => (f._1.lat, f._1.lon))           
           .par          
           .groupBy(f => f._1)
           .map{case (loc, temps) => (loc, formatDouble(temps.foldLeft(0.0)({case(v, (l, t)) => v + t / temps.size})))}
           .toVector
  }*/
  /*def locationYearlyAverageRecords(records: Iterable[(LocalDate, Location, Temperature)]): Iterable[(Location, Temperature)] = {
    records
      .par
      .groupBy(f => f._2)
      .mapValues({v => v.map(_._3).sum / v.size})
      .seq
  }*/  
  
  /*def locationYearlyAverageRecords(records: Iterable[(LocalDate, Location, Double)]): Iterable[(Location, Double)] = {
    records
      .par
      .groupBy(_._2)
      .mapValues(values => values.foldLeft(0.0)((t,r) => t + r._3) / values.size)
      .seq
  }*/  
  
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    spark.sparkContext.setLogLevel("ERROR")    
    //val resource = fsPath("/stations.csv")
    //readStations(resource)    
    //val resource = fsPath("/2015.csv")
    //readTemperatures(resource)
    //println("Year: " + getYear("/2015.csv"))
    //val date = LocalDate.of(2015, 12, 25)
    //readTemperatures("/2015.csv").show()
    //readStations("/stations.csv").show()
    val year: Year = 2015
    val res = locateTemperatures(year, "/stations.csv", "/2015.csv")
    //res.take(20).foreach(println)
    //res.take(20).foreach(println)
    locationYearlyAverageRecords(res).take(50).foreach(println)    
    spark.sparkContext.stop()
  }
}
