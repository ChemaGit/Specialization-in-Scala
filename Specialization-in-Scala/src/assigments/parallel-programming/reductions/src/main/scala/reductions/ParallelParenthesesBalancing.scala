package reductions

import scala.annotation._
import org.scalameter._
import common._

object ParallelParenthesesBalancingRunner {
  
  @volatile var seqResult = false

  @volatile var parResult = false

  val standardConfig = config(
    Key.exec.minWarmupRuns -> 40,
    Key.exec.maxWarmupRuns -> 80,
    Key.exec.benchRuns -> 120,
    Key.verbose -> true
  ) withWarmer(new Warmer.Default)

  def main(args: Array[String]): Unit = {    
    val length = 100000000
    val chars = new Array[Char](length)
    val threshold = 10000
    val seqtime = standardConfig measure {
      seqResult = ParallelParenthesesBalancing.balance(chars)
    }
    println(s"sequential result = $seqResult")
    println(s"sequential balancing time: $seqtime ms")

    val fjtime = standardConfig measure {
      parResult = ParallelParenthesesBalancing.parBalance(chars, threshold)
    }
    println(s"parallel result = $parResult")
    println(s"parallel balancing time: $fjtime ms")
    println(s"speedup: ${seqtime / fjtime}")
  }
}

object ParallelParenthesesBalancing {

  /** Returns `true` iff the parentheses in the input `chars` are balanced.
   */
  def balance(chars: Array[Char]): Boolean = {      
    @tailrec
    def loop(f: Int,u: Int, p: Int, l: Int, r: Int): (Int, (Int,Int)) = {
      if(f == u) (p, (l, r))
      else {
        val c = chars(f)
        c match {
          case '(' => {
            loop(f + 1, u, p + 1, l + 1, r)              
          }
          case ')' => {
            if(l > 0) loop(f + 1, u, p - 1, l - 1, r)              
            else loop(f + 1, u, p - 1, l, r + 1)
          }
          case _ => loop(f + 1, u,p , l, r)
        }
      }        
    }   
    loop(0, chars.length, 0, 0, 0) == (0, (0,0))       
  }

  /** Returns `true` iff the parentheses in the input `chars` are balanced.
   */
  def parBalance(chars: Array[Char], threshold: Int): Boolean = {

    def traverse(from: Int, until: Int): (Int,(Int, Int)) = {
      @tailrec
      def loop(f: Int,u: Int, p: Int, l: Int, r: Int): (Int, (Int,Int)) = {
        if(f == u) (p, (l, r))
        else {
          val c = chars(f)
          c match {
            case '(' => {
              loop(f + 1, u, p + 1, l + 1, r)              
            }
            case ')' => {
              if(l > 0) loop(f + 1, u, p - 1, l - 1, r)              
              else loop(f + 1, u, p - 1, l, r + 1)
            }
            case _ => loop(f + 1, u,p , l, r)
          }
        }        
      }         
      loop(from, until, 0, 0, 0)           
    }

    def reduce(from: Int, until: Int): (Int,(Int, Int)) = {
      if(until - from <= threshold) traverse(from, until)
      else {
        val mid = from + (until - from) / 2
        val (pL, pR) = parallel(reduce(from, mid), reduce(mid, until))                     
        if(pL._1 == 0 && pR._1 == 0) {
          (pL._1 + pR._1,(pL._2._1 + pR._2._1 ,pL._2._2 + pR._2._2))
        } else if(pL._1 < 0 && (pL._1 + pR._1) == 0) { 
          (pL._1 + pR._1,( -(pL._2._1 + pR._2._1) ,(pL._2._2 + pR._2._2)) )
        } else {
          (pL._1 + pR._1,(pL._2._1 + pR._2._1 ,pL._2._2 + pR._2._2))
        }               
      }
    }
    if(chars.length <= threshold) {
      balance(chars)
    } else {
      val res = reduce(0, chars.length)        
      (res._1, res._2._1 - res._2._2) == (0, 0)     
    }
  }

  // For those who want more:
  // Prove that your reduction operator is associative!

}
