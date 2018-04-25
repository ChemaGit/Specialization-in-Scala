package parallel_programming.week_4

import parallel_programming.week_1._
import scala.collection.parallel.Combiner
import scala.collection._
import scala.collection.mutable._
import scala.reflect._
import org.scalameter._

object ParallelTwoPhaseConstruction {

}

class ArrayCombiner[T <: AnyRef: ClassTag](val parallelism: Int)extends Combiner[T, Array[T]] {
  private var numElems = 0
  private val buffers = new ArrayBuffer[ArrayBuffer[T]]
  buffers += new ArrayBuffer[T]  
  
  def +=(x: T) = {
    buffers.last += x
    numElems += 1
    this
  }    
  
  def combine[N <: T, That>: Array[T]](that: Combiner[N, That]) = {
    (that: @unchecked) match {
      case that: ArrayCombiner[T] =>
        buffers ++= that.buffers
        numElems += that.numElems
        this
    }
  }    
  
  def size = numElems
  
  def clear() = buffers.clear()
  
  private def copyTo(array: Array[T], from: Int, end: Int): Unit = {
    var i = from
    var j = 0
    while(i >= buffers(j).length) {
      i -= buffers(j).length
      j += 1
    }
    var k = from
    while(k < end) {
      array(k) = buffers(j)(i)
      i += 1
      if(i >= buffers(j).length) {
        i = 0
        j += 1
      }
      k += 1
    }
  }
  
  def result: Array[T] = {
    val step = math.max(1, numElems / parallelism)
    val array = new Array[T](numElems)      
    val starts = (0 until numElems by step) :+ numElems
    val chunks = starts.zip(starts.tail)
    val tasks = for ((from, end) <- chunks) yield task {
      copyTo(array, from, end)
    }
    tasks.foreach(_.join())
    array
  }    
}

object ArrayCombiner extends App{
  val standardConfig = config (
      Key.exec.minWarmupRuns -> 20,
      Key.exec.maxWarmupRuns -> 40,
      Key.exec.benchRuns -> 60,
      Key.verbose -> true
  ) withWarmer(new Warmer.Default)
  
  val memConfig = config (
      Key.exec.minWarmupRuns -> 0,
      Key.exec.maxWarmupRuns -> 0,
      Key.exec.benchRuns -> 10,
      Key.verbose -> true
  ) withWarmer(Warmer.Zero)
  
  val size = 1000000
  
  def run(p: Int) {
    val taskSupport = new collection.parallel.ForkJoinTaskSupport(
        new scala.concurrent.forkjoin.ForkJoinPool(p)
    )
    val strings = (0 until size).map(_.toString)
    val time = standardConfig measure {
      val parallelized = strings.par
      parallelized.tasksupport = taskSupport
      def newCombiner = new ArrayCombiner(p): Combiner[String, Array[String]]
      parallelized.aggregate(newCombiner)(_ += _, _ combine _).result
    }
    println(s"p = $p, time = $time ms")
  }
  run(1)
  run(2)
  run(4)
  run(8)
}