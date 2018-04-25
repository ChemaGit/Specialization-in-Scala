package functional_program_design_scala_week_3

object PrinciplesOfReactiveProgramming {
  
  def power (x: Double, exp: Int): Double = {
    var r = 1.0
    var i = exp
    while(i < 0) {
      r = r * x
      i = i - 1
    }
    r
  }
  
  /**
   * The function WHILE can be defined as follows
   * WHILE is tail recursive, so it can operate with a constant stack size.
   */
  /*
  def WHILE(condition: => Boolean)(commnad: => Unit): Unit =
    if(condition) {
      command
      WHILE(condition)(command)
    }else ()
  */
  
  def doWhile(n: Int): Int = {
    var sum = 0
    var i = n
    do {
      sum = sum + n
      i = i - 1
    }while(i > 0)
    sum
  }
  
  /**
   * The function REPEAT can be defined as follows
   * REPEAT is tail recursive, so it can operate with a constant stack size.
   */
  /*def REPEAT(condition: => Boolean)(command: => Unit): Unit = {
    command
    if(condition) ()
    else REPEAT (command)(condition)
  }*/
  
  def REPEAT_UNTIL(condition: => Boolean)(command: => Unit): Unit = {
    
  }
  
  
	def main(args: Array[String]) {
		println(doWhile(10))
		
		for(i <- 1 until 3; j <- "abc") println(i + " " + j)
		
		(1 until 3).foreach(i => "abc".foreach(j => println(i + " " + j)))
	}
}