package scalashop

import org.scalameter._
import common._

object HorizontalBoxBlurRunner {

  val standardConfig = config(
    Key.exec.minWarmupRuns -> 5,
    Key.exec.maxWarmupRuns -> 10,
    Key.exec.benchRuns -> 10,
    Key.verbose -> true
  ) withWarmer(new Warmer.Default)

  def main(args: Array[String]): Unit = {
    val radius = 3
    val width = 1920
    val height = 1080
    val src = new Img(width, height)
    val dst = new Img(width, height)
    val seqtime = standardConfig measure {
      HorizontalBoxBlur.blur(src, dst, 0, height, radius)
    }
    println(s"sequential blur time: $seqtime ms")

    val numTasks = 32
    val partime = standardConfig measure {
      HorizontalBoxBlur.parBlur(src, dst, numTasks, radius)
    }
    println(s"fork/join blur time: $partime ms")
    println(s"speedup: ${seqtime / partime}")
  }
}


/** A simple, trivially parallelizable computation. */
object HorizontalBoxBlur {

  /** Blurs the rows of the source image `src` into the destination image `dst`,
   *  starting with `from` and ending with `end` (non-inclusive).
   *
   *  Within each row, `blur` traverses the pixels by going from left to right.
   */
  def blur(src: Img, dst: Img, from: Int, end: Int, radius: Int): Unit = {
  // TODO implement this method using the `boxBlurKernel` method  
    for (c <- from until end) {
      for (r <- 0 until src.width) {                
        dst(r, c) = boxBlurKernel(src, r, c, radius)
      }
    }
  }

  /** Blurs the rows of the source image in parallel using `numTasks` tasks.
   *
   *  Parallelization is done by stripping the source image `src` into
   *  `numTasks` separate strips, where each strip is composed of some number of
   *  rows.
   */
  def parBlur(src: Img, dst: Img, numTasks: Int, radius: Int): Unit = {
  // TODO implement using the `task` construct and the `blur` method    
	  var listRanges: List[(Int, Int)] = List()
    val height = src.height          
    val step = height / Math.min(numTasks, height)
    var taskN = (Math.min(numTasks, height))
	  var bottom = 0
	  var lastRange = 0
    while(bottom <= height) {
      if(taskN > 1) {
        listRanges = (bottom, bottom + step) :: listRanges
      } 
      if(taskN == 1) {
        lastRange = bottom
        listRanges = (lastRange, height) :: listRanges
      }
      taskN -= 1
      bottom += step
    }   	  
	  val lTask = (for{
	    t <- listRanges 
	    ts = task {
	      blur(src, dst, t._1, t._2, radius)
	    }
	  }yield ts).toList	  
	  lTask.foreach{_.join()}  
  }
}
