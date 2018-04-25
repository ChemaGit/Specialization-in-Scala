package functional_program_design_scala_week_3

object FunctionsAndState {
  
  def iterate(n: Int, f: Int => Int, x: Int): Int =
    if(n == 0) x else iterate(n - 1, f, f(x))
    
  def square(x: Int) = x * x 
  
  /**
   * class with mutable state
   * It is a stateful object
   */
  class BankAccount {
      private var balance = 0
      def deposit(amount: Int): Unit = {
        if(amount > 0) balance = balance + amount
      }
      def withdraw(amount: Int): Int =
        if(0 < amount && amount <= balance) {
          balance = balance - amount
          balance
        } else throw new Error("Insufficent funds")
    }
  
  /**
   * Instead of using a lazy val, we could also implement
   * non-empty streams using a mutable variable
   * This class does not have a mutable state
   */
  /*def cons[T](hd: T, tl: => Stream[T]) = new Stream[T] {
    def head = hd
    private var tlOpt: Option[Stream[T]] = None
    def tail: T = tlOpt match {
      case (Some(x: T)) => x
      case None => tlOpt = Some(tl); tail
    }
  }*/
  
  /**
   * class BankAccountProxy with mutable state
   * It is a stateful object
   */
  class BankAccountProxy(ba: BankAccount) {
    def deposit(amount: Int): Unit = ba.deposit(amount)
    def withdraw(amount: Int): Int = ba.withdraw(amount)
  }
  
	def main(args: Array[String]) {
		println(iterate(2, square, 3))
		val account = new BankAccount
		
		/**
		 * Clearly, accounts are stateful objects.
		 */
		println(account.deposit(50))
		println(account.withdraw(20))
		println(account.withdraw(20))
		println(account.withdraw(15))
	}
}