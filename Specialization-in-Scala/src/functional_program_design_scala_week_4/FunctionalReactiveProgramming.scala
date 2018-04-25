package functional_program_design_scala_week_4

import scala.util.DynamicVariable

object FunctionalReactiveProgramming {
  
  def consolidated(accts: List[BankAccount]): Signal[Int] =
    Signal(accts.map(_.balance()).sum)
  
  class BankAccount {
    val balance = Var(0)
    
    def deposit(amount: Int): Unit =
      if(amount > 0) {
        val b = balance()
        balance() = b + amount               
      }
    
    def withdraw(amount: Int): Unit = 
      if(0 < amount && amount <= balance()) {
        val b = balance()
        balance() = b - amount      
      } else throw new Error("insufficient funds")
  }
   
  
	def main(args: Array[String]) {
		val a = new BankAccount
		val b = new BankAccount
		val c = consolidated(List(a, b))
		
		println(c())
		a.deposit(20)
		println(c())
		b.deposit(30)
		println(c())		
		b.deposit(30)
		println(c())
		val xChange = Signal(240.00)
		val inDollar = Signal(c() * xChange())
		println(inDollar())
		b.withdraw(10)
		println(inDollar())
	}
}