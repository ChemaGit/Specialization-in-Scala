package functional_program_design_scala_week_3

import org.scalacheck._
import Arbitrary._
import Gen._
import Prop._
/**
 * https://github.com/rickynils/scalacheck/blob/master/doc/UserGuide.md
 */
object ScalaCheckTest extends Properties("String"){
  /**
   * Generators 
   */
  val smallInteger = Gen.choose(0, 100)
  
  /**
   * Properties
   */
  val propConcatLists = forAll { 
    (l1: List[Int], l2: List[Int]) => l1.size + l2.size == (l1 ::: l2).size 
  }
  propConcatLists.check
  
  val propSqrt = forAll {
    (n: Int) => scala.math.sqrt(n * n) == n
  }
  propSqrt.check
  
  val propReverseList = forAll {
    l: List[String] => l.reverse.reverse == l
  }
  propReverseList.check
  
  val propConcatString = forAll {
    (s1: String, s2: String) => (s1 + s2).endsWith(s2)
  }
  propConcatString.check
  
  val propMakeList = forAll {
    n: Int => (n >= 0 && n < 10000) ==> (List.fill(n)("").length == n)
  }      
  propMakeList.check
  
  val propSmallInteger = forAll(smallInteger) {
    n => n >= 0 && n <= 100
  }
  propSmallInteger.check
  
  val propTrivial = forAll {
    n: Int => (n == 0) ==> (n == 0)
  }
  propTrivial.check
  
  /**
   * Combining Properties
   */
  val p1 = propReverseList && propConcatString
  p1.check
  val p2 = propReverseList || propConcatString
  p2.check
  val p3 = propReverseList == propConcatString
  p3.check
  val p4 = all(propReverseList,propConcatString) //same as propReverseList && propConcatString
  p4.check
  val p5 = atLeastOne(propReverseList,propConcatString) //same as propReverseList || propConcatString
	p5.check
		
  /**
   * Grouping properties
   */
  property("startsWith") = forAll {
    (a: String, b: String) => (a + b).startsWith(a)
  }
  
  property("endsWith") = forAll {
    (a: String, b: String) => (a + b).endsWith(b)
  }
  
  property("substring") = forAll {
    (a: String, b: String) => (a + b).substring(a.length) == b
  }
  
  property("substring") = forAll {
    (a: String, b: String, c: String) => (a + b + c).substring(a.length, a.length + b.length) == b
  }  	
  
  /**
   * Labeling properties
   */
  def myMagicFunction(n: Int, m: Int) = n + m
  val complexProp = forAll {
    (m: Int, n: Int) => val res = myMagicFunction(n, m)
    (res >= m)    :| "result > #1" &&
    (res >= n)    :| "result > #2" &&
    (res < m + n) :| "result not sum"
  }
  complexProp.check 
  
  val propMul = forAll {
    (n: Int, m: Int) =>
    val res = n * m
    ("evidence = " +  res) |: all(
     "div1"                |: m != 0 ==> (res / m == n),
     "div2"                |: n != 0 ==> (res / n == m),
     "lt1"                 |: res > m,
     "lt2"                 |: res > n
    )
  }
  propMul.check 
  
  /**
   * Generators
   */
  val smallInteger_1 = Gen.choose(0, 100)
  
  val myGen = for {
    n <- Gen.choose(10, 20)
    m <- Gen.choose(2 * n, 500)
  } yield(n, m)
  
  val vowel = Gen.oneOf('A', 'E', 'I','O','U','Y')
  
  val vowel_1 = Gen.frequency((3, 'A'),(4, 'E'),(2,'I'),(3, 'O'),(1, 'U'),(1, 'Y'))
  
  /**
   * Generating Case Classes
   */
  sealed abstract class Tree
  case class Node(left: Tree, right: Tree, v: Int)extends Tree
  case object Leaf extends Tree
  
