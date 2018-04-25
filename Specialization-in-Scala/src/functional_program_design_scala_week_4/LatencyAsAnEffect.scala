package functional_program_design_scala_week_4

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
/*import akka.serializer._

object LatencyAsAnEffect {
  
  trait Future[T] {
    def onComplete(callback: Try[T] => Unit)
    (implicit executor: ExecutionContext): Unit
    
    def onComplete(success: T => Unit, failed: Throwable => Unit): Unit
    
    def onComplete(callback: Observer[T]: Unit
  }
  
  trait Observer[T] {
    def onNext(value: T): Unit
    def onError(error: Throwable): Unit
  }
  
  trait Socket {
    def readFromMemory(): Future[Array[Byte]]
    def sendToEurope(packet: Array[Byte]): Future[Array[Byte]]
  }
  
  trait Adventure {
    def collectCoins(): List[Coin]
    def buyTreasure(coins: List[Coin]:Treasure
  }
  
  /**
   * Creating Futures
   */
  object Future {
    def apply(body: => T)
    (implicit context: ExecutionContext): Future[T]
  }
  
  val memory = Queue[EMaiMessage] (
    EMaiMessage(from = "Erik", to = "Roland"),
    EMaiMessage(from = "Martin", to = "Erik"),
    EMaiMessage(from = "Roland", to = "Martin"))
    
  def readFromMemory(): Future[Array[Byte]] = Future {
    val email = queue.dequeue()
    val serializer = serialization.findSerializerFor(email)
    serializer.toBinary(email)
  }
  
  val adventure = Adventure()
  val coins = adventure.collectCoins()
  val treasure = adventure.buyTreasure(coins)
  
  
	def main(args: Array[String]) {
		/**
		 * Send packets using futures I
		 */
    val socket = Socket()
    val packet: Future[Array[Byte]] = socket.readFromMemory()
    
    val confirmation: Future[Array[Byte]] = packet.onComplete {
      case Success(p) => socket.sendToEurope(p)
      case Failure(t) => ...
    }
    
		/**
		 * Send packets using futures II
		 */
    val socket = Socket()
    val packet: Future[Array[Byte]] = socket.readFromMemory()   
    
    packet.onComplete {
      case Success(p) => {
        val confirmation: Future[Array[Byte]] = socket.sendToEurope(p)
      }
      case Failure(t) => ...
    }
	}
}*/