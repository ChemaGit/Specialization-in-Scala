package observatory

import org.apache.log4j.{Level, Logger}
import Visualization2.{visualizeGrid,generateTile}
import Manipulation.{deviation, average}

object Main extends App {
  
  Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
  
  val tempToColor = List[(Temperature, Color)]((60, Color(255, 255, 255)), (32, Color(255, 0, 0)),
    (12, Color(255, 255, 0)), (0, Color(0, 255, 255)), (-15, Color(0, 0, 255)), (-27, Color(255, 0, 255)),
    (-50, Color(33, 0, 107)), (-60, Color(0, 0, 0)))

  val devToColor = List[(Temperature, Color)]((7, Color(0, 0, 0)), (4, Color(255, 0, 0)),
    (2, Color(255, 255, 0)), (0, Color(255, 255, 255)), (-2, Color(0, 255, 255)), (-7, Color(0, 0, 255))) 
    
  //val temperatures = Extraction.locateTemperatures(2015, "/stations.csv", "/1975.csv") 
  //println("Part one done. It seems to be working. Great.")
  //val temperaturesAvg = Extraction.locationYearlyAverageRecords(temperatures)    
  //println("Part two done. It seems to be working. Great.")  
  //println("temperaturesAvg size: " + temperaturesAvg.size)
  //val yearlyData = Vector((1975, temperaturesAvg))
  //Visualization.visualize(temperaturesAvg, tempToColor).output(new java.io.File("target/some-image.png"))
  //Interaction.generateTiles(yearlyData, Interaction.generateImage)
  //println("Tiles generated for year 1975.")
    
  /**  
   * First Task  
   * Generate tiles from 1975 to 2015   
   */
  val yearRange = 1979 to 2015  
  yearRange.foreach({case(year) =>{ 
    val temperatures = Extraction.locateTemperatures(year, "/stations.csv", "/" + year + ".csv")
    val temperaturesAvg = Extraction.locationYearlyAverageRecords(temperatures)
    val yearlyData = Vector((year, temperaturesAvg))
    Interaction.generateTiles(yearlyData, Interaction.generateImage)
    println("Tile generated for year: " + year)   
  }})
  
  /**
   * Second Task
   * Generate Deviations from 1990 to 2015
   */
  val yearRangeNormals = 1975 to 1989
  val yearRangeDeviations = 1990 to 2015
  
  val tempNormal = yearRangeNormals.map({case(year) =>{
    val temperatures = Extraction.locateTemperatures(year, "/stations.csv", "/" + year + ".csv")
    val temperaturesAvg = Extraction.locationYearlyAverageRecords(temperatures)
    temperaturesAvg
  }}).toList
  println("Calculate tempNormal from 1975 to 1989")
  val tempNormalDeviations = yearRangeDeviations.map({case(year) =>{
    val temperatures = Extraction.locateTemperatures(year, "/stations.csv", "/" + year + ".csv")
    val temperaturesAvg = Extraction.locationYearlyAverageRecords(temperatures)
    (temperaturesAvg,year)
  }}).toList
  println("Calculate tempNormalDeviations from 1990 to 2015")
  generateTile(1).foreach({case(tile) =>{    
      tempNormalDeviations.foreach({case(data, year) => { visualizeGrid( deviation(data,average(tempNormal)), devToColor, tile)
        .output(new java.io.File("target/deviations/" + year + "/" + tile.zoom + "/" + tile.x + "-" + tile.y + ".png"))
        println("Tile deviation generated for year: " + year)
      }})    
  }})
  
  Extraction.spark.sparkContext.stop()
}