  val genLeaf = const(Leaf)
  val genNode = for {
    v <- arbitrary[Int]
    left <- genTree
    right <- genTree
  }yield Node(left, right, v)
  
  def genTree: Gen[Tree] = oneOf(genLeaf, genNode)
  
  println(genTree.sample.mkString(""))
  
  /**
   * Generator of a Map of Int
   */
  lazy val genMap: Gen[Map[Int,Int]] = oneOf(
    const(Map.empty[Int,Int]),
    for {
      k <- arbitrary[Int]
      v <- arbitrary[Int]
      m <- oneOf(const(Map.empty[Int,Int]), genMap)
    } yield m.updated(k, v)
  )  
  
  /**
   * Sized generators
   */
  def matrix[T](g: Gen[T]): Gen[Seq[Seq[T]]] = Gen.sized {
    size => val side = scala.math.sqrt(size).asInstanceOf[Int]
    Gen.listOfN(side, Gen.listOfN(side, g))
  }
  
  /**
   * Condiotional Generators
   */
  val smallEvenInteger = Gen.choose(0, 200).suchThat(_ % 2 == 0)
  
  /**
   * Generating Containers
   */
  val genIntList = Gen.containerOf[List, Int](Gen.oneOf(1, 3, 5))
  val genStringStream = Gen.containerOf[Stream, String](Gen.alphaStr)
  val genBoolArray = Gen.containerOf[Array, Boolean](true)
  
  /**
   * The arbitrary Generator
   */
  val evenInteger = Arbitrary.arbitrary[Int].suchThat(_ % 2 == 0)
  
  val squares = for {
    xs <- Arbitrary.arbitrary[List[Int]]
  } yield xs.map(x => x * x)
  
  implicit lazy val arbBool: Arbitrary[Boolean] = Arbitrary(oneOf(true, false))
  
  abstract sealed class TreeT[T] {
    def merge(t: TreeT[T]) = Internal(List(this, t))
    
    def size: Int = this match {
      case LeafT(_) => 1
      case Internal(children) => (children :\ 0)(_.size + _)
    }
  }
  case class Internal[T](children: Seq[TreeT[T]]) extends TreeT[T]
  case class LeafT[T](elem: T) extends TreeT[T]
/*  
  implicit def arbTreeT[T](implicit a: Arbitrary[T]): Arbitrary[TreeT[T]] =
  Arbitrary {
    val genLeaf = for(e <- Arbitrary.arbitrary[T]) yield LeafT(e)
    
    def genInternal(sz: Int): Gen[TreeT[T]] = for {
      n <- Gen.choose(sz/3, sz/2)
      c <- Gen.listOfN(n, sizedTree(sz/2))
    } yield Internal(c)
    
    def sizedTree(sz: Int) =
      if(sz <= 0) genLeaf
      else Gen.frequency((1, genLeaf), (3, genInternal(sz))
    
    Gen.sized(sz => sizedTree(sz))
  }
  val propMergeTree = forAll{(t1: TreeT[Int], t2: TreeT[Int]) =>
    t1.size + t2.size == t1.merge(t2).size
  }
*/  
  /**
   * Collecting Generated Test Data
   */
  def ordered(l: List[Int]) = l == l.sorted
  val myProp = forAll { l: List[Int] =>
    classify(ordered(l), "ordered") {
      classify(l.length > 5, "large", "small") {
        l.reverse.reverse == l
      }
    }
  }  
  myProp.check
  
  val dummyProp = forAll(Gen.choose(1, 10)) {
    n => collect(n) {
       n == n
    }   
  }
  dummyProp.check
  
  /**
   * Test Case Minimisation
   */
  val p6 = forAllNoShrink(arbitrary[List[Int]])(l => l == l.distinct)
  val p7 = forAll(arbitrary[List[Int]])(l => l == l.distinct)
  val p8 = forAll((l: List[Int]) => l == l.distinct)
  p6.check
  p7.check
  p8.check
  
  
}