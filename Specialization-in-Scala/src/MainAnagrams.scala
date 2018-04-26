import principles_FP_scala.week_6.`package`.loadDictionary

object MainAnagrams {
  /** A word is simply a `String`. */
  type Word = String

  /** A sentence is a `List` of words. */
  type Sentence = List[Word]

  /** `Occurrences` is a `List` of pairs of characters and positive integers saying
   *  how often the character appears.
   *  This list is sorted alphabetically w.r.t. to the character in each pair.
   *  All characters in the occurrence list are lowercase.
   *
   *  Any list of pairs of lowercase characters and their frequency which is not sorted
   *  is **not** an occurrence list.
   *
   *  Note: If the frequency of some character is zero, then that character should not be
   *  in the list.
   */
  type Occurrences = List[(Char, Int)]

  /** The dictionary is simply a sequence of words.
   *  It is predefined and obtained as a sequence using the utility method `loadDictionary`.
   */
  val dictionary: List[Word] = loadDictionary  
  
  /** Converts the word into its character occurrence list.
   *
   *  Note: the uppercase and lowercase version of the character are treated as the
   *  same character, and are represented as a lowercase character in the occurrence list.
   *
   *  Note: you must use `groupBy` to implement this method!
   */
  def wordOccurrences(w: Word): Occurrences = 
    w.toList.filter(c => c.isLetter)
    .groupBy(c => c.toLower)
    .toList.map( l => (l._1, l._2.length))
    .sorted
    
  /** Converts a sentence into its character occurrence list. */
  def sentenceOccurrences(s: Sentence): Occurrences = 
    wordOccurrences(s.mkString(""))
    
  /** The `dictionaryByOccurrences` is a `Map` from different occurrences to a sequence of all
   *  the words that have that occurrence count.
   *  This map serves as an easy way to obtain all the anagrams of a word given its occurrence list.
   *
   *  For example, the word "eat" has the following character occurrence list:
   *
   *     `List(('a', 1), ('e', 1), ('t', 1))`
   *
   *  Incidentally, so do the words "ate" and "tea".
   *
   *  This means that the `dictionaryByOccurrences` map will contain an entry:
   *
   *    List(('a', 1), ('e', 1), ('t', 1)) -> Seq("ate", "eat", "tea")
   *
   */
  lazy val dictionaryByOccurrences: Map[Occurrences, List[Word]] = {       
    val r = dictionary.map(w => wordOccurrences(w))
    .zip(dictionary)
    .groupBy{case(o, w) => o}
    r.map{case(o, lw) => (o, lw.map{case(oc, s) => s} )}        
  }
  
  /** Returns all the anagrams of a given word. */
  def wordAnagrams(word: Word): List[Word] = {
    val m = dictionaryByOccurrences.map{case(o, lw) => lw}
    val med = m.filter{case(lw) => lw.contains(word)}
    if(med.isEmpty) List()
    else med.head    
  }
  
