/**
 * For this part, you will earn credit by completing the TweetSet.scala file. This file defines an abstract class TweetSet 
 * with two concrete subclasses,Empty which represents an empty set, and NonEmpty(elem: Tweet, left: TweetSet, right: TweetSet), 
 * which represents a non-empty set as a binary tree rooted at elem. The tweets are indexed by their text bodies: 
 * the bodies of all tweets on the left are lexicographically smaller than elem and all bodies of elements on the right are lexicographically greater.
 * Note also that these classes are immutable: the set-theoretic operations do not modify this but should return a new set.
 * Before tackling this assignment, we suggest you first study the already implemented methods contains and incl for inspiration.
 */



package principles_FP_scala.week_3

import TweetReader._

/**
 * A class to represent tweets.
 */
class Tweet(val user: String, val text: String, val retweets: Int) {
  override def toString: String =
    "User: " + user + "\n" +
    "Text: " + text + " [" + retweets + "]"
}

/**
 * This represents a set of objects of type `Tweet` in the form of a binary search
 * tree. Every branch in the tree has two children (two `TweetSet`s). There is an
 * invariant which always holds: for every branch `b`, all elements in the left
 * subtree are smaller than the tweet at `b`. The elements in the right subtree are
 * larger.
 *
 * Note that the above structure requires us to be able to compare two tweets (we
 * need to be able to say which of two tweets is larger, or if they are equal). In
 * this implementation, the equality / order of tweets is based on the tweet's text
 * (see `def incl`). Hence, a `TweetSet` could not contain two tweets with the same
 * text from different users.
 *
 *
 * The advantage of representing sets as binary search trees is that the elements
 * of the set can be found quickly. If you want to learn more you can take a look
 * at the Wikipedia page [1], but this is not necessary in order to solve this
 * assignment.
 *
 * [1] http://en.wikipedia.org/wiki/Binary_search_tree
 */
abstract class TweetSet {
  /**
   * 1 Filtering
   * Implement filtering on tweet sets. Complete the stubs for the methods filter and filterAcc. filter takes as argument a function, 
   * the predicate, which takes a tweet and returns a boolean. filter then returns the subset of all the tweets in the original set 
   * for which the predicate is true. For example, the following call:
   * tweets.filter(tweet => tweet.retweets > 10)
   * applied to a set tweets of two tweets, say, where the first tweet was not retweeted and the second tweet was retweeted 20 times 
   * should return a set containing only the second tweet.
   * Hint: start by defining the helper method filterAcc which takes an accumulator set as a second argument. 
   * This accumulator contains the ongoing result of the filtering.
   * This method takes a predicate and returns a subset of all the elements
   * in the original set for which the predicate is true.
   * def filter(p: Tweet => Boolean): TweetSet
   * def filterAcc(p: Tweet => Boolean, acc: TweetSet): TweetSet
   * The definition of filter in terms of filterAcc should then be straightforward.
   */
  
  /**
   * This method takes a predicate and returns a subset of all the elements
   * in the original set for which the predicate is true.
   *
   * Question: Can we implment this method here, or should it remain abstract
   * and be implemented in the subclasses?
   */
  def filter(p: Tweet => Boolean): TweetSet = filterAcc(p, new Empty)
  
  /**
   * This is a helper method for `filter` that propagetes the accumulated tweets.
   */
  def filterAcc(p: Tweet => Boolean, acc: TweetSet): TweetSet

  /**
   * Returns a new `TweetSet` that is the union of `TweetSet`s `this` and `that`.
   *
   * Question: Should we implment this method here, or should it remain abstract
   * and be implemented in the subclasses?
   */
  def union(that: TweetSet): TweetSet 
  
  /**
   * Returns the tweet from this set which has the greatest retweet count.
   *
   * Calling `mostRetweeted` on an empty set should throw an exception of
   * type `java.util.NoSuchElementException`.
   *
   * Question: Should we implment this method here, or should it remain abstract
   * and be implemented in the subclasses?
   */
  def mostRetweeted: Tweet 
  
  /**
   * Returns a list containing all tweets of this set, sorted by retweet count
   * in descending order. In other words, the head of the resulting list should
   * have the highest retweet count.
   *
   * Hint: the method `remove` on TweetSet will be very useful.
   * Question: Should we implment this method here, or should it remain abstract
   * and be implemented in the subclasses?
   */
  def descendingByRetweet: TweetList
  
  /**
   * The following methods are already implemented
   */

  /**
   * Returns a new `TweetSet` which contains all elements of this set, and the
   * the new element `tweet` in case it does not already exist in this set.
   *
   * If `this.contains(tweet)`, the current set is returned.
   */
  def incl(tweet: Tweet): TweetSet

  /**
   * Returns a new `TweetSet` which excludes `tweet`.
   */
  def remove(tweet: Tweet): TweetSet

  /**
   * Tests if `tweet` exists in this `TweetSet`.
   */
  def contains(tweet: Tweet): Boolean

  /**
   * This method takes a function and applies it to every element in the set.
   */
  def foreach(f: Tweet => Unit): Unit
  
  
}

class Empty extends TweetSet {
   def filterAcc(p: Tweet => Boolean, acc: TweetSet): TweetSet = acc
   
   def union(that: TweetSet): TweetSet = that
   
   def mostRetweeted: Tweet = throw new java.util.NoSuchElementException("Empty TweetSet")
   
   def descendingByRetweet: TweetList = Nil
  
  /**
   * The following methods are already implemented
   */

  def contains(tweet: Tweet): Boolean = false

  def incl(tweet: Tweet): TweetSet = new NonEmpty(tweet, new Empty, new Empty)

  def remove(tweet: Tweet): TweetSet = this

  def foreach(f: Tweet => Unit): Unit = ()
    
}

class NonEmpty(elem: Tweet, left: TweetSet, right: TweetSet) extends TweetSet {

    def filterAcc(p: Tweet => Boolean, acc: TweetSet): TweetSet = { 
      var res = Set[Tweet]()
      this.foreach(t => res += t)   
      def go(s: Set[Tweet], e: Tweet, p: Tweet => Boolean, acu: TweetSet): TweetSet = {
        if(s.isEmpty && p(e)) acu.incl(e)
        else if(s.isEmpty) acu
        else {          
          if(p(e)) go(s.tail, s.head, p, acu.incl(e))
          else go(s.tail, s.head, p, acu)
        }
      }      
      go(res.tail, res.head, p, acc)
     }
    
      //((left.union(right)).union(that)).incl(elem)
      //((left union right) union that) incl elem
    def union(that: TweetSet): TweetSet = {
      val acc = new Empty
      var res = Set[Tweet]()  
      that.foreach(t => res += t)
      this.foreach(t => res += t)
      
      def go(s: Set[Tweet],t: Tweet, acu: TweetSet): TweetSet = {
        if(s.isEmpty) acu.incl(t)
        else go(s.tail, s.head, acu.incl(t))
      }
      go(res.tail,res.head, new Empty)
    }
      
    def mostRetweeted: Tweet = {
      var res = Set[Tweet]()
      this.foreach(t => res += t)
      
      def go(s: Set[Tweet], e: Tweet, mr: Tweet): Tweet = {
        val m = if(e.retweets > mr.retweets) e
        else mr
        
        if(s.isEmpty) m      
        else go(s.tail, s.head, m)
      }      
      
      go(res.tail, res.head, res.head)      
    }
    
    
    /**
     * The more often a tweet is “re-tweeted” (that is, repeated by a different user with or without additions), the more influential it is.
		 * The goal of this part of the exercise is to add a method descendingByRetweet to TweetSet which should produce a 
		 * linear sequence of tweets (as an instance of class TweetList), ordered by their number of retweets:
		 * def descendingByRetweet: TweetList
     * This method reflects a common pattern when transforming data structures. While traversing one data structure (in this case, a TweetSet), 
     * we’re building a second data structure (here, an instance of class TweetList). 
     * The idea is to start with the empty list Nil (containing no tweets), and to find the tweet with the most retweets in the input TweetSet. 
     * This tweet is removed from the TweetSet (that is, we obtain a new TweetSet that has all the tweets of the original 
     * set except for the tweet that was “removed”; this immutable set operation, remove, is already implemented for you), 
     * and added to the result list by creating a new Cons. After that, the process repeats itself, 
     * but now we are searching through a TweetSet with one less tweet.
     * Hint: start by implementing the method mostRetweeted which returns the most popular tweet of a TweetSet.
     */
    def descendingByRetweet: TweetList = {
      val lTweet: TweetList = Nil
      
      def reverse(l: TweetList, r: TweetList): TweetList = {
        l match {
          case Nil => r
          case _ => reverse(l.tail, new Cons(l.head, r))
        }
      }
      
      def go(s: TweetSet, accuL: TweetList): TweetList = {
        try {
          val t = s.mostRetweeted
          val acc = new Cons(t, accuL)
          val sr = s.remove(t)
          go(sr, acc)
        } catch {
          case ex: java.util.NoSuchElementException => accuL
        }        
      }
      
      reverse(go(this, lTweet), Nil)
    }
    
    
      
      
  /**
   * The following methods are already implemented
   */

  def contains(x: Tweet): Boolean =
    if (x.text < elem.text) left.contains(x)
    else if (elem.text < x.text) right.contains(x)
    else true

  def incl(x: Tweet): TweetSet = {
    if (x.text < elem.text) new NonEmpty(elem, left.incl(x), right)
    else if (elem.text < x.text) new NonEmpty(elem, left, right.incl(x))
    else this
  }

  def remove(tw: Tweet): TweetSet =
    if (tw.text < elem.text) new NonEmpty(elem, left.remove(tw), right)
    else if (elem.text < tw.text) new NonEmpty(elem, left, right.remove(tw))
    else left.union(right)

  def foreach(f: Tweet => Unit): Unit = {
    f(elem)
    left.foreach(f)
    right.foreach(f)
  }
}

trait TweetList {
  def head: Tweet
  def tail: TweetList
  def isEmpty: Boolean
  def foreach(f: Tweet => Unit): Unit =
    if (!isEmpty) {
      f(head)
      tail.foreach(f)
    }
}

object Nil extends TweetList {
  def head = throw new java.util.NoSuchElementException("head of EmptyList")
  def tail = throw new java.util.NoSuchElementException("tail of EmptyList")
  def isEmpty = true
}

class Cons(val head: Tweet, val tail: TweetList) extends TweetList {
  def isEmpty = false
}


object GoogleVsApple {
  val google = List("android", "Android", "galaxy", "Galaxy", "nexus", "Nexus")
  val apple = List("ios", "iOS", "iphone", "iPhone", "ipad", "iPad")

  lazy val googleTweets: TweetSet = TweetReader.allTweets.filter(tweet => google.exists(andr => tweet.text.contains(andr)))
  lazy val appleTweets: TweetSet = TweetReader.allTweets.filter(tweet => apple.exists(app => tweet.text.contains(app)))
  
  /**
   * A list of all tweets mentioning a keyword from either apple or google,
   * sorted by the number of retweets.
   */
   lazy val trending: TweetList = googleTweets.union(appleTweets).descendingByRetweet
  }

object Main extends App {
    
  val set1 = new Empty  
  val set2 = set1.incl(new Tweet("a", "a body", 20))
  val set3 = set2.incl(new Tweet("b", "b body", 20))
  val c = new Tweet("c", "c body", 7)
  val d = new Tweet("d", "d body", 9)
  val set4c = set3.incl(c)
  val set4d = set3.incl(d)
  val sl = new Tweet("r", "Kendra Lust", 25)
  val set5 = set4c.incl(d).incl(new Tweet("k", "Vicky Vette", 20)).incl(new Tweet("j", "Ava Addams", 30)).incl(sl)  
  
  //set5.foreach(println)  
  //set5.filter(tw => tw.retweets == 20).foreach(println)
  val sUnion = set5.union(set4d)
  sUnion.foreach(println)  
  //println(sUnion.mostRetweeted)
  
  //val l = sUnion.descendingByRetweet
  
  //l.foreach((t: Tweet) => println(t))
  
  // Print the trending tweets
  //GoogleVsApple.trending foreach println
}