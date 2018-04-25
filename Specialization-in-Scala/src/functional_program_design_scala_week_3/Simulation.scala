package functional_program_design_scala_week_3

import DiscreteEventSimulation._

object Simulation extends Circuits with Parameters{
  
	def main(args: Array[String]) {   
    val input1, input2, sum, carry = new Wire      
    halfAdder(input1, input2, sum, carry)
    probe("sum", sum)
    probe("carry", carry)      
    input1.setSignal(true)
    run()
    println("sum: " + sum.getSignal + " carry: " + carry.getSignal)        
    input2.setSignal(true)
    println("sum: " + sum.getSignal + " carry: " + carry.getSignal)   
    run()      
    input1.setSignal(false)
    run()
	}
}