  /** Returns the list of all subsets of the occurrence list.
   *  This includes the occurrence itself, i.e. `List(('k', 1), ('o', 1))`
   *  is a subset of `List(('k', 1), ('o', 1))`.
   *  It also include the empty subset `List()`.
   *
   *  Example: the subsets of the occurrence list `List(('a', 2), ('b', 2))` are:
   *
   *    List(
   *      List(),
   *      List(('a', 1)),
   *      List(('a', 2)),
   *      List(('b', 1)),
   *      List(('a', 1), ('b', 1)),
   *      List(('a', 2), ('b', 1)),
   *      List(('b', 2)),
   *      List(('a', 1), ('b', 2)),
   *      List(('a', 2), ('b', 2))
   *    )
   *
   *  Note that the order of the occurrence list subsets does not matter -- the subsets
   *  in the example above could have been displayed in some other order.
   */  
  def combinations(occurrences: Occurrences): List[Occurrences] = {
    def filterRep(rep:  List[List[(Char, Int)]]):  List[List[(Char, Int)]] = {
      def filter(elem: (Char, Int), lo: List[(Char, Int)], ac: List[(Char, Int)]): List[(Char, Int)] = {
        lo match  {
          case List() => if(ac.isEmpty) List(elem) else if(elem._1 == ac.head._1) ac else elem :: ac
          case y :: ys => if(elem._1 == y._1)  if(ys.isEmpty || ys.tail.isEmpty) elem :: ac else filter(ys.head, ys.tail, elem :: ac)
                          else filter(y, ys, elem :: ac)   
        }      
      }        
      if(rep.isEmpty) rep
        else
          for {
            l <- rep
            middle = if(l.isEmpty || l.tail.isEmpty) l
                     else filter(l.head, l.tail, List())
          }yield middle.reverse        
    }    
    def flatOccurrences(o: Occurrences): Occurrences = {      
      for{
        l <- o
        n <- 1 to l._2        
      }yield (l._1.toLower, n)           
    }      
    def combine(c: Occurrences, ocs: Occurrences): List[Occurrences] = {
      ocs match {
        case List() => List(c)
        case y :: ys => {
          c.head match {
            case (ch, o) => {
              if(ch == y._1) List(c ::: ys) ::: combine(c, ys) 
              else List(c ::: List(y)) ::: List(c ::: ys) ::: combine(c, ys) ::: combine(c ::: List(y), ys)  
            }
            case _ => List()  
          }
        }
      }
    }    
    def goMan(occ: Occurrences, acum: List[Occurrences]): List[Occurrences] = {
      if(occ.isEmpty) List(List()) ::: acum
      else goMan(occ.tail, acum ::: combine(List(occ.head), occ.tail))
    }        
    val comb = flatOccurrences(occurrences)
    if(occurrences.isEmpty) List(List())
    else filterRep(goMan(comb, List())).distinct
  }  
  
  /** Subtracts occurrence list `y` from occurrence list `x`.
   *
   *  The precondition is that the occurrence list `y` is a subset of
   *  the occurrence list `x` -- any character appearing in `y` must
   *  appear in `x`, and its frequency in `y` must be smaller or equal
   *  than its frequency in `x`.
   *
   *  Note: the resulting value is an occurrence - meaning it is sorted
   *  and has no zero-entries.
   */
  def subtract(x: Occurrences, y: Occurrences): Occurrences = {       
    def loop( t: List[(Char, Int)], yx: List[(Char, Int)], acc: List[(Char, Int)]): List[(Char, Int)] = {
      def innerLoop(he: (Char, Int), ly: List[(Char, Int)]): (Char, Int) = {
        if(ly.isEmpty) he
        else if(he._1 == ly.head._1) (he._1, he._2 - ly.head._2)
        else innerLoop(he, ly.tail)
      }
      if(t.isEmpty) acc.reverse.filter{case(c, o) => o > 0}
      else loop(t.tail, yx, innerLoop(t.head, yx) :: acc)      
    }
    if(y.isEmpty || x.isEmpty) x
    else loop(x, y, List())
  }
  
