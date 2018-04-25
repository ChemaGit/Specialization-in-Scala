package functional_program_design_scala_week_4

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps
/*
import scala.imaginary.Http._


object CombinatorsOnFutures {
  object Duration {
    def apply(length: Long, unit: TimeUnit): Duration
  }  
  
  /**
   * All these methods take an implicit execution context 
   */
  trait Awaitable[T] extends AnyRef{
    abstract def ready(atMost: Duration): Unit
    abstract def result(atMost: Duration): T
  }
  
  trait Future[T] extends Awaitable[T] {
    def filter(p: T=>Boolean): Future[T]
    def flatMap[S](f: T=>Future[S]): Future[U]
    def map[S](f: T=>S): Future[S]
    def recoverWith(f: PartialFunction[Throwable, Future[T]]): Future[T]
  }
  
  object Future {
    def apply[T](body :=>T): Future[T] 
  }
 
  def sendTo(url: URL, packet: Array[Byte]):
  Future[Array[Byte]] = Http(url, Request(packet)).filter(response=> response.isOK).map(response=> response.toByteArray)

  def sendToSafe(packet: Array[Byte]):Future[Array[Byte]] =
    sendTo(mailServer.europe, packet) recoverWith{
      case europeError=>
        sendTo(mailServer.usa, packet) recover {
          caseusaError=>usaError.getMessage.toByteArray
        }
    }  
  
  def sendToSafe(packet: Array[Byte]): Future[Array[Byte]] =
    sendTo(mailServer.europe, packet) recoverWith{
    case europeError=>
      sendTo(mailServer.usa, packet) recover {
      case usaError=>usaError.getMessage.toByteArray
    }
  }
  
  def sendToSafe(packet: Array[Byte]):Future[Array[Byte]]=
    sendTo(mailServer.europe, packet) fallbackTo{
      sendTo(mailServer.usa, packet)
    } recover {
      caseeuropeError=>
        europeError.getMessage.toByteArray
  }  
  
  def fallbackTo(that: =>Future[T]): Future[T] = {
    this recoverWith{
      case_ =>that recoverWith { 
        case_ =>this 
      }
    }
  }
  
  
	def main(args: Array[String]) {
    val socket = Socket()
    val packet: Future[Array[Byte]] = socket.readFromMemory()
    val confirmation: Future[Array[Byte]] = packet.flatMap(p => socket.sendToEurope(p))	
    
    /**
     * Asynchronous where possible, blocking where necessary
     */
    val socket = Socket()
    val packet: Future[Array[Byte]] = socket.readFromMemory()
    val confirmation: Future[Array[Byte]] = packet.flatMap(socket.sendToSafe(_))
    val c = Await.result(confirmation, 2 seconds)
    println(c.toText)    
    
    val fiveYears = 1836 minutes
	}
}
*/