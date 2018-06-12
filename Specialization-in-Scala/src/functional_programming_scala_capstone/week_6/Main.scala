package observatory

import org.apache.log4j.{Level, Logger}
import Extraction._
import Interaction._
import Visualization._

object Main extends App {
  Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
  val year: Year = 2015
  val temperatures = locateTemperatures(year, "/stations.csv", "/2015.csv")  
}
