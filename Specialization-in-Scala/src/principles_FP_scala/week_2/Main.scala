package principles_FP_scala.week_2

object Main extends App {
  import FunSets._
  val s = Set(1,2,3,4)
  val s1 = Set(3,4, 5, 6)
  
  println(contains(singletonSet(1), 1))
  printSet(singletonSet(1))
  printSet(singletonSet(2))
  printSet(union(singletonSet(2), singletonSet(1)))
  printSet(union(s, s1))
  printSet(intersect(s, s1))
  printSet(diff(s, s1))
  printSet(filter(s, x => x > 1))    
  println(forall(s1 , x => x > 0))
  printSet(map(s1, x => x * x))
  println(exists(s1, x => x == 7))
}