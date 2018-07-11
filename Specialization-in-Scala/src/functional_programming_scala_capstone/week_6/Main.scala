package observatory

import org.apache.log4j.{Level, Logger}
import java.io.PrintWriter
import java.io.File
import scala.io.Source
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
  //set JAVA_OPTS=-Xmx4G   ==> we set up the memory for the JVM
  // Write files with calculated temperatures
   
  val yearRange = 1975 to 2015   
  yearRange.foreach({case(year) =>{
   val temperatures = Extraction.locateTemperatures(year, "/stations.csv", "/" + year + ".csv")   
   val temperaturesAvg = Extraction.locationYearlyAverageRecords(temperatures)
   val file = new File("C:\\Users\\chema\\WorkspaceOxygen\\observatory\\target\\aux_files\\" + year + ".txt")
   println("file created: " + file.getAbsolutePath)  
   val writer = new PrintWriter(file)
   temperaturesAvg.foreach({case(loc, temp) => {
     writer.println("" + year + "," + loc.lat + "," + loc.lon + "," + temp)
   }})
   writer.close
  } })   
   Extraction.spark.sparkContext.stop()
   println("Spark has stopped")  
   
   //read the files with the temperatures and generate images
   val yearRangeTwo = 1975 to 2015
   import scala.io.Source
   yearRangeTwo.foreach({case(year: Year) => {
     println("Year: " + year)
     val file = new File("C:\\Users\\chema\\WorkspaceOxygen\\observatory\\target\\aux_files\\" + year + ".txt")
     println("Reading file: " + file.getAbsolutePath)
     if(file.canRead) {
       val temperaturesAvg = Source.fromFile(file).getLines().map({case(line) => { 
         val arr = line.split(",") 
         val lat = arr(1).toDouble
         val lon = arr(2).toDouble
         val temp: Temperature = arr(3).toDouble
         (Location(lat, lon), temp)
       }}).toSeq
       val yearlyData = Vector((year, temperaturesAvg))
       Interaction.generateTiles(yearlyData, Interaction.generateImage)
     } else {
       println("Can't read file: " + file.getAbsolutePath)       
     }
   }})  
    
  /**  
   * END First Task  
   * Generate tiles from 1975 to 2015   
   */    
    
  /**
   * Second Task
   * Generate Deviations from 1990 to 2015
   */
   val yearRangeNormals = 1975 to 1989
   //We read from a file the temperatures and generate the normals temperatures from 1975 to 1989
   val listTemperatures = yearRangeNormals.map({case(year) => {
     val file = new File("C:\\Users\\chema\\WorkspaceOxygen\\observatory\\target\\aux_files\\" + year + ".txt")
     if(file.canRead()) {
       Source.fromFile(file).getLines().map({case(line) => { 
        val arr = line.split(",") 
        val lat = arr(1).toDouble
        val lon = arr(2).toDouble
        val temp: Temperature = arr(3).toDouble
        (Location(lat, lon), temp)
      }}).toList      
     } else {
       println("Can't read file: " + file.getAbsolutePath)
       List()
     }
   }}).flatten    
  val tempGroup = listTemperatures.groupBy({case(loc, temp) => loc})   
  val auxGroup = tempGroup.mapValues(f => f.map({case(loc, temp) => temp}))   
  val averages = auxGroup.map({case(loc, temps) => (loc, temps.sum / temps.size)}).toList
  
  
  //calculate Deviations
  val yearRangeDeviations = 1990 to 2015
  yearRangeDeviations.foreach({case(year) => {
    val file = new File("C:\\Users\\chema\\WorkspaceOxygen\\observatory\\target\\aux_files\\" + year + ".txt")
     if(file.canRead()) {
       val fileDev = new File("C:\\Users\\chema\\WorkspaceOxygen\\observatory\\target\\aux_files\\deviation_" + year + ".txt")
       val writer = new PrintWriter(fileDev)
       Source.fromFile(file).getLines().foreach({case(line) => { 
        val arr = line.split(",") 
        val lat = arr(1).toDouble
        val lon = arr(2).toDouble
        val temp: Temperature = arr(3).toDouble
        val normal = averages.filter({case(loc, tempAvg) => loc == Location(lat, lon)})        
        val deviation = if(!normal.isEmpty) {
          (Location(lat, lon), temp - normal.head._2)
        } else {
          (Location(lat, lon), 0.0)        
        }
        writer.println("" + year + "," + deviation._1.lat + "," + deviation._1.lon + "," + deviation._2)
      }})   
      writer.close
     } else {
       println("Can't read file: " + file.getAbsolutePath)
       List()
     } 
  }})
  
  //Generate images deviations 
  //set JAVA_OPTS=-Xmx4G   ==> we set up the memory for the JVM
  //val yearRangeDeviations = 1990 to 2015
  yearRangeDeviations.foreach({case(year) => {
    println("Year: " + year)
    val file = new File("C:\\Users\\chema\\WorkspaceOxygen\\observatory\\target\\aux_files\\deviation_" + year + ".txt")
    println("Reading file: " + file.getAbsolutePath)
    if(file.canRead) {
       val temperaturesDeviation = Source.fromFile(file).getLines().map({case(line) => { 
         val arr = line.split(",") 
         val lat = arr(1).toDouble
         val lon = arr(2).toDouble
         val temp: Temperature = arr(3).toDouble
         (Location(lat, lon), temp)
       }}).toSeq
       val yearlyData = Vector((year, temperaturesDeviation))
       Interaction.generateTiles(yearlyData, Interaction.generateImageDeviation)      
    } else {
      println("Can't read file: " + file.getAbsolutePath)
    }
  }})
  
  /**
   * END Second Task
   * Generate Deviations from 1990 to 2015
   */  
 
  /*val yearRangeNormals = 1975 to 1989
  val yearRangeDeviations = 1990 to 2015
  
  //We read from a file the temperatures and generate the normals temperatures from 1975 to 1989
  val tempNormal = yearRangeNormals.map({case(year) =>{    
    val file = new File("C:\\Users\\chema\\WorkspaceOxygen\\observatory\\target\\aux_files\\" + year + ".txt")
    val temperaturesAvg = if(file.canRead()){
      Source.fromFile(file).getLines().map({case(line) => { 
        val arr = line.split(",") 
        val lat = arr(1).toDouble
        val lon = arr(2).toDouble
        val temp: Temperature = arr(3).toDouble
        (Location(lat, lon), temp)
      }}).toSeq      
    }else{
      println("Can't read file: " + file.getAbsolutePath)
      Seq()
    }        
    temperaturesAvg
  }})
  println("normal from 1975 to 1989: " + tempNormal.length)
  tempNormal.foreach({case(temps) => {
    println("size: " + temps.length)
  }})
  
  //We read from a file the temperatures to calculate the deviations 
  val tempNormalDeviations = yearRangeDeviations.map({case(year) => {
    val file = new File("C:\\Users\\chema\\WorkspaceOxygen\\observatory\\target\\aux_files\\" + year + ".txt")
    val temperaturesAvg = if(file.canRead()){      
      Source.fromFile(file).getLines().map({case(line) => { 
        val arr = line.split(",") 
        val lat = arr(1).toDouble
        val lon = arr(2).toDouble
        val temp: Temperature = arr(3).toDouble
        (Location(lat, lon), temp)
      }}).toSeq      
    }else{
      println("Can't read file: " + file.getAbsolutePath)
      Seq()
    }        
    (temperaturesAvg, year)
  }})
  println("normal from 1990 to 2015: " + tempNormalDeviations.length)
  tempNormalDeviations.foreach({case(temps, year) => {
    println("year: " + year + "==>> size: " + temps.length)
  }})
  
    
  //We can generate tiles easily right now and bit by bit
  println("Calculate tempNormalDeviations from 1990 to 2015")
  println("tile size: " + generateTile(0).length)
  generateTile(0).foreach({case(tile) =>{    
      println("tile x: " + tile.x + " ==>> tile y: " + tile.y + " ==>> zoom: " + tile.zoom)
      tempNormalDeviations.foreach({case(data, year) => { visualizeGrid( deviation(data,average(tempNormal)), devToColor, tile)
        .output(new java.io.File("target/deviations/" + year + "/" + tile.zoom + "/" + tile.x + "-" + tile.y + ".png"))
        println("Tile deviation generated for year: " + year)
      }})    
  }})*/ 
}
