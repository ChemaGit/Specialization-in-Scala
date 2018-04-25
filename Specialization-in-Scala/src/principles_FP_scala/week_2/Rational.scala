package principles_FP_scala.week_2

class Rational(x: Int, y: Int) {
  
  require(y != 0, "Denominator must be nonzero")
  
  private def gcd(a: Int, b: Int) : Int = if(b == 0) a else gcd(b, a % b)
  val g = gcd(x, y)  
  val numer = x / g
  val denom = y / g
  
  /**
   * Auxiliary constructor
   */
  def this(x: Int) = this(x, 1)
  
  def + (that: Rational): Rational = 
    new Rational(numer * that.denom + denom * that.numer, denom * that.denom)
 
  def - (that: Rational): Rational =
    this + (-that)
  
  def * (that: Rational): Rational =
    new Rational(numer * that.numer, denom * that.denom)
  
  def / (that: Rational): Rational = 
    new Rational(this.numer * that.denom, this.denom * that.numer)
  
  def unary_- : Rational = new Rational(-numer, denom)
  
  def equal(that: Rational): Boolean =
    numer * that.denom == denom * that.numer
    
  def < (that: Rational): Boolean =
    numer * that.denom < denom * that.numer
    
  def max(that: Rational): Rational =
    if(this < that) that else this
  
  override def toString = {      
      numer + "/" + denom
    }
}