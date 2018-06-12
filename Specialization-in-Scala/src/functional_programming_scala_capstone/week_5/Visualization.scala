package observatory

import com.sksamuel.scrimage.{Image, Pixel}
import scala.math._
import java.text.DecimalFormat
import scala.collection.parallel.ParSeq

/**
  * 2nd milestone: basic visualization
  */
object Visualization {
  
  val P = 8.0
  //Radius for spherical Earth
  val EARTH_RADIUS = 6371.0
  //very close
  val VERY_CLOSE = 1.0
  
  val IMAGE_WIDTH = 360
  val IMAGE_HEIGHT = 180  
  val ALPHA = 127
  
  val df = new DecimalFormat("0.00000000");
  
  def formatDouble(value: Double): Temperature = {
    val temp: Temperature = java.lang.Double.valueOf (df.format(value).replace(",", "."))
    temp
  }     
  
  /**
   * check if location is antipodes of the 
   * otehr location
   */
  def antipodes(loc: Location, loc1: Location): Boolean = {    
    loc.lat == -loc1.lat && abs(loc.lon - loc1.lon) == 180
  }
  
  /**
   * The great-circle distance or orthodromic distance is the shortest distance 
   * between two points on the surface of a sphere, 
   * measured along the surface of the sphere 
   * (as opposed to a straight line through the sphere's interior).
   * @param
   * @return
   */
  def greatCircleDistance(location: Location, location1: Location): Double = {
    val Location(locationLat, locationLon) = location
    val Location(locationLat1, locationLon1) = location1
    if(location == location1) 0
    else if(antipodes(location, location1)) EARTH_RADIUS * Pi
    else {
      val latRad = toRadians(location.lat)
      val latRad1 = toRadians(location1.lat)
      val lonRad = toRadians(location.lon)
      val lonRad1 = toRadians(location1.lon)  
      val sigma = acos(sin(latRad) * sin(latRad1) + cos(latRad) * cos(latRad1) * cos(abs(lonRad - lonRad1)))
      sigma * EARTH_RADIUS
    }    
  }
  
  /**
   * Inverse distance weighting (IDW) is a type of deterministic method for multivariate 
   * interpolation with a known scattered set of points. 
   */
  def inverseDistanceWeighting(distTemp: List[(Double, Temperature)]): Temperature = {
    val weightTemp = distTemp.map({case(dist, temp) => ( (1/pow(dist, P))* temp, 1/pow(dist, P))})
    val norm = weightTemp.foldLeft(0.0,0.0)((p,z) => (p._1 + z._1, p._2 + z._2) )
    formatDouble(norm._1 / norm._2) 
  }
  
  /*Another way to do this
   * def inverseDistanceWeighting(dists: List[(Double, Temperature)]): Temperature = {
    val weights = dists.map(entry => (1 / pow(entry._1, P), entry._2))
    val normalizer = weights.map(_._1).sum
    formatDouble(weights.map(entry => entry._1 * entry._2).sum  / normalizer) 
  }*/    
  

  /**
    * @param temperatures Known temperatures: pairs containing a location and the temperature at this location
    * @param location Location where to predict the temperature
    * @return The predicted temperature at `location`
    */
  def predictTemperature(temperatures: Iterable[(Location, Temperature)], location: Location): Temperature = {    
    val distTemp = temperatures.map({case(loc, temp) => (greatCircleDistance(loc, location),temp) }).toList
                              .sortBy({case(dist,temp) => dist})       
    if(distTemp.head._1 < VERY_CLOSE) distTemp.head._2 //is location is very close
    else inverseDistanceWeighting(distTemp) //spatial interpolation
  }

  def interpolation(pointsOrd: List[(Temperature, Color)], value: Temperature): Color = {
    def aux(weightX: Double, x: Int, weightY: Double, y: Int): Int = {
      ((weightX * x + weightY * y) / (weightX + weightY)).round.toInt
    }
    
    val (smaller, greater) = pointsOrd.partition({case(temp, color) => temp < value})
    val minInterval = smaller.maxBy({case(temp, color) => temp})
    val maxInterval= greater.head   
    val weightMin = 1 / abs(minInterval._1 - value)
    val weightMax = 1 / abs(maxInterval._1 - value) 
    
    Color(aux(weightMin,minInterval._2.red,weightMax,maxInterval._2.red),
          aux(weightMin,minInterval._2.green,weightMax,maxInterval._2.green),
          aux(weightMin,minInterval._2.blue,weightMax,maxInterval._2.blue))
  }

  /**
    * @param points Pairs containing a value and its associated color
    * @param value The value to interpolate
    * @return The color that corresponds to `value`, according to the color scale defined by `points`
    */
  def interpolateColor(points: Iterable[(Temperature, Color)], value: Temperature): Color = {
    val pointsOrd = points.toList.sortBy({case(temp, color) => temp})
    val minTemp = pointsOrd.head
    val maxTemp = pointsOrd.maxBy({case(temp, color) => temp})
    if(value <= minTemp._1) minTemp._2
    else if(value >= maxTemp._1) maxTemp._2
    else{
      interpolation(pointsOrd, value)
    }
  }
  
  def generateCoords : ParSeq[Location] = {
    (for {
      lat <- 90 until -90 by -1
      lon <- -180 until 180
    } yield Location(lat, lon)).par    
  }

  /**
    * @param temperatures Known temperatures
    * @param colors Color scale
    * @return A 360×180 image where each pixel shows the predicted temperature at its location
    */
  def visualize(temperatures: Iterable[(Location, Temperature)], colors: Iterable[(Temperature, Color)]): Image = {
    val coords = generateCoords
    val arrPixel = coords.map({case(loc) => interpolateColor(colors, predictTemperature(temperatures, loc))})
                         .map({case(c) => Pixel(c.red, c.green, c.blue, ALPHA)}).toArray
     Image(IMAGE_WIDTH, IMAGE_HEIGHT, arrPixel)                            
  }
  
  def main(args: Array[String]): Unit = {
    val data: Iterable[(Location, Temperature)] = Iterable((Location(52.195,77.074),4.78), (Location(49.973,6.693),9.98),
        (Location(65.417,26.967),3.67),(Location(55.333,42.117),6.71),(Location(34.433,108.967),15.36),(Location(33.821,35.488),21.63),
        (Location(59.617,9.633),6.43),(Location(58.204,8.085),8.42),(Location(14.74,-17.49),21.09),(Location(58.307,26.69),7.44),
        (Location(35.017,-1.45),18.22),(Location(30.383,76.767),13.71),(Location(41.933,45.383),13.26),(Location(19.717,96.217),28.95),                                           
        (Location(56.083,49.867),-3.61),(Location(26.85,116.333),19.34),(Location(33.412,36.516),17.91),(Location(50.067,132.133),1.5),                                            
        (Location(37.483,69.383),17.19),(Location(55.817,94.3),2.79),(Location(47.867,28.217),15.29),(Location(41.274,-7.72),13.78),                                            
        (Location(47.153,-1.611),12.9),(Location(60.417,24.4),6.3),(Location(64.533,12.383),4.74),(Location(62.783,148.167),0.74),                                            
        (Location(57.15,31.183),6.73),(Location(40.733,72.333),14.06),(Location(13.4,-6.15),28.67),(Location(43.15,133.017),6.36))
    //println(predictTemperature(data, Location(69.617,10.633)))  
    val points:Iterable[(Temperature, Color)] = Iterable((60.0, Color(255,255,255)),(32.0, Color(255,0,0)),(12.0, Color(255,255,0)),
        (0.0, Color(0,255,255)),(-15.0, Color(0,0,255)),(-27.0, Color(255,0,255)),(-50.0, Color(33,0,107)),(-60.0, Color(0,0,0)))
    //println(interpolateColor(points, 10.0))   
    visualize(data, points).output(new java.io.File("target/some-image.png"))  
  }
}

