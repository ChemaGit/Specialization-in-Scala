package functional_program_design_scala_week_3

object IdentityAndChange {
  
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
   
	def main(args: Array[String]) {
	  //do the two variables have the same meaning?
		val x = new BankAccount
		val y = new BankAccount
		//Obviously not
		println(x.deposit(30))
		println(y.withdraw(20))
		//On the other hand, if we define
		//z and p are the same in this case
		val z = new BankAccount
		val p = z
		p.deposit(50)
		println(z.withdraw(20))
		
		
	}
}