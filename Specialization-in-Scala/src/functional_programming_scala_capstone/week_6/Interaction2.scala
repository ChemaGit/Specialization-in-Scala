package observatory

/**
  * 6th (and last) milestone: user interface polishing
  */
object Interaction2 {

    val colorTemperature: Seq[(Temperature, Color)] = Seq(
      (60, Color(red = 255, green = 255, blue = 255)),
      (32, Color(red = 255, green = 0, blue = 0)),
      (12, Color(red = 255, green = 255, blue = 0)),
      (0, Color(red = 0, green = 255, blue = 255)),
      (-15, Color(red = 0, green = 0, blue = 255)),
      (-27, Color(red = 255, green = 0, blue = 255)),
      (-50, Color(red = 33, green = 0, blue = 107)),
      (-60, Color(red = 0, green = 0, blue = 0))
    )

    val colorDeviation: Seq[(Temperature, Color)] = Seq(
      (7, Color(red = 0, green = 0, blue = 0)),
      (4, Color(red = 255, green = 0, blue = 0)),
      (2, Color(red = 255, green = 255, blue = 0)),
      (0, Color(red = 255, green = 255, blue = 255)),
      (-2, Color(red = 0, green = 255, blue = 255)),
      (-7, Color(red = 0, green = 0, blue = 255))
    )  
  
  /**
    * @return The available layers of the application
    */
  def availableLayers: Seq[Layer] = {
    Seq(
      Layer(LayerName.Temperatures, colorTemperature, 1975 to 2015),
      Layer(LayerName.Deviations, colorDeviation, 1975 to 2015)
    )
  }

  /**
    * @param selectedLayer A signal carrying the layer selected by the user
    * @return A signal containing the year bounds corresponding to the selected layer
    */
  def yearBounds(selectedLayer: Signal[Layer]): Signal[Range] = {
    Signal(selectedLayer().bounds)
  }

  /**
    * @param selectedLayer The selected layer
    * @param sliderValue The value of the year slider
    * @return The value of the selected year, so that it never goes out of the layer bounds.
    *         If the value of `sliderValue` is out of the `selectedLayer` bounds,
    *         this method should return the closest value that is included
    *         in the `selectedLayer` bounds.
    */
  def yearSelection(selectedLayer: Signal[Layer], sliderValue: Signal[Year]): Signal[Year] = {
    
    Signal(if(sliderValue() >= selectedLayer().bounds.max) selectedLayer().bounds.max
           else if(sliderValue() <= selectedLayer().bounds.min) selectedLayer().bounds.min
           else sliderValue() + 1)    
  }

  /**
    * @param selectedLayer The selected layer
    * @param selectedYear The selected year
    * @return The URL pattern to retrieve tiles
    */
  def layerUrlPattern(selectedLayer: Signal[Layer], selectedYear: Signal[Year]): Signal[String] = {
     Signal(s"./target/${selectedLayer().layerName.id}/${selectedYear()}/{z}/{x}-{y}.png")
  }

  /**
    * @param selectedLayer The selected layer
    * @param selectedYear The selected year
    * @return The caption to show
    */
  def caption(selectedLayer: Signal[Layer], selectedYear: Signal[Year]): Signal[String] = {
    Signal(s"${selectedLayer().layerName.id.capitalize} (${selectedYear()})")
  }

}

sealed abstract class LayerName(val id: String)
object LayerName {
  case object Temperatures extends LayerName("Temperatures")
  case object Deviations extends LayerName("Deviations")
}

/**
  * @param layerName Name of the layer
  * @param colorScale Color scale used by the layer
  * @param bounds Minimum and maximum year supported by the layer
  */
case class Layer(layerName: LayerName, colorScale: Seq[(Temperature, Color)], bounds: Range)
