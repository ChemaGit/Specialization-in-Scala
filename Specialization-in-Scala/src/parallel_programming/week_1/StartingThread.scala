package parallel_programming.week_1

object StartingThread {
  
  class HelloThread extends Thread {
    override def run() {
      println("Hello ")
      println("world!")
    }
    
    /**
     * Atomicity
     */
    private var uidCount = 0L
    def getUniqueId(): Long = {
      uidCount = uidCount + 1
      uidCount
    }
  }
  
	def main(args: Array[String]) {
	  //The two threads are executed in parallel
		val t = new HelloThread
		val s = new HelloThread
		
		t.start()
		s.start()
		t.join()
		s.join()
	}
}