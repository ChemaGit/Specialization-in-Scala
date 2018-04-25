package principles_FP_scala.week_6

class Poly(terms0: Map[Int, Double]) {
  def this(bindings: (Int, Double)*) = this(bindings.toMap)
  
  val terms = terms0.withDefaultValue(0.0)
  
  //def + (other: Poly) = new Poly(terms ++ (other.terms.map(adjust)))
  
  def addTerm(terms: Map[Int, Double], term: (Int, Double)): Map[Int, Double] = {
    val (exp, coeff) = term
    terms + (exp -> (coeff + terms(exp)))
  }  
  
  def + (other: Poly) = new Poly(other.terms.foldLeft(this.terms)(addTerm))     
  
  def adjust(term: (Int, Double)): (Int, Double) = {
    val (exp, coeff) = term
    exp -> (coeff + terms(exp))   
  }
  
  override def toString =
    (for ((exp, coeff) <- terms.toList.sorted.reverse)
      yield coeff + "x^" + exp).mkString(" + ")
}

object Poly {
	def main(args: Array[String]) {
		val p1 = new Poly(1 -> 2.0, 3 -> 4.0, 5 -> 6.2)
		val p2 = new Poly(0 -> 3.0, 3 -> 7.0)
		println(p1 + p2)
	}
}