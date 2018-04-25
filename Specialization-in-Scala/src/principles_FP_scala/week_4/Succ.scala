package principles_FP_scala.week_4

class Succ(n: Nat) extends Nat{
  override def isZero: Boolean = false
  def predecessor: Nat = n
  def + (that: Nat): Nat = new Succ(n + that)
  def - (that: Nat): Nat = if(that.isZero) this else n - that.predecessor
}