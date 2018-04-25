package functional_program_design_scala_week_1

object TraslationOfFor {
  
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
  
  case class Book(title: String, authors: List[String])
  
  val books: List[Book] = List(
  Book(title = "Structure and Interpretation of Computer Programs", authors = List("Abelson, Harald", "Sussman, Gerald J.")),
  Book(title = "Introduction to Functional Programming", authors = List("Bird, Richard", "Wadler, Phil")),
  Book(title = "Effective Java", authors = List("Bloch, Joshua")),
  Book(title = "Java Puzzlers", authors = List("Bloch, Joshua", "Gafter, Neal")),
  Book(title = "Programming in Scala", authors = List("Odersky, Martin", "Spoon, Lex", "Venners, Bill")))   
  
  /**
   * A number is prime if the only divisors of n are 1 and n itself  
   */
  def isPrime(n: Int): Boolean =
    (2 until n).forall(d => n % d != 0)  
  
  /**
   * With for expressions.
   * Given a positive integer n, find all the pairs of positive integers(i, j)
   * such that 1 <= j < i < n, and i + j is prime.
   */
  def combinatorialSearch(n: Int) =
    for {
      i <- 1 until n
      j <- 1 until i
      if isPrime(i + j)
    } yield (i, j)
    
  /**
   * Traslation for, for combinatorialSearch  
   */
  def combinatorialSearchT(n: Int) =
    (1 until n).flatMap(i =>
      (1 until i).withFilter(j => isPrime(i + j))
      .map(j => (i, j)))      

  
	def main(args: Array[String]) {
		val f = for (b <- books; a <- b.authors if a.startsWith("Bird"))
		yield b.title
		
		println(f.mkString(","))
		
		val h = books.map(p => p.authors.map(f => if(f.startsWith("Bird"))p else None)).filter(c => c.head != None).flatten.head match {
		  case (x: Book) => x.title
		}
		println(h)
		
		//Another way to do the same above
		val r = books.flatMap(b => if(b.authors.mkString.contains("Bird")) b.title else None).mkString
		println(r)
		
		//Another way that doesn't work
		//books.flatMap(b => b.authors.withFilter(a => a.startsWith("Bird")).map(y => y.title))
		val z = books.flatMap(b => for (a <- b.authors if a.startsWith("Bird"))yield b.title)
		println(z.mkString(","))
		
		val y = books.flatMap(b => for (a <- b.authors.withFilter(p => p.startsWith("Bird"))) yield b.title)
		println(y.mkString(""))
		
	}
}