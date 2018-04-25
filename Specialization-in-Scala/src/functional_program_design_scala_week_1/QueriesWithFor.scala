package functional_program_design_scala_week_1

object QueriesWithFor {
  
  case class Book(title: String, authors: List[String])
  
  val books: List[Book] = List(
  Book(title = "Structure and Interpretation of Computer Programs", authors = List("Abelson, Harald", "Sussman, Gerald J.")),
  Book(title = "Introduction to Functional Programming", authors = List("Bird, Richard", "Wadler, Phil")),
  Book(title = "Effective Java", authors = List("Bloch, Joshua")),
  Book(title = "Java Puzzlers", authors = List("Bloch, Joshua", "Gafter, Neal")),
  Book(title = "Programming in Scala", authors = List("Odersky, Martin", "Spoon, Lex", "Venners, Bill")))  
  
	def main(args: Array[String]) {
		//To find the titles of books whose author's name is Bird
    val f = for(b <- books; 
        a <- b.authors
        if a.startsWith("Bird,")
        ) yield b.title        
    println(f.mkString(","))  
    
    //To find all the books which have the word "Program" in the title
    val b = for(
        b <- books 
        if b.title.indexOf("Program") >= 0
        ) yield b.title
    println(b.mkString(","))    
    
    //To find the names of all authors who have written at least two books present in the database
    val a = (for {
      b1 <- books
      b2 <- books
      if b1 != b2
      a1 <- b1.authors
      a2 <- b2.authors
      if a1 == a2
    } yield a1).distinct //To avoid repeated names   
    println(a.mkString(","))
    
    //To find the names of all authors who have written at least two books present in the database
    val c = (for {
      b1 <- books
      b2 <- books
      if b1 != b2
      a1 <- b1.authors
      a2 <- b2.authors
      if a1 == a2
    } yield a1).toSet //To avoid repeated names   
    println(c.mkString(","))    
	}
}