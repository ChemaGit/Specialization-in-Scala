package principles_FP_scala.week_2

object MainRational {
	def main(args: Array[String]) {
    val x = new Rational(1, 3)
    val y = new Rational(5, 7)
    val z = new Rational(3, 2)
    println((x + y) * (z))
    println(x - y - z)
    println(x - y - (-z))
    //val strange = new Rational(1, 0)
	}
}