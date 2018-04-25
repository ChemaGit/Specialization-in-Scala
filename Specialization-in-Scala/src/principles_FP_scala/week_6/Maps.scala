package principles_FP_scala.week_6

object Maps {
  
	def main(args: Array[String]) {
		val romanNumerals = Map("I" -> 1, "V" -> 5, "X" -> 10)
		val capitalOfCountry = Map("US" -> "Washington", "Switzerland" -> "Bern")
		val countryOfCapital = capitalOfCountry map {
		  case(x, y) => (y, x)
		}
		println(romanNumerals.mkString(","))
		println(capitalOfCountry.mkString(","))
		println(countryOfCapital.mkString(","))
		println(capitalOfCountry("US"))
		//println(capitalOfCountry("Andorra"))//throws NoSuchElementException
		println(capitalOfCountry.get("US"))
		println(capitalOfCountry.get("Andorra"))
		
    def showCapital(country: String) = capitalOfCountry.get(country) match {
      case Some(capital) => capital
      case None => "missing data"
    }		
		
		println(showCapital("España"))
		println(showCapital("US"))
		
		val cap1 = capitalOfCountry.withDefaultValue("<unknown>")
		println(cap1("Andorra"))
		
		println("**************************")
		val fruit = List("apple", "pear", "orange", "pineapple")
		println(fruit.sortWith( (f, p) => f.length < p.length))
		println(fruit.sorted)
		println(fruit.groupBy((s) => s.head))
		println("**************************")
		//A polynomial can be seen as a map from exponents to coefficients.
		//for instance x^3 - 2x + 5
		val pol = Map(0 -> 5, 1 -> -2, 3 -> 1)
	}
}