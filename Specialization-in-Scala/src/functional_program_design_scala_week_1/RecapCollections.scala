package functional_program_design_scala_week_1

object RecapCollections {
  
  abstract class JSON
  case class JSeq (elems: List[JSON]) extends JSON
  case class JObj (bindings: Map[String, JSON]) extends JSON
  case class JNum (num: Double) extends JSON
  case class JStr (str: String) extends JSON
  case class JBool (b: Boolean) extends JSON
  case object JNull extends JSON
  
  val data: List[JSON] = List(JObj(Map("firstName" -> JStr("John"),"lastName" -> JStr("Smith"),"address" -> 
  JObj(Map("streetAddress" -> JStr("21 2nd Street"),"state" -> JStr("NY"),"postalCode" -> JNum(10021))),"phoneNumbers" -> 
  JSeq(List(JObj(Map("type" -> JStr("home"), "number" -> JStr("212 555-1234"))),
  JObj(Map("type" -> JStr("fax"), "number" -> JStr("646 555-4567"))) )) ))  )
  
  def show(json: JSON): String = json match {
    case JSeq(elems) => "[" + (elems.map(f => show(f).mkString) + ", ") + "]"
    case JObj(bindings) => val assocs = bindings map {
                              case (key, value) => "\"" + key + "\": " + show(value)
                            }
                               "{" + (assocs mkString ", ") + "}"
    case JNum(num) => num.toString
    case JStr(str) => '\"' + str + '\"'
    case JBool(b) => b.toString
    case JNull => "null"
  }  
  
  def isPrime(n: Int): Boolean =
    (2 until n).forall(d => n % d != 0) 
    
  def combinatorialSearchC(n: Int) = 
    ((1 until n) flatMap (i => (1 until i) map (j => (i, j)))).filter{case (x,y) => isPrime(x + y)}    
    
  def combinatorialSearch(n: Int) =
    for {
      i <- 1 until n
      j <- 1 until i
      if isPrime(i + j)
    } yield (i, j)
  
	def main(args: Array[String]) {
      
    val r = for {
      JObj(bindings) <- data
      JSeq(phones) = bindings("phoneNumbers")
      JObj(phone) <- phones
      JStr(digits) = phone("number")
      if digits startsWith "212"
    } yield (bindings("firstName"), bindings("lastName"))      
		
	}
}