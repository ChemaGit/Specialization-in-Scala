package principles_FP_scala.week_4

object Zero extends Nat{
  override def isZero: Boolean = true
  def predecessor: Nat = throw new Error("0.predecessor")
  def + (that: Nat): Nat = that
  def - (that: Nat): Nat = if(that.isZero) this else throw new Error("negative number")
}