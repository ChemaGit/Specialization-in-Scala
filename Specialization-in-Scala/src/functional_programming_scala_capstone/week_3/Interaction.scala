package observatory

import com.sksamuel.scrimage.{Image, Pixel}
import Visualization.{predictTemperature, interpolateColor}

/**
  * 3rd milestone: interactive visualization
  */
object Interaction {
  val ALPHA = 127
  val IMAGE_WIDTH = 256
  val IMAGE_HEIGHT = 256
  val P = 8
  /**
    * @param tile Tile coordinates
    * @return The latitude and longitude of the top-left corner of the tile, as per http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames
    */
  def tileLocation(tile: Tile): Location = {
    tile.toLocation
  }

  /**
    * @param temperatures Known temperatures
    * @param colors Color scale
    * @param tile Tile coordinates
    * @return A 256×256 image showing the contents of the given tile
    */
  def tile(temperatures: Iterable[(Location, Temperature)], colors: Iterable[(Temperature, Color)], tile: Tile): Image = {
    val coordinates = for{h <- 0 until IMAGE_HEIGHT
                          w <- 0 until IMAGE_WIDTH} yield(h, w)
    val arrPixel = coordinates.par.map({case(h, w) => Tile((tile.x * (1 << P)) + w, (tile.y * (1 << P)) + h, tile.zoom + P).toLocation })
                              .map({case(location) => interpolateColor(colors, predictTemperature(temperatures, location))})
                              .map({case(color) => Pixel(color.red, color.green, color.blue, ALPHA)}).toArray
    Image(IMAGE_WIDTH, IMAGE_HEIGHT, arrPixel)                           
  }

  /**
    * Generates all the tiles for zoom levels 0 to 3 (included), for all the given years.
    * @param yearlyData Sequence of (year, data), where `data` is some data associated with
    *                   `year`. The type of `data` can be anything.
    * @param generateImage Function that generates an image given a year, a zoom level, the x and
    *                      y coordinates of the tile and the data to build the image from
    */
  def generateTiles[Data](
    yearlyData: Iterable[(Year, Data)],
    generateImage: (Year, Tile, Data) => Unit
  ): Unit = { 
    for{zoom <- 0 to 3
        (year, data) <- yearlyData
        x <- 0 until (1 << zoom)
        y <- 0 until (1 << zoom)                 
    } {
      generateImage(year, Tile(x, y, zoom), data)
    }
  }
  
  /** User Defined function for generating the images
    *
    * @param year         Year number
    * @param zoom         Zoom level
    * @param x            X coordinate
    * @param y            Y coordinate
    * @param inputs       Known temperatures
    */
  def generateImage(year: Int,
                    tile: Tile,
                    inputs: Iterable[(Location, Double)]): Unit = {
    val points:Iterable[(Temperature, Color)] = Iterable((60.0, Color(255,255,255)),(32.0, Color(255,0,0)),(12.0, Color(255,255,0)),
        (0.0, Color(0,255,255)),(-15.0, Color(0,0,255)),(-27.0, Color(255,0,255)),(-50.0, Color(33,0,107)),(-60.0, Color(0,0,0)))  
    // location: 'target/temperatures/<year>/<zoom>/<x>-<y>.png'
    Interaction.tile(inputs,points, tile).output(new java.io.File(s"target/temperatures/$year/$tile.zoom/$tile.x-$tile.y.png"))
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
        
    tile(data, points, Tile(40, 56, 4)).output(new java.io.File("target/some-image.png"))    
  }
}
