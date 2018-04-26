package recfun

object Main {
  def main(args: Array[String]) {
    println("Pascal's Triangle")
    for (row <- 0 to 10) {
      for (col <- 0 to row)
        print(pascal(col, row) + " ")
      println()
    }
  }

  /**
   * Exercise 1
   */
    def pascal(c: Int, r: Int): Int = {     
      
      def fillArray(r: Array[Int], p: Array[Int], ind: Int): Array[Int] = {
        val len = p.length             
        if(ind == 0) {
          r(ind) = 1
          fillArray(r, p, ind + 1)
        } else if (ind < len){
          r(ind) = p(ind - 1) + p(ind)   
          fillArray(r, p, ind + 1)
        } else {
          r(ind) = 1
          r
        }        
      }
      
      def calcRow(row: Int, parc: Array[Int]): Array[Int] = {
        val len = parc.length
        val arr = new Array[Int](len + 1)
        val res = fillArray(arr,parc,0)        
        if(row == len) {
          res
        } else {
          calcRow(row, res)
        }       
      }
                  
      if(r <= 1) 1
      else {
        if(c == 0) 1
        else if(c == r) 1
        else {
          val patron = Array(1,1)
          val arr = calcRow(r, patron)
          arr(c)
        }
      }
    }
  
  /**
   * Exercise 2
   */
    def balance(chars: List[Char]): Boolean = {      
      def doBalance(chAcc: List[Char], chr: List[Char]) : Boolean = {
        if(chr.isEmpty && chAcc.isEmpty) true
        else if(chr.isEmpty && !chAcc.isEmpty) false
        else {        
          val c = chr.head
          val p = c match {
            case '(' => true
            case ')' => true
            case _ =>   false
          }
          val ch = chr.tail
          if(p) {            
            if(chAcc.isEmpty && c == ')') false 
            else if(chAcc.isEmpty && c == '(') doBalance(c::chAcc, ch) 
            else {
              val last = chAcc.last
              if(last == '(' && c == ')') doBalance(chAcc.dropRight(1), ch)
              else doBalance(c::chAcc, ch)                          
            }            
          } else doBalance(chAcc, ch)                    
        }
      }
      
      val acc = List()
      doBalance(acc, chars)
    }
  
  /**
   * Exercise 3
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
  }
