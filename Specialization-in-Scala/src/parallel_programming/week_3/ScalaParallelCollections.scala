package parallel_programming.week_3

import scala.collection._

import org.scalameter._

import java.util.concurrent._

object ScalaParallelCollections {
  
  val standardConfig = config (
      Key.exec.minWarmupRuns -> 10,
      Key.exec.maxWarmupRuns -> 20,
      Key.exec.benchRuns -> 20,
      Key.verbose -> true
  ) withWarmer(new Warmer.Default)
  
  val memConfig = config (
      Key.exec.minWarmupRuns -> 0,
      Key.exec.maxWarmupRuns -> 0,
      Key.exec.benchRuns -> 10,
      Key.verbose -> true
  ) withWarmer(Warmer.Zero)	  
  
  /**
   * Writing Parallelism-Agnostic Code
   * Example: find the largest palindrome in the sequence
   */
  def largestPalindrome(xs: GenSeq[Int]): Int = {
    xs.aggregate(Int.MinValue)((largest, n) => if(n > largest && n.toString == n.toString.reverse) n else largest, math.max)
  }
  
  /**
   * Computing Set Intersection
   */
  def intersection(a: GenSet[Int], b: GenSet[Int]): Set[Int] = {
    val result = mutable.Set[Int]()
    for (x <- a) if (b contains x) result += x
    result
  }
  
  /**
   * Synchronizing Side-Effects
   * This would be correct
   */
  def intersectionConcurrent(a: GenSet[Int], b: GenSet[Int]) = {
    val result = new ConcurrentSkipListSet[Int]()
    for (x <- a) if (b contains x) result.add(x)
    result
  }  
  
  /**
   * Avoiding Side Effects
   * Side-effects can be avoided by using the correct combinators.
   * For example, we can use filter to compute intersection
   */
  def intersectionC(a: GenSet[Int], b: GenSet[Int]): GenSet[Int] = {
    if(a.size < b.size) a.filter(b(_))
    else b.filter(a(_))
  }
  
	def main(args: Array[String]) {  
	  /**
	   * Parallelizable collections
	   * ParArray[T] - parallel array of objects, counterpart of Array and ArrayBuffer
	   * ParRange - parallel range of integers, counterpart of Range
	   * ParVector[T] - parallel vector, counterpart of Vector
	   * immutable.ParHashSet[T] - counterpart of immutable.HashSet
	   * immutable.ParHashMap[K, V] - counterpart of immutable.HashMap
	   * mutable.ParHashSet[T] - counterpart of mutable.HashSet
	   * mutable.ParHashMap[K, V] - counterpart of mutable.HashMap
	   * ParTrieMap[K, V] - thread-safe parallel map with atomic snaphsots, counterpart of TrieMap
	   * for other collections, par creates the closest parallele collection - e.g. a List is converted to a ParVector
	   */
		val array = (0 until 15253).toArray 
		val largest = largestPalindrome(array)
		println(largest)
		//A sequential collection can be converted into a parallel one by calling par.
		val largestPar = largestPalindrome(array.par)
		println(largestPar)
		println("*************************************")
	  val vector = Vector.fill(10000000)("")
	  val list = vector.toList  		
		val listime = standardConfig measure {
		  list.par //also creates a ParVector[String]
		}
		println(s"list conversion time: $listime ms")
		val vectortime = standardConfig measure {
		  vector.par // creates a ParVector[String]
		}		
		println(s"vector conversion time: $vectortime ms")	
		println(s"difference: ${listime / vectortime}")
		println("*************************************")
		val listmem = memConfig measure {
		  list.par //also creates a ParVector[String]
		}
		println(s"list measure mem: $listmem ms")
		val vectormem = memConfig measure {
		  vector.par // creates a ParVector[String]
		}		
		println(s"vector measure mem: $vectormem ms")	
		println(s"difference: ${listmem / vectormem}")
		println("**************************")
		//This is not correct
		val int = intersection((0 until 1000).toSet, ( 0 until 1000 by 4).toSet)
		println(int)
		val intPar = intersection((0 until 1000).par.toSet, ( 0 until 1000 by 4).par.toSet)
		println(intPar)
		log(s"Sequential result - ${int.size}")
		log(s"Parallel result - ${intPar.size}")
		println("**********CONCURRENT COLLECTIONS************")
		//This would be correct, use concurrent collections
		val intConcurrent = intersectionConcurrent((0 until 1000).toSet, ( 0 until 1000 by 4).toSet)
		println(intConcurrent)
		val intParConcurrent = intersectionConcurrent((0 until 1000).par.toSet, ( 0 until 1000 by 4).par.toSet)
		println(intParConcurrent)		
		log(s"Concurrent Sequential result - ${intConcurrent.size}")
		log(s"Concurrent Parallel result - ${intParConcurrent.size}")		
		println("**********AVOIDING SIDE EFFECTS************")
		val intC = intersectionC((0 until 1000).toSet, ( 0 until 1000 by 4).toSet)
		println(intC)
		val intParC = intersectionC((0 until 1000).par.toSet, ( 0 until 1000 by 4).par.toSet)
		println(intParC)
		log(s"Avoiding Side-Effects Sequential result - ${intC.size}")
		log(s"Avoiding Side-Effects Parallel result - ${intParC.size}")			
		println("**********CONCURRENT MODIFICATIONS DURING TRAVERSALS************")
		/**
		 * Rule: Never modify a parallel collection on which a data-parallel operation is in progress
		 *       Never write to a collection that is concurrently traversed
		 *       Never read from a collection that is concurrently modified
		 * In either case, program non-deterministically prints different results, or crashes.
		 */
		val graph = mutable.Map[Int, Int]() ++= (0 until 100000).map(i => (i, i + 1))
		graph(graph.size - 1) = 0
		for ((k, v) <- graph.par) graph(k) = graph(v)
		val violation = graph.find({case (i, v) => v != (i + 2) % graph.size})
		println(s"violation: $violation")
		println("**********THE TRIEMAP COLLECTION************")
		/**
		 * The TrieMap Collection
		 * TrieMap is an exception to these rules.
		 * The snapshot method can be used to efficiently grab the current state 
		 */
		val graphTrie = concurrent.TrieMap[Int, Int]() ++= (0 until 100000).map(i => (i, i + 1))
		graphTrie(graphTrie.size - 1) = 0
		val previous = graphTrie.snapshot()
		for ((k, v) <- graphTrie.par) graphTrie(k) = previous(v)
		val violationTrie = graphTrie.find({case (i, v) => v != (i + 2) % graphTrie.size})
		println(s"violationTrie: violationTrie")		
	}
}