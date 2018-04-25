package functional_program_design_scala_week_4

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps


object ComposingFutures {
  
  def readFromMemory() = ???
  
  def sendToSafe(packet: Array[Byte]):Future[Array[Byte]] = ???
/*  
  /**
   * retry successfully completing block at most noTimes
   * and give up after that
   */
  def retry(noTimes: Int)(block: => Future[T]): Future[T] = {
    if(noTimes == 0) {
      Future.failed(new Exception("Sorry"))
    } else {
      block.fallbackTo {
        retry(noTimes - 1) {
          block
        }
      }
    }
  }
  
  /**
   * Retry using foldLeft
   */
  def retry(noTimes: Int)(block: => Future[T]) = {
    val ns = (1 to noTimes).toList
    val attempts = ns.map(_ => () => block)
    val failed = Future.failed(new Exception)
    val result = attempts.foldRight(() => failed)((block, a) => ()) => {
      block() fallbackTo {
        a()result ()
      }
    }
  }
  */
	def main(args: Array[String]) {
		/*val socket = Socket()
		val confirmation: Future[Array[Byte]] = for {
		  packet <- socket.readFromMemory()
		  confirmation <- socket.sendToSave(packet)
		} yield confirmation*/
    /**
     * Example foldLeft
     */
    val a = List(5, 6, 7).foldLeft(2)( (x: Int, y: Int) => x * y)
    println(a)
    val b = List(5, 6, 7).foldRight(2)( (x: Int, y: Int) => y.min(x))
    println(b)
	}
	
}