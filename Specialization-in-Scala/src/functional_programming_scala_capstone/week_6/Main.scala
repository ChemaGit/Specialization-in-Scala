package observatory

import org.apache.log4j.{Level, Logger}

object Main extends App {
  
  Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
  
  val tempToColor = List[(Temperature, Color)]((60, Color(255, 255, 255)), (32, Color(255, 0, 0)),
    (12, Color(255, 255, 0)), (0, Color(0, 255, 255)), (-15, Color(0, 0, 255)), (-27, Color(255, 0, 255)),
    (-50, Color(33, 0, 107)), (-60, Color(0, 0, 0)))

  val devToColor = List[(Temperature, Color)]((7, Color(0, 0, 0)), (4, Color(255, 0, 0)),
    (2, Color(255, 255, 0)), (0, Color(255, 255, 255)), (-2, Color(0, 255, 255)), (-7, Color(0, 0, 255))) 
    
  val temperatures = Extraction.locateTemperatures(2015, "/stations.csv", "/1975.csv") 
  println("Part one done. It seems to be working. Great.")
  val temperaturesAvg = Extraction.locationYearlyAverageRecords(temperatures)  
  
  println("Part two done. It seems to be working. Great.")
}