  /** Returns a list of all anagram sentences of the given sentence.
   *
   *  An anagram of a sentence is formed by taking the occurrences of all the characters of
   *  all the words in the sentence, and producing all possible combinations of words with those characters,
   *  such that the words have to be from the dictionary.
   *
   *  The number of words in the sentence and its anagrams does not have to correspond.
   *  For example, the sentence `List("I", "love", "you")` is an anagram of the sentence `List("You", "olive")`.
   *
   *  Also, two sentences with the same words but in a different order are considered two different anagrams.
   *  For example, sentences `List("You", "olive")` and `List("olive", "you")` are different anagrams of
   *  `List("I", "love", "you")`.
   *
   *  Here is a full example of a sentence `List("Yes", "man")` and its anagrams for our dictionary:
   *
   *    List(
   *      List(en, as, my),
   *      List(en, my, as),
   *      List(man, yes),
   *      List(men, say),
   *      List(as, en, my),
   *      List(as, my, en),
   *      List(sane, my),
   *      List(Sean, my),
   *      List(my, en, as),
   *      List(my, as, en),
   *      List(my, sane),
   *      List(my, Sean),
   *      List(say, men),
   *      List(yes, man)
   *    )
   *
   *  The different sentences do not have to be output in the order shown above - any order is fine as long as
   *  all the anagrams are there. Every returned word has to exist in the dictionary.
   *
   *  Note: in case that the words of the sentence are in the dictionary, then the sentence is the anagram of itself,
   *  so it has to be returned in this list.
   *
   *  Note: There is only one anagram of an empty sentence.
   */
  def sentenceAnagramsB(sentence: Sentence): List[Sentence] = {
    def combineSentence(w: Sentence, lw: Sentence, max: Int): List[Sentence] = {
      List()
    }  
    if(sentence.isEmpty) List(List())
    else {
      val maxLen = sentence.flatten.length
      val occs = sentenceOccurrences(sentence)
      val comb = combinations(occs) //.map(f => dictionaryByOccurrences.get(f)).filter(p => p.nonEmpty).map(s => s.get).flatten 
      
      println(comb.mkString("\n"))
      /*val res = for {
        c <- comb
        if c.length > 0
        lr <- combineSentence(List(c), comb).distinct              
        if subtract(occs, lr).isEmpty
        r = dictionaryByOccurrences.get(lr) match {
              case Some(w) => w
              case None => List()
            }      
      } yield r
      res.distinct */
    }
    List()
  }
  
  
  def sentenceAnagrams(sentence: Sentence): List[Sentence] = {        
    def giveWordByOcc(occ: Occurrences): List[Word] = {
      dictionaryByOccurrences.get(occ) match {
        case Some(w) => w
        case None => List()
      }
    }    
    def loop(occurrences: Occurrences): List[Sentence] = {   
      (for( occ <- combinations(occurrences) if occ.length > 0 ) yield {
        (for( w <- giveWordByOcc(occ) ) yield {
          if( subtract(occurrences, occ).length == 0 ) List(List(w))
          else {          
            (for(a <- loop(subtract(occurrences, occ))) yield {
              w :: a
            })            
          }
        }).flatten
      }).flatten     
    }
    val occs = sentenceOccurrences(sentence)    
    if(sentence.length == 0) List(List())
    else loop(occs)
  }  
  

  
	def main(args: Array[String]) {
	  val p = List(('a', 1))
	  val ex = List(('a',1), ('b',1), ('c',1), ('d',1), ('e',1))
	  //println(combine(p, ex).distinct.mkString("\n"))
    //val s: Sentence = List("Deauxma", "Julia Ann", "Ava Addams", "Darla Crane", "Kendra Lust", "Vicky Vette", "Diane Diamond")
    //val ls = s.mkString("")
    //println(ls)
		//println(dictionary.take(5).mkString(","))
	  //println(wordOccurrences("man").mkString(","))
	  //println(sentenceOccurrences(s))
    //dictionaryByOccurrences
    //wordAnagrams("mierda")	 	 
	  //val example = List(('a',1))
    //val example = List(('a',2),('m',2))
	  //val example = List(('e',1),('s',1),('y',1))
	  //val example = List(('a',2),('e',1),('m',1),('n',1),('s',1),('y',1))	 
	  //val example = List(('e',1), ('i',1), ('l',1), ('n',1), ('r',1))
	  //List((e,1), (l,1), (r,1))
	  //val ele = ('e',1)
	  //val example = List(List(('e',1), ('i',1),('l',1), ('l',2), ('n',1), ('r',1),('u',1), ('u',2), ('x',1), ('z',1)))
	  //println(filterRep(example))
	  //val example = (List(('a', 2), ('b', 2)))
    //println(combinations(example).mkString("\n"))	
	  //combinations(example)
    
    
	  
	  //val x = List((e,1),(s,1),(y,1)) 
    //val y = List((a,1),(m,1),(n,1))     
    //println(subtract(x, y))
	  //println(List("Yes","man", "fuck").flatten.length)
	  //println(combineSentence(List("Yes"), List("Yes","man", "fuck")).distinct.mkString("\n"))
	  println(sentenceAnagramsB(List("Yes","man")).mkString("," + "\n"))
	  //println(sentenceAnagrams(List("pinax", "rulez")).mkString("\n"))	
	  //println(sentenceAnagrams(List("Linux", "rulez")).mkString("\n"))
	}
}