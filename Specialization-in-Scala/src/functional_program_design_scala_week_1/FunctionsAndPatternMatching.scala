package functional_program_design_scala_week_1

object FunctionsAndPatternMatching {
  
  abstract class JSON
  case class JSeq (elems: List[JSON]) extends JSON
  case class JObj (bindings: Map[String, JSON]) extends JSON
  case class JNum (num: Double) extends JSON
  case class JStr (str: String) extends JSON
  case class JBool (b: Boolean) extends JSON
  case object JNull extends JSON
  
  val data = JObj(Map("firstName" -> JStr("John"),"lastName" -> JStr("Smith"),"address" -> 
  JObj(Map("streetAddress" -> JStr("21 2nd Street"),"state" -> JStr("NY"),"postalCode" -> JNum(10021))),"phoneNumbers" -> 
  JSeq(List(JObj(Map("type" -> JStr("home"), "number" -> JStr("212 555-1234"))),
  JObj(Map("type" -> JStr("fax"), "number" -> JStr("646 555-4567"))) )) ))  
  
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
  
	def main(args: Array[String]) {
		println(data)
		
		val f: String => String = {case "ping" => "pong"}
		println(f("ping"))
		//println(f("abc"))
		
		val pf: PartialFunction[String, String] = {case "ping" => "pong"}
		
		if(pf.isDefinedAt("ping")) println(pf("ping"))
		else println("Sucker")
		
		if(pf.isDefinedAt("pong")) println(pf("pong"))
		else println("Sucker")
		
	}
}