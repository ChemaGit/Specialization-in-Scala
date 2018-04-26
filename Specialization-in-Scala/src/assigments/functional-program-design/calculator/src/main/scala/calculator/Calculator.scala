package calculator

sealed abstract class Expr
final case class Literal(v: Double) extends Expr
final case class Ref(name: String) extends Expr
final case class Plus(a: Expr, b: Expr) extends Expr
final case class Minus(a: Expr, b: Expr) extends Expr
final case class Times(a: Expr, b: Expr) extends Expr
final case class Divide(a: Expr, b: Expr) extends Expr

object Calculator {
  def computeValues(
      namedExpressions: Map[String, Signal[Expr]]): Map[String, Signal[Double]] = {   
    (for {
      (name, expr) <- namedExpressions      
    }yield (name, Signal{eval(getReferenceExpr(name, namedExpressions),namedExpressions)}) ).toMap       
  }

  def eval(expr: Expr, references: Map[String, Signal[Expr]]): Double = {
    expr match {
      case lit: Literal => lit.v
      //eval(getReferenceExpr(rVar.name, references), references)
      case rVar: Ref => {
        val sig = getReferenceExpr(rVar.name, references)
        eval(sig, references - rVar.name)
      }
      case add: Plus => eval(add.a, references) + eval(add.b, references)
      case minus: Minus => eval(minus.a, references) - eval(minus.b, references)
      case mul: Times => eval(mul.a, references) * eval(mul.b, references)
      case div: Divide => eval(div.a, references) / eval(div.b, references)           
      case _ => Double.NaN  
    }   
  }

  /** Get the Expr for a referenced variables.
   *  If the variable is not known, returns a literal NaN.
   */
  private def getReferenceExpr(name: String,
      references: Map[String, Signal[Expr]]) = {
    references.get(name).fold[Expr] {
      Literal(Double.NaN)
    } { exprSignal =>
      exprSignal()
    }
  }
}
