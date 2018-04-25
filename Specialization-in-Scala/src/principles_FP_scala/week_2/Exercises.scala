package principles_FP_scala.week_2

object Exercises {
  def main(args: Array[String]) {
    println(sum(cube)( 1,6))   
    println(sum(factorial)(1,6))
    println(sum(id)(1,100))
    println("**********************************************")
    println(sumInts(1,10))
    println(sumCubes(1,10))
    println(sumFactorials(1,10))
    println("**********************************************")
    println(product(x => x * x) (3,4))
    
    def fact(n: Int) = product(x => x)(1, n)
    println(fact(5))
    
    def factB(n: Int) = productB(x => x)(1, n)
    println(factB(5))
  }

  /**
   * Factorial de un numero
   */
  def factorial(x: Int): Int = {
    @annotation.tailrec
    def doFactorial(n: Int, acc: Int): Int = {
      if (n <= 0) acc
      else doFactorial(n - 1, n * acc)
    }
    doFactorial(x, 1)
  }
  
  def cube(x: Int): Int = x * x * x
  
  def id(x: Int): Int = x  
  
  /* This functions are redundant, We have to implement High Order Functions
  def sumFactorials(a: Int, b: Int): Int =
    if(a < b) 0  else factorial(a) + sumFactorials(a + 1, b)

  def sumInts(a: Int, b: Int): Int =
    if (a < b) 0 else a + sumInts(a + 1, b)
  
  def sumCubes(a: Int, b: Int): Int =
    if (a < b) 0 else cube(a) + sumCubes(a + 1, b)     
    */

  /**
   * High Order Functions solve the problem 
   * This function can sum whatever f(Int): Int
   * between an interval a -> b
   */
  def sum(f: Int => Int)(a: Int, b: Int): Int = {
    @annotation.tailrec  
    def loop(a: Int, acc: Int): Int = {
      if (a > b) acc
      else loop(a + 1, f(a) + acc)
    }
    loop(a, 0)
  }
  
  def sumB(f: Int => Int)(a: Int, b: Int): Int =
    if(a > b) 0 else f(a) + sum(f)(a + 1, b)
  
  /**
   * Functions Returning Functions
   * Sum is now a function that returning another function
   */
  def sumR(f: Int => Int): (Int, Int) => Int = {       
    def sumF(a: Int, b: Int): Int =
      if(a > b) 0
      else f(a) + sumF(a + 1, b)
    sumF  
  }
  
  //def sumInts = sumR(x => x)
  def sumInts = sumR(id)
  //sumCubes = sumR(x => x * x * x)
  def sumCubes = sumR(cube)
  //def sumFactorials = sumR(x => x)
  def sumFactorials = sumR(factorial)
  
  def product(f: Int => Int)(a: Int, b: Int): Int = {
    @annotation.tailrec
    def doProduct(a: Int, acc: Int): Int = {
      if(a > b) acc
      else doProduct(a + 1, acc *  f(a))
    }
    doProduct(a, 1)
  }
  
  /**
   * Version of MapReduce
   */
  def mapReduce(f: Int => Int, combine: (Int, Int) => Int, zero: Int)(a: Int, b: Int): Int = 
    if(a > b) zero
    else combine(f(a), mapReduce(f, combine, zero)(a + 1, b))
    
  /**
   * Defining product in terms of MapReduce    
   */    
  def productB(f: Int => Int)(a: Int, b: Int): Int = mapReduce(f, (x, y) => x * y, 1)(a, b)  
}