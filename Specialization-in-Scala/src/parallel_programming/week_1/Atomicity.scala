package parallel_programming.week_1

object Atomicity {
  
  private var uidCount = 0L
  /**
   * Atomicity
   */    
  def getUniqueId(): Long = {
    uidCount = uidCount + 1
    uidCount
  }		
  
  private val x = new AnyRef()
  private var uidCountB = 0L
  def getUniqueIdB(): Long = x.synchronized {
    uidCountB = uidCountB + 1
    uidCountB
  }
  
  /**
   * Composition with the synchronized block
   */
  class Account(private var amount: Int = 0) {
    val uid = getUniqueIdB()
    
    private def lockAndTransfer(target: Account, n: Int) =
      this.synchronized {
        target.synchronized {
          this.amount -= n
          target.amount += n
          println(this.amount)
          println(target.amount)
        }
      }
    
    def transfer(target: Account, n: Int) =
      this.synchronized {
        target.synchronized {
          this.amount -= n
          target.amount += n
        }
      }
    
    def transferB(target: Account, n: Int) =
      if(this.uid < target.uid) this.lockAndTransfer(target, n)
      else target.lockAndTransfer(this, -n)
  }
  
	def main(args: Array[String]) {
	  def startThread() = {
	    val t = new Thread {
	      override def run() {
	        val uids = for(i <- 0 until 10) yield getUniqueId()
	        println(uids)
	      }
	    }
	    t.start()
	    t
	  }
	  
	  def startThreadB() = {
	    val t = new Thread {
	      override def run() {
	        val uids = for(i <- 0 until 10) yield getUniqueIdB()
	        println(uids)
	      }
	    }
	    t.start()
	    t
	  }	  
	  
	  //this execution is not atomic
	  startThread()
	  startThread()
	  //This execution is atomic
	  startThreadB()
	  startThreadB()
	  
	  def startThreadC(a: Account, b: Account, n: Int) = {
	    val t = new Thread {
	      override def run() {
	        for(i <- 0 until n) {
	          a.transfer(b, 1)
	        }
	      }
	    }
	    t.start()
	    t
	  }
	  val a1 = new Account(500000)
	  val a2 = new Account(700000)
	  
	  //The program is blocked
	  //val t1 = startThreadC(a1, a2, 150000)
	  //val s1 = startThreadC(a2, a1, 150000)
	  //The program is blocked
	  //t1.join()
	  //s1.join()
	  
	  def startThreadD(a: Account, b: Account, n: Int) = {
	    val t = new Thread {
	      override def run() {
	        for(i <- 0 until n) {
	          a.transferB(b, 1)
	        }
	      }
	    }
	    t.start()
	    t
	  }	  
	  
	  //The program is not blocked
	  val t1 = startThreadD(a1, a2, 150000)
	  val s1 = startThreadD(a2, a1, 150000)
	  //The program is not blocked
	  t1.join()
	  s1.join()	  
	}
}