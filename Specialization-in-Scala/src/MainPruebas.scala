import principles_FP_scala.week_4.Huffman.Leaf
import principles_FP_scala.week_4.Huffman.CodeTree
import principles_FP_scala.week_4.Huffman.Fork

object MainPruebas {
  
  // Part 1: Basics
  def weight(tree: CodeTree): Int = {
    tree match {
      case Fork(l,r,c,w) => w
      case Leaf(c, we) => we
    }
  }
  
  def chars(tree: CodeTree): List[Char] = {
    tree match {
      case Fork(l,r,c,w) => c
      case Leaf(c, we) => List(c)
    }    
  }
  
  def makeCodeTree(left: CodeTree, right: CodeTree) =
    Fork(left, right, chars(left) ::: chars(right), weight(left) + weight(right))  
  
  /**
   * This function computes for each unique character in the list `chars` the number of
   * times it occurs. For example, the invocation
   *
   *   times(List('a', 'b', 'a'))
   *
   * should return the following (the order of the resulting list is not important):
   *
   *   List(('a', 2), ('b', 1))
   *
   * The type `List[(Char, Int)]` denotes a list of pairs, where each pair consists of a
   * character and an integer. Pairs can be constructed easily using parentheses:
   *
   *   val pair: (Char, Int) = ('c', 1)
   *
   * In order to access the two elements of a pair, you can use the accessors `_1` and `_2`:
   *
   *   val theChar = pair._1
   *   val theInt  = pair._2
   *
   * Another way to deconstruct a pair is using pattern matching:
   *
   *   pair match {
   *     case (theChar, theInt) =>
   *       println("character is: "+ theChar)
   *       println("integer is  : "+ theInt)
   *   }
   */
  def times(chars: List[Char]): List[(Char, Int)] = {    
    def go(head: Char, xs: List[Char], acc: List[(Char, Int)]): List[(Char, Int)] = {      
      def countChar(h: Char, lacc: List[(Char, Int)], bAcu: List[(Char, Int)]): List[(Char, Int)] = {
        if(lacc.isEmpty && bAcu.isEmpty) List((head, 1))
        else if(lacc.isEmpty && !bAcu.isEmpty){
          (h,1) :: bAcu
        } else {
          lacc.head match {
            case(theChar, theInt) =>
              if(theChar == h) ((theChar, theInt + 1) :: bAcu) ::: lacc.tail
              else countChar(h, lacc.tail, (theChar, theInt) :: bAcu)
          }                        
        }
      }      
      xs match {
        case List() => countChar(head, acc, List())
        case _ => go(xs.head, xs.tail, countChar(head, acc, List()))
      }
    }    
    go(chars.head, chars.tail, List())
  }
  
  /**
   * Returns a list of `Leaf` nodes for a given frequency table `freqs`.
   *
   * The returned list should be ordered by ascending weights (i.e. the
   * head of the list should have the smallest weight), where the weight
   * of a leaf is the frequency of the character.
   */
  def makeOrderedLeafList(freqs: List[(Char, Int)]): List[Leaf] = {
    val ordFreq = freqs.sortBy( c => c._2).reverse
    def go(h: (Char, Int),fr: List[(Char, Int)], acc: List[Leaf]): List[Leaf] = {
      if(fr.isEmpty) new Leaf(h._1, h._2) :: acc
      else go(fr.head, fr.tail, new Leaf(h._1, h._2) :: acc)
    }
    go(ordFreq.head, ordFreq.tail, List())
  }
  
  /**
   * Checks whether the list `trees` contains only one single code tree.
   */
  def singleton(trees: List[CodeTree]): Boolean = 
    if(!trees.isEmpty && trees.length == 1) true
    else false
    
  /**
   * The parameter `trees` of this function is a list of code trees ordered
   * by ascending weights.
   *
   * This function takes the first two elements of the list `trees` and combines
   * them into a single `Fork` node. This node is then added back into the
   * remaining elements of `trees` at a position such that the ordering by weights
   * is preserved.
   *
   * If `trees` is a list of less than two elements, that list should be returned
   * unchanged.
   */
  def combine(trees: List[CodeTree]): List[CodeTree] = {
    def go(fork: CodeTree, head: CodeTree, tail: List[CodeTree], acc: List[CodeTree]): List[CodeTree] = {
      val wf = weight(fork)
      val wh = weight(head)
      val insert = if(wf < wh) true else false      
      val acu = if(wf < wh) tail.reverse ::: head :: fork :: acc else head :: acc
      if(insert) acu.reverse
      else if(tail.isEmpty) (fork :: acu).reverse
      else go(fork, tail.head, tail.tail, acu)
    }
      
    if(trees.isEmpty) trees
    else if(singleton(trees)) trees
    else {
      val left = trees.head
      val t1 = trees.tail
      val right = t1.head
      val t2 = t1.tail
      val f = makeCodeTree(left, right)
      if(t2.isEmpty) List(f)
      else go(f, t2.head, t2.tail, List())
    }
  }
  
  /**
   * This function will be called in the following way:
   *
   *   until(singleton, combine)(trees)
   *
   * where `trees` is of type `List[CodeTree]`, `singleton` and `combine` refer to
   * the two functions defined above.
   *
   * In such an invocation, `until` should call the two functions until the list of
   * code trees contains only one single tree, and then return that singleton list.
   *
   * Hint: before writing the implementation,
   *  - start by defining the parameter types such that the above example invocation
   *    is valid. The parameter types of `until` should match the argument types of
   *    the example invocation. Also define the return type of the `until` function.
   *  - try to find sensible parameter names for `xxx`, `yyy` and `zzz`.
   */
  def until(f: List[CodeTree] => Boolean  , c: List[CodeTree] => List[CodeTree] )(tree: List[CodeTree] ): CodeTree = {
    val newTree = c(tree)
    if(f(newTree)) newTree.head
    else {      
      until(f,c)(newTree)
    }
  }  
  
  /**
   * This function creates a code tree which is optimal to encode the text `chars`.
   *
   * The parameter `chars` is an arbitrary text. This function extracts the character
   * frequencies from that text and creates a code tree based on them.
   */
  def createCodeTree(chars: List[Char]): CodeTree = {
    val freqs = times(chars)
    val lCodeTrees = makeOrderedLeafList(freqs)
    until(singleton,combine)(lCodeTrees)
  }
    
  type Bit = Int

  /**
   * This function decodes the bit sequence `bits` using the code tree `tree` and returns
   * the resulting list of characters.
   */
  def decode(tree: CodeTree, bits: List[Bit]): List[Char] = {
    def go(tre: CodeTree, head: Bit, tail: List[Bit], acc: List[Char]): List[Char] = {     
      
      def giveMeChar(ct: CodeTree, t: List[Bit]): (Char, List[Bit]) = {        
        ct match {
          case Fork(l,r,c,w) => {
            if(t.head == 1) giveMeChar(r, t.tail)
            else giveMeChar(l, t.tail)
          }
          case Leaf(c,w) => (c, t)                     
        }                
      } 
      
      val tuple = tre match {
        case Fork(l,r,c,w) => {
          if(head == 1) {
            giveMeChar(r,tail)
          } else {
            giveMeChar(l,tail)
          }
        }
      }
      
      val ch = tuple._1
      val lBits = tuple._2
      if(lBits.isEmpty) (ch :: acc).reverse
      else {
        go(tree, lBits.head, lBits.tail, ch :: acc)
      }      
    }    
    go(tree, bits.head, bits.tail, List())
  }
  
