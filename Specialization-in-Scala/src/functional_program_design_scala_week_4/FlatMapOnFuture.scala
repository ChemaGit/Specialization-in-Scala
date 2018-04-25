package functional_program_design_scala_week_4

object FlatMapOnFuture {
  /* 
  trait Future[T] {
    def flatMap[S](f: T => Future[S]): Future[S] = 
      new Future[S] {
        def onComplete(callback: Try[S] => Unit): Unit =
          self onComplete {
            case Success(x) => f(x).onComplete(callback)
            case Failure(e) => callback(Failure(e)) 
          }
      }
  }
  */
	def main(args: Array[String]) {
		
	}
}