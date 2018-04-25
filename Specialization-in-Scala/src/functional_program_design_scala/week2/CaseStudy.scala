package functional_program_design_scala.week2

/**
 * The water pouring problem
 */

object CaseStudy {
	def main(args: Array[String]) {
		val problem = new Pouring(Vector(4,9,19))
		val m = problem.moves
		m.foreach(println)
		println("************")
		//problem.pathSets.take(3).toList.foreach(println)
		println(problem.solutions(17))
	}
}