package functional_program_design_scala.week2

object StructuralInductionOnTrees {
  
  abstract class IntSet {
    def incl(x: Int): IntSet
    def contains(x: Int): Boolean
    def union(other: IntSet): IntSet
  }
  
  object Empty extends IntSet {
    def contains(x: Int): Boolean = false
    def incl(x: Int): IntSet = NonEmpty(x, Empty, Empty)
    def union(other: IntSet) = other
  }
  
  case class NonEmpty(elem: Int, left: IntSet, right: IntSet) extends IntSet {
    def contains(x: Int): Boolean = 
      if(x < elem) left contains x
      else if(x > elem) right contains x
      else true
      
    def incl(x: Int): IntSet =
      if(x < elem) NonEmpty(elem, left incl x, right)
      else if(x > elem) NonEmpty(elem, left, right incl x)
      else this
      
    def union(other: IntSet): IntSet = (left union (right union (other))) incl elem  
  }
  
	def main(args: Array[String]) {
		
	}
}