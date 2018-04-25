package parallel_programming.week_3

object DataParallelProgrammingModel {
  /**
   * Returns a parallel implementation of this collection.
   */
  def initalizeArray(xs: Array[Int])(v: Int): Unit = {
    for(i <- (0 until xs.length).par) {
      xs(i) = v
    }
  }
  
  /**
   * Example: Mandelbrot Set
   * We approximate the definition of the Mandelbrot set - as long as the 
   * absolute value of z of n is less than 2, we compute z of n+1 until 
   * we do maxIterations
   */
  private def computePixel(xc: Double, yc: Double, maxIterations: Int): Int = {
    var i = 0
    var x, y = 0.0
    while (x * x + y * y < 4 && i < maxIterations) {
      val xt = x * x - y * y + xc
      val yt = 2 * x * y + yc
      x = xt
      y = yt
      i += 1
    }
    //color(i)
    i
  }
  
  /**
   * How do we render the set using data-parallel programming?
   * task-parallel implementation - the slowest
   * data-parallel implementation - about 2x faster.
   */
  /*def parRender(): Unit = {
    for (idx <- (0 until image.length).par) {
      val (xc, yc) = coordinatesFor(idx)
      image(idx) = computePixel(xc, yc, maxIterations)
    }
  }*/
  
	def main(args: Array[String]) {

	}
}