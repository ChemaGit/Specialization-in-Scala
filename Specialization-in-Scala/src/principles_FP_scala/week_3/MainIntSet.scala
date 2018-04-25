package principles_FP_scala.week_3

object MainIntSet {
	def main(args: Array[String]) {
		val t1 = new NonEmptyE(4, new EmptyE, new EmptyE)
		val t2 = t1 incl 5
		val t3 = new NonEmptyE(7, t1, new EmptyE)
		val t4 = new NonEmptyE(8, new EmptyE, new EmptyE)
		println(t1)
		println(t2)
		println(t3)
		println(t1 union t4)
		println(t2 union t3)   
	}
}