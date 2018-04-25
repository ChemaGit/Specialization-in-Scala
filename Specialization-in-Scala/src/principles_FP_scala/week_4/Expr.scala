package principles_FP_scala.week_4

trait Expr {
  def isNumber: Boolean
  def isSum: Boolean
  def isProd: Boolean
  def isVar: Boolean
  def numValue: Int
  def leftOp: Expr
  def rightOp: Expr
  def eval: Int = {
    this match {
      case Number(n) => n
      case Sum(e1, e2) => e1.eval + e2.eval
      case Prod(a1, a2) => a1.eval * a2.eval
      case _ => throw new Error("Expr.Not a valid expression.")
    }
  }
  
  def show(e: Expr): String = {
    e match {
      case Number(x) => x.toString()
      case Sum(l, r) => show(l) + " + " + show(r)
    }  
  }
}

case class Number(n: Int) extends Expr {
  def isNumber: Boolean = true
  def isSum: Boolean = false
  def isProd: Boolean = false
  def isVar: Boolean = false
  def numValue = n  
  def leftOp: Expr = throw new Error("Number.leftOp")
  def rightOp: Expr = throw new Error("Number.rightOp")
}

case class Sum(e1: Expr, e2: Expr) extends Expr {
  def isNumber: Boolean = false
  def isSum: Boolean = true
  def isProd: Boolean = false
  def isVar: Boolean = false
  def numValue: Int = throw new Error("Sum.numValue")
  def leftOp: Expr = e1
  def rightOp: Expr = e2  
}

case class Prod(e1: Expr, e2: Expr) extends Expr {
  def isNumber: Boolean = false
  def isSum: Boolean = false
  def isProd: Boolean = true
  def isVar: Boolean = false
  def numValue: Int = throw new Error("Prod.numValue")
  def leftOp: Expr = e1
  def rightOp: Expr = e2
}

/*case class Var(x: String) extends Expr {
  def isNumber: Boolean = false
  def isSum: Boolean = false
  def isProd: Boolean = false
  def isVar: Boolean = true
  def numValue: Int = throw new Error("Var.numValue")
}*/

object TestExpr extends App{
  val e: Expr = Sum(Number(1), Number(44))
  println(e.show(e))
}