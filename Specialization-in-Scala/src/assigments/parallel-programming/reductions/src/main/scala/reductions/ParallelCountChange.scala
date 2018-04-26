package reductions

import org.scalameter._
import common._

object ParallelCountChangeRunner {

  @volatile var seqResult = 0

  @volatile var parResult = 0

  val standardConfig = config(
    Key.exec.minWarmupRuns -> 20,
    Key.exec.maxWarmupRuns -> 40,
    Key.exec.benchRuns -> 80,
    Key.verbose -> true
  ) withWarmer(new Warmer.Default)

  def main(args: Array[String]): Unit = {
    val amount = 250
    val coins = List(1, 2, 5, 10, 20, 50)
    val seqtime = standardConfig measure {
      seqResult = ParallelCountChange.countChange(amount, coins)
    }
    println(s"sequential result = $seqResult")
    println(s"sequential count time: $seqtime ms")

    def measureParallelCountChange(threshold: ParallelCountChange.Threshold): Unit = {
      val fjtime = standardConfig measure {
        parResult = ParallelCountChange.parCountChange(amount, coins, threshold)
      }
      println(s"parallel result = $parResult")
      println(s"parallel count time: $fjtime ms")
      println(s"speedup: ${seqtime / fjtime}")
    }

    measureParallelCountChange(ParallelCountChange.moneyThreshold(amount))
    measureParallelCountChange(ParallelCountChange.totalCoinsThreshold(coins.length))
    measureParallelCountChange(ParallelCountChange.combinedThreshold(amount, coins))
  }
}

object ParallelCountChange {

  /** Returns the number of ways change can be made from the specified list of
   *  coins for the specified amount of money.
   */
  def countChange(money: Int, coins: List[Int]): Int = {
    
    def sortCoins(lCoins: List[Int]): List[Int] = {
      def insertCoins(coin: Int, lCoins: List[Int]): List[Int] = {
        if(lCoins.isEmpty || coin > lCoins.head) coin :: lCoins
        else lCoins.head :: insertCoins(coin, lCoins.tail)      
      }        
      if(lCoins.isEmpty) Nil
      else insertCoins(lCoins.head, sortCoins(lCoins.tail))
    }
    
    def removeBigCoins(c: List[Int], m: Int): List[Int] = {
      if(c.isEmpty) c
      else if(c.head > money) removeBigCoins(c.tail, m)
      else c                  
    }
    
    def doChange(m: Int, c: List[Int]) : Int = {
      if(m == 0) 1
      else if(m > 0 && !c.isEmpty) {
        val head = doChange(m - c.head, c) 
        val tail = doChange(m, c.tail)
        tail + head
      } else 0
    }           
    
    val coinList = if(!coins.isEmpty) sortCoins(coins)
                   else coins      
    
    val listCoins = removeBigCoins(coinList, money)
    
    doChange(money, listCoins)
  }

  type Threshold = (Int, List[Int]) => Boolean

  /** In parallel, counts the number of ways change can be made from the
   *  specified list of coins for the specified amount of money.
   */
  def parCountChange(money: Int, coins: List[Int], threshold: Threshold): Int = {
    if(threshold(money, coins) || money < 0 || coins.isEmpty) countChange(money, coins)
    else {
      val (tL, tR) = parallel(parCountChange(money - coins.head, coins, threshold), parCountChange(money, coins.tail, threshold))
      tL + tR
    }
  }

  /** Threshold heuristic based on the starting money. 
   *  The moneyThreshold method, which creates a threshold function that 
   *  returns true when the amount of money is less than or 
   *  equal to 2 / 3 of the starting amount:
   */
  def moneyThreshold(startingMoney: Int): Threshold =
    (money: Int, coins: List[Int]) => (money <= (startingMoney * 2) / 3)

  /** Threshold heuristic based on the total number of initial coins. 
   *   the method totalCoinsThreshold, 
   *   which returns a threshold function that returns true when 
   *   the number of coins is less than or equal to the 2 / 3 
   *   of the initial number of coins
   */
  def totalCoinsThreshold(totalCoins: Int): Threshold =
    (money: Int, coins: List[Int]) => (coins.length <= (totalCoins * 2) / 3)


  /** Threshold heuristic based on the starting money and the initial list of coins.
   *   the method combinedThreshold, which returns a threshold function 
   *   that returns true when the amount of money multiplied with 
   *   the number of remaining coins is less than or equal to the 
   *   starting money multiplied with the initial number of coins divided by 2
   */
  def combinedThreshold(startingMoney: Int, allCoins: List[Int]): Threshold = {
    (money: Int, coins: List[Int]) => ( (money * coins.length) <= (startingMoney * allCoins.length) / 2)
  }
}
