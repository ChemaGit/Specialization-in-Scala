package principles_FP_scala.week_3

class EmptyE extends IntSet{
  def contains(x: Int): Boolean = false
  def incl(x: Int): IntSet = new NonEmptyE(x, new EmptyE, new EmptyE) 
  def union(other: IntSet): IntSet = other
  
  override def toString = "."
}