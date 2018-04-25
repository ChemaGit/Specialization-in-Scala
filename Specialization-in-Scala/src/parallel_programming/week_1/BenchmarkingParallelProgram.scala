package parallel_programming.week_1

import org.scalameter._

object BenchmarkingParallelProgram {
  
	def main(args: Array[String]) {
	  /**
	   * Testing
	   * Testing is different to benchmarking.
	   */
	  val xz = List(1,2,3)
	  assert(xz.reverse == List(3, 2, 1))
	  
	  /**
	   * An old and bad style to measure benchmarking.
	   */
	  val xs = List(1, 2, 3)
	  val startTime = System.nanoTime()
	  xs.reverse
	  println((System.nanoTime() - startTime) / 1000000) 
	  
	  /**
	   * Measuring the running time.
	   */
    val time = measure {
      (0 until 1000000).toArray
    }	
    println(s"Array initalization time: $time ms")
    
    /**
     * Usually, we want to measure steady state program performance.
     * ScalaMeter Warmer objects run the benchmarked code until 
     * detecting steady state.
     */
    val time2 = withWarmer(new Warmer.Default) measure {
      (0 until 1000000).toArray
    }
    println(s"Array initalization time: $time2 ms")
    
    /**
     * Measuring the stable running time.
     * ScalaMeter configuration clause allows specifying 
     * various parameters, such as the minimum and maximum
     * number of warmup runs.
     */
     val time3 = config(
         Key.exec.minWarmupRuns -> 20,
         Key.exec.maxWarmupRuns -> 60,
         Key.verbose -> true
     ) withWarmer(new Warmer.Default) measure {
      (0 until 1000000).toArray
    }
    println(s"Array initalization time: $time3 ms")
    
    /**
     * Measuring the stable running time with verbose output.
     * Finally, ScalaMeter can measure more than just the running time.
     * Measurer.Default - plain running time
     * IgnoringGC - running time without GC pauses
     * OutlierElimination - removes statistical outliers
     * MemoryFootprint - memory footprint of an object
     * GarbageCollectionCycles - total number of GC pauses
     */
    val time4 = withMeasurer(new Measurer.Default) measure {
       (0 until 1000000).toArray
    }
    println(s"Array initalization time: $time4 ms") 
    
    val time5 = withMeasurer(new Measurer.MemoryFootprint) measure {
       (0 until 1000000).toArray
    }
    println(s"Array initalization time: $time5 ms")  
    
    val time6 = withMeasurer(new Measurer.IgnoringGC) measure {
       (0 until 1000000).toArray
    }
    println(s"Array initalization time: $time6 ms")     
	}
}