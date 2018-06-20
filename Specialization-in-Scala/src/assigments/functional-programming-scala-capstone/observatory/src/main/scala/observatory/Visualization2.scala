package observatory

import com.sksamuel.scrimage.{Image, Pixel}
import scala.collection.parallel.ParSeq
import Visualization._

import java.text.DecimalFormat

/**
  * 5th milestone: value-added information visualization
  */
object Visualization2 {
  val P = 8
  val IMAGE_WIDTH = 256
  val IMAGE_HEIGHT = 256  
  val ALPHA = 127  

  /**
    * @param point (x, y) coordinates of a point in the grid cell
    * @param d00 Top-left value
    * @param d01 Bottom-left value
    * @param d10 Top-right value
    * @param d11 Bottom-right value
    * @return A guess of the value at (x, y) based on the four known values, using bilinear interpolation
    *         See https://en.wikipedia.org/wiki/Bilinear_interpolation#Unit_Square
    */
  def bilinearInterpolation(
    point: CellPoint,
    d00: Temperature,
    d01: Temperature,
    d10: Temperature,
    d11: Temperature
  ): Temperature = {
    d00 * (1 - point.x) * (1 - point.y) + d10 * point.x * (1 - point.y) + d01 * (1 - point.x) * point.y + d11 * point.x * point.y  
  }
  
  def auxGrid(grid: GridLocation => Temperature,
                  loc: Location): Temperature = {
    val lt = loc.lat.toInt
    val ln = loc.lon.toInt
    val d00 = GridLocation(lt, ln)
    val d01 = GridLocation(lt + 1, ln)
    val d10 = GridLocation(lt, ln + 1)
    val d11 = GridLocation(lt + 1, ln + 1)
    val cellPoint = CellPoint(loc.lon - ln, loc.lat - lt)
    bilinearInterpolation(cellPoint, grid(d00), grid(d01), grid(d10), grid(d11))
  }  
  
  def generateCoords: Seq[(Int, Int)] = {
    for {
      lt <- 0 until IMAGE_WIDTH 
      ln <- 0 until IMAGE_HEIGHT
    }yield {
      (lt, ln)
    }
  }  

  /**
    * @param grid Grid to visualize
    * @param colors Color scale to use
    * @param tile Tile coordinates to visualize
    * @return The image of the tile at (x, y, zoom) showing the grid using the given color scale
    */
  def visualizeGrid(
    grid: GridLocation => Temperature,
    colors: Iterable[(Temperature, Color)],
    tile: Tile
  ): Image = {
    val coordinates = generateCoords.par.map({case(lt, ln) => Tile((tile.x * (1 << P)) + lt, (tile.y * (1 << P)) + ln, tile.zoom + P).toLocation}) 
                                    .map({case(location) => auxGrid(grid, location)})
                                    .map({case(temperature) => interpolateColor(colors, temperature)})
                                    .map({color => Pixel(color.red, color.green, color.blue, ALPHA)}).toArray 
    Image(IMAGE_WIDTH, IMAGE_HEIGHT, coordinates)                                    
  }
  
  def generateTile(zomMax: Int): Seq[Tile] = {
    for{zoom <- 0 to zomMax        
        x <- 0 until (1 << zoom)
        y <- 0 until (1 << zoom)                 
    }yield Tile(x, y, zoom)    
  }
    
  
  def main(args: Array[String]): Unit = {
    val points:Iterable[(Temperature, Color)] = Iterable((7.0, Color(0,0,0)),(4.0, Color(255,0,0)),(2.0, Color(255,255,0)),
        (0.0, Color(255,255,255)),(-2.0, Color(0,255,255)),(-7.0, Color(0,0,255)))    
  }

}