  /**
   * This function encodes `text` using the code tree `tree`
   * into a sequence of bits.
   */
  def encode(tree: CodeTree)(text: List[Char]): List[Bit] = {       
    def go(tre: CodeTree,tail: List[Char], acc: List[Bit]): List[Bit] = {         
      def giveMeBit(ct: CodeTree,h: Char, acu: List[Bit]): List[Bit] = {  
        val left = ct match {
          case Fork(l,r,c,w) => {
            l match {
              case Fork(le, ri, ch, we) => {
                if(ch.contains(h)) (true,"Fork",l)     
                else (false ,"Fork",l)
              }
              case Leaf (ch, we)=> {
                if(ch == h) (true, "Leaf", l) 
                else (false, "Leaf", l)
              }
            }
          }    
        }
        val right = ct match {
          case Fork(l,r,c,w) => {
            r match {
              case Fork(le, ri, ch, we) => {
                if(ch.contains(h)) (true,"Fork",r) 
                else (false,"Fork",r)
              }
              case Leaf (ch, we)=> {
                if(ch == h) (true,"Leaf",r) 
                else (false,"Leaf",r)
              }
            }
          }    
        }        
        if(left._1 && left._2 == "Fork") giveMeBit(left._3, h, 0 :: acu)
        else if (left._1 && left._2 == "Leaf") (0 :: acu).reverse
        else if (right._1 && right._2 == "Fork") giveMeBit(right._3, h, 1 :: acu)
        else (1 :: acu).reverse       
      }
      
      if(tail.isEmpty) acc
      else go(tre, tail.tail, acc ::: giveMeBit(tre, tail.head, List()))     
    }    
    go(tree, text,List())
  }
  
  /**
   * A Huffman coding tree for the French language.
   * Generated from the data given at
   *   http://fr.wikipedia.org/wiki/Fr%C3%A9quence_d%27apparition_des_lettres_en_fran%C3%A7ais
   */
  val frenchCode: CodeTree = Fork(Fork(Fork(Leaf('s',121895),Fork(Leaf('d',56269),Fork(Fork(Fork(Leaf('x',5928),Leaf('j',8351),List('x','j'),14279),Leaf('f',16351),List('x','j','f'),30630),Fork(Fork(Fork(Fork(Leaf('z',2093),Fork(Leaf('k',745),Leaf('w',1747),List('k','w'),2492),List('z','k','w'),4585),Leaf('y',4725),List('z','k','w','y'),9310),Leaf('h',11298),List('z','k','w','y','h'),20608),Leaf('q',20889),List('z','k','w','y','h','q'),41497),List('x','j','f','z','k','w','y','h','q'),72127),List('d','x','j','f','z','k','w','y','h','q'),128396),List('s','d','x','j','f','z','k','w','y','h','q'),250291),Fork(Fork(Leaf('o',82762),Leaf('l',83668),List('o','l'),166430),Fork(Fork(Leaf('m',45521),Leaf('p',46335),List('m','p'),91856),Leaf('u',96785),List('m','p','u'),188641),List('o','l','m','p','u'),355071),List('s','d','x','j','f','z','k','w','y','h','q','o','l','m','p','u'),605362),Fork(Fork(Fork(Leaf('r',100500),Fork(Leaf('c',50003),Fork(Leaf('v',24975),Fork(Leaf('g',13288),Leaf('b',13822),List('g','b'),27110),List('v','g','b'),52085),List('c','v','g','b'),102088),List('r','c','v','g','b'),202588),Fork(Leaf('n',108812),Leaf('t',111103),List('n','t'),219915),List('r','c','v','g','b','n','t'),422503),Fork(Leaf('e',225947),Fork(Leaf('i',115465),Leaf('a',117110),List('i','a'),232575),List('e','i','a'),458522),List('r','c','v','g','b','n','t','e','i','a'),881025),List('s','d','x','j','f','z','k','w','y','h','q','o','l','m','p','u','r','c','v','g','b','n','t','e','i','a'),1486387)

  /**
   * What does the secret message say? Can you decode it?
   * For the decoding use the `frenchCode' Huffman tree defined above.
   */
  val secret: List[Bit] = List(0,0,1,1,1,0,1,0,1,1,1,0,0,1,1,0,1,0,0,1,1,0,1,0,1,1,0,0,1,1,1,1,1,0,1,0,1,1,0,0,0,0,1,0,1,1,1,0,0,1,0,0,1,0,0,0,1,0,0,0,1,0,1)

  /**
   * Write a function that returns the decoded secret
   */
  def decodedSecret: List[Char] = decode(frenchCode,secret)
  
  type CodeTable = List[(Char, List[Bit])]
  
  /**
   * This function returns the bit sequence that represents the character `char` in
   * the code table `table`.
   */
  def codeBits(table: CodeTable)(char: Char): List[Bit] = {
    def go(tail: CodeTable): List[Bit] = {
      val headCh = tail.head._1
      if(char == headCh) tail.head._2
      else go(tail.tail)      
    }    
    go(table)
  }
  
  /**
   * Given a code tree, create a code table which contains, for every character in the
   * code tree, the sequence of bits representing that character.
   *
   * Hint: think of a recursive solution: every sub-tree of the code tree `tree` is itself
   * a valid code tree that can be represented as a code table. Using the code tables of the
   * sub-trees, think of how to build the code table for the entire tree.
   */
  def convert(tree: CodeTree): CodeTable = {
    def go(tr: CodeTree, b: List[Bit]): CodeTable = {
      tr match {
        case Fork(l, r, ch, w) => mergeCodeTables( go(l, b ::: List(0)),go(r, b ::: List(1)))
        case Leaf(ch, w) => List( (ch, b) )         
      }
    }
    go(tree, List()) 
  }
  
  /**
   * This function takes two code tables and merges them into one. Depending on how you
   * use it in the `convert` method above, this merge method might also do some transformations
   * on the two parameter code tables.
   */
  def mergeCodeTables(a: CodeTable, b: CodeTable): CodeTable = a ::: b  
  
  /**
   * This function encodes `text` according to the code tree `tree`.
   *
   * To speed up the encoding process, it first converts the code tree to a code table
   * and then uses it to perform the actual encoding.
   */
  def quickEncode(tree: CodeTree)(text: List[Char]): List[Bit] = {    
    def go(listCh: List[Char], ct: CodeTable): List[Bit] = {
      if(listCh.isEmpty) List()
      else {
        go(listCh.tail,ct) ::: codeBits(ct)(listCh.head).reverse
      }
    }           
    if(text.isEmpty) List()
    else {
      go(text, convert(tree))
    }
  }
  
	def main(args: Array[String]) {	  	  	  	
		//val pairs = p.map((x: Char) => (x, (p.filter( (c: Char) => c == x)).length) )		
		//println(pairs.mkString(","))		  
		//val p = List('c','a','c','b','a','b','c','h','x','h','c','h') 
		val x = List('a','a','a','e','f','a','a','d','a','g','a','h','a','b','b','b','c')
		//println(x.mkString(","))
		
		val res = times(x)		
		//println(res.mkString(","))
		//val lOrd = makeOrderedLeafList(res)
		//println(lOrd.mkString(","))
		//println("*********************************")
	  
		val codeTree = createCodeTree(x)
		//println(List(codeTree).mkString(","))
		
		//caca == 100000100000
		//val secret: List[Bit] = List(1,1,0,0,0,1,0,0,1,0)
		//val listChar = decode(codeTree, secret)
		//println(listChar.mkString(""))
     		
		//val lBits = encode(codeTree)(List('g','a','f','a'))
		//println(lBits.mkString(""))
		//println(decodedSecret.mkString(""))
		//println(convert(codeTree).mkString("-"))
		println(quickEncode(codeTree)(List('g','a','f','a')).reverse.mkString("") ) //1100010010
		                                                                            //1100010010
	  
	}
}