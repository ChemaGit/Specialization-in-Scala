package big_data_analysis_scala_spark.week_4

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql._
import org.apache.spark.sql.types._

object DataFrames {
  
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
   * DataFrames have their own APls
   * DataFrames are: A relational API over Spark's RDDs
   * Able to be automatically aggressively optimized
   * DataFrames are Untyped!
   */
  
  /**
   * DataFrames data types
   * To enable optimization opportunities, Spark SQL's DataFrames operate on
   * a restricted set of data types.
   * 
   * Basic Spark SQL Data Types: 
   * ByteType,ShortType,IntegerType,LongType,DecimalType,FloatType
   * DoubleType,BinaryType,BooleanType,BooleanType,TimestampType,DateType,StringType
   * 
   * Complex Spark SQL Data Types:
   * ArrayType(elementType, containsNull)
   * MapType(keyType, valueType, valueContainsNull)
   * case class StructType(List[StructFields])
   */
  
  /**
   * ArrayType
   * Array of only one type of element (elementType}. containsNull is set to true if the
   * elements in ArrayType value can have null values.
   * ArrayType(StringType, true)
   */
  
  /**
   * MapType
   * Map of key /value pairs with two types of elements. value containsNull is set to true if
   * the elements in MapType value can have null values.
   * MapType(IntegerType, StringType, true)
   */
  
  /**
   * case class StructType(List[StructFields])
   * Struct type with list of possible fields of different types. containsNull is set to true if the
   * elements in StructFields can have null values.
   * StructType(List(StructField("name", StringType, true),StructField("age", IntegerType, true)))
   * 
   * It's possible to arbitrarily nest complex data types! For example:
   * 
   * to access any of these data types, we must first import Spark SQL types!
   * import org.apache.spark.sql.types._
   */
  case class Account(balance: Double,employees:Array[Employee])
  case class Employe(id: Int,name: String,jobTitle: String)
  case class Project(title: String,team: Array[Employee],acct: Account)
  /*
  StructType(
		StructField(title,StringType,true),
		StructField(
			team,
			ArrayType(
				StructType(StructField(id,IntegerType,true),
				StructField(name,StringType,true),
				StructField(jobTitle,StringType,true)),
				true),
			true),
			StructField(
				acct,
				StructType(
					StructField(balance,DoubleType,true),
					StructField(
						employees,
						ArrayType(
							StructType(StructField(id,IntegerType,true),
							StructField(name,StringType,true),
							StructField(jobTitle,StringType,true)),
							true),
						true)
					),
				true)
			)
  */
  
  /**
   * DataFrames API: Similar-looking to SQL. Example methods include:
   * select, where, limit, orderBy, groupBy, join
   * 
   * show() pretty-prints DataFrame in tabular form. Shows first 20 elements.
   * printSchema() prints the schema of your DataFrame in a tree format.
   */
  case class Person(id: Int, name: String, country: String, city: String)
  val tuplePerson = sc.textFile("dir").map(line => line.split(",")).map(f => new Person(f(0).toInt, f(1), f(2), f(3)))
  val personDF = tuplePerson.toDF("id","name","country","city") //infer the attributes from the case class's fields.  
  personDF.printSchema()
  // root
  // 1-- id: integer (nullable = true)
  // 1-- name: string (nullable = true)
  // 1-- country: string (nullable = true)
  // 1-- city: string (nullable = true)  
  
  /**
   * Common DataFrame Transformations
   * Like on RDDs, transformations on DataFrames are (1) operations which return a
   * DataFrame as a result, and (2) are lazily evaluated.
   * 
   * selects a set of named columns and returns a new DataFrame with these columns as a result.
   * def select(col: String, cols: String*): DataFrame
   * 
   * performs aggregations on a series of columns and returns a new DataFrame with the calculated output.
   * def agg(expr: Column, exprs: Column*): DataFrame
   * 
   * groups the DataFrame using the specified columns. Intended to be used before an aggregation.
   * def groupBy(col1: String, cols: String*): DataFrame
   * 
   * inner join with another DataFrame
   * def join(right: DataFrame): DataFrame
   * 
   * Other transformations include: filter, limit, orderBy, where, as, sort, union, drop, amongst others.
   */
  
  /**
   * Most methods on DataFrames tend to some well-understood, pre-defined operation on a column of the data set
   * You can select and work with columns in three ways:
   * 1. Using $-notation: $-notation requires: import spark.implicits._
   * df.filter($"age" > 18)
   * 2. Referring to the Dataframe
   * df.filter(df("age") > 18))
   * 3. Using SQL query string
   * df.filter("age > 18")
   */
  
  /**
   * EXAMPLE
   * We'd like to obtain just the IDs and last names of employees working in a
   * specific city, say, Sydney, Australia. Let's sort our result in order of in creasing employee ID.
   * Rather than using SQL syntax, let's convert our example to use the DataFrame API.
   */
  
  //Let's assume we have a DataFrame representing a data set of employees:
  case class Employee(id: Int, fname: String, lname: String, age: Int, city: String)
  //DataFrame with schema defined in Emplyee case class
  val employeeDF = sc.textFile("dir").map(line => line.split(",")).map(f => new Employee(f(0).toInt, f(1), f(2), f(3).toInt,f(4)))
                   .toDF("Id", "fname", "lname", "age", "city")  
  val sydneyEmployeesDF = employeeDF.select("id", "lname")
                                    .where("city == 'Sydney'")
                                    .orderBy("id")
  // employeeDF:
  //+---+-----+-------+---+--------+
  // id |lfnamel lname|age | city  |
  //+---+-----+-------+---+--------+
  //| 121 Joel Smithl 381New York |
  //| 563ISallyl Owensl 481New York|
  // l645ISlatelMarkhaml 281 Sydney|
  // 1221 | David| Walker|21 Sydney|
  //+---+-----+-------+---+--------+   
                                    
  //sydneyEmployeesDF:
  //+---+-------+
  //|id | lname|
  //+---+-------+
  //1221 | Walker|
  //l6451 |Markham|
  //+---+-------+       
 
  /**
   * The DataFrame API makes two methods available for filtering:
   * filter and where (from SQL). They are equivalent!                                                    
   */
  val over30 = employeeDF.filter("age > 30").show()
  val over30W = employeeDF.where("age > 30").show()
  
  val filterComplex = employeeDF.filter(($"age" > 25) && ($"city" === "Sydney")).show()
  employeeDF.filter("age > 25 && city == 'Sydney'").show()
  
  /**
   * Grouping and Aggregating on Data Frames
   */
                    
  
  
	def main(args: Array[String]) {
		
	}
}