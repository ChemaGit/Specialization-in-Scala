package observatory

import Visualization._
import java.text.DecimalFormat

/**
  * 4th milestone: value-added information
  */
object Manipulation {
  
  val df = new DecimalFormat("0.00000000");
  
  def formatDouble(value: Double): Temperature = {
    val temp: Temperature = java.lang.Double.valueOf (df.format(value).replace(",", "."))
    temp
  }     

  /**
    * @param temperatures Known temperatures
    * @return A function that, given a latitude in [-89, 90] and a longitude in [-180, 179],
    *         returns the predicted temperature at this location
    */
  def makeGrid(temperatures: Iterable[(Location, Temperature)]): GridLocation => Temperature = GridLocation =>{
    formatDouble(predictTemperature(temperatures, Location(GridLocation.lat, GridLocation.lon)))
  }
  
  /**
    * @param temperaturess Sequence of known temperatures over the years (each element of the collection
    *                      is a collection of pairs of location and temperature)
    * @return A function that, given a latitude and a longitude, returns the average temperature at this location
    */
  def average(temperaturess: Iterable[Iterable[(Location, Temperature)]]): GridLocation => Temperature = GridLocation =>{
    val tmps = temperaturess.map(temperature => makeGrid(temperature)(GridLocation)).toList
    formatDouble(tmps.sum / tmps.length)
  }

  /**
    * @param temperatures Known temperatures
    * @param normals A grid containing the “normal” temperatures
    * @return A grid containing the deviations compared to the normal temperatures
    */
  def deviation(temperatures: Iterable[(Location, Temperature)], normals: GridLocation => Temperature): GridLocation => Temperature = GridLocation =>{
    formatDouble(makeGrid(temperatures)(GridLocation) - normals(GridLocation))
  }
}

