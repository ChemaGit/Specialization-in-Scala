package calculator

object Polynomial {
  def computeDelta(a: Signal[Double], b: Signal[Double],
      c: Signal[Double]): Signal[Double] = {  
    Signal { 
      val aAux = a.apply()
      val bAux = b.apply()
      val cAux = c.apply()        
      (bAux * bAux) - (4 * aAux * cAux) 
    }
  }

  def computeSolutions(a: Signal[Double], b: Signal[Double],
      c: Signal[Double], delta: Signal[Double]): Signal[Set[Double]] = {
    Signal { 
      if(computeDelta(a, b, c).apply() < 0) Set() 
      else if(a.apply() == 0) Set()
      else {
        val aAux = a.apply()
        val bAux = b.apply()
        val cAux = c.apply()        
        val delta = Math.sqrt(computeDelta(a, b, c).apply())
        Set((-bAux + delta) /(2 * aAux), (-bAux - delta) /(2 * aAux))
      }
    }
  }
}
