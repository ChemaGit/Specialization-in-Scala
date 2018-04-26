package quickcheck

import common._

import org.scalacheck._
import Arbitrary._
import Gen._
import Prop._

abstract class QuickCheckHeap extends Properties("Heap") with IntHeap {

  val genA = arbitrary[A]        
  lazy val genHeap: Gen[H] = for {
    x <- genA
    he <- Gen.frequency((1, genHeap),(1, empty))   
  } yield insert(x, he)
  
  implicit lazy val arbHeap: Arbitrary[H] = Arbitrary(genHeap)

    /**
     * check that adding a single element to an empty heap,
     * and the removing this element, should yield the element in question
     */
    property("min1") = forAll { a: Int =>
      val h = insert(a, empty)
      findMin(h) == a
    }  
    
    /**
     * for any heap, adding the minimal element,
     * and then finding in, should return the element in question
     */
    property("gen1") = forAll { (h: H) =>
      val m = if (isEmpty(h)) 0 else findMin(h)      
      findMin(insert(m, h)) == m
    }
    
    /**
     * If you insert any two elements into an empty heap, finding the 
     * minimum of the resulting heap should get the smallest of the two elements
     */
    property("smallest") = forAll { (x: A, y: A) =>
      val c = x.min(y)
      findMin(insert(y, insert(x, empty))) == c
    }    
    
    /**
     * If you insert an element into an empty heap, then delete the minimum, the 
     * resulting heap should be empty.
     */
    property("empty") = forAll { (x: A) =>
      isEmpty(deleteMin(insert(x, empty))) 
    }   
    
    /**
     * Given any heap, you should get a sorted sequence of elements when continually
     * finding and deleting minima. (Hint. recursion and helper functions are your friends.)
     */
    def getList(z: H, acc: List[A]):List[A] = {
      z match {
        case t :: ts => {
          val min = findMin(z)
          val h = deleteMin(z)
          getList(h, min :: acc)
        }        
        case List() => acc
      }
    }
    property("sorted_sequence") = forAll { (h: H) =>
      val lSorted = getList(h, List())
      lSorted == lSorted.sorted
    }
    

    
    /**
     * Finding a minimum of the melding of any two heaps should return a minimum
     * of one or the other.
     */
    property("melding_minimum") = forAll { (a: H, b: H) =>
      val min = if(isEmpty(a) && !isEmpty(b)) findMin(b)
                else if(isEmpty(b) && !isEmpty(a)) findMin(a)
                else if(isEmpty(a) && isEmpty(b)) findMin(insert(0, a))
                else findMin(a).min(findMin(b)) 
                     
      findMin(meld(a, b)) == min      
    }
}
