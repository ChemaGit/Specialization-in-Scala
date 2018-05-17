package stackoverflow

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.rdd.RDD
import annotation.tailrec
import scala.reflect.ClassTag

/** A raw stackoverflow posting, either a question or an answer */
case class Posting(postingType: Int, id: Int, acceptedAnswer: Option[Int], parentId: Option[QID], score: Int, tags: Option[String]) extends Serializable


/** The main class */
object StackOverflow extends StackOverflow {

  @transient lazy val conf: SparkConf = new SparkConf().setMaster("local").setAppName("StackOverflow")
  @transient lazy val sc: SparkContext = new SparkContext(conf)  

  /** Main function */
  def main(args: Array[String]): Unit = {
    sc.setLogLevel("ERROR")
    
    val lines = sc.textFile("src/main/resources/stackoverflow/stackoverflow.csv")  
    //println(lines.count())
    //lines.take(5).foreach(println)
    
    val raw = rawPostings(lines)
    //println(raw.count())
    //raw.take(5).foreach(println)
    
    val grouped = timed("Time spent in grouped process: ", groupedPostings(raw))
    //println(grouped.count())
    //grouped.take(1).foreach(println)
    
    val scored = timed("Time spent in scored process: ", scoredPostings(grouped))
    //println(scored.count())
    //scored.take(1).foreach(println)
    
    val vectors = timed("Time spent in vectors process: ", vectorPostings(scored))    
    //println(vectors.count())
    //vectors.take(5).foreach(println)    
    
    //assert(vectors.count() == 2121822, "Incorrect number of vectors: " + vectors.count())
    //vectors.persist()
    val means = timed("Time spent in k-means process: ", kmeans(sampleVectors(vectors), vectors, debug = true))    
   
    val results = timed("Time spent to show results: ", clusterResults(means, vectors))
    printResults(results)
    println(timing)
    sc.stop()
  }
  val timing = new StringBuffer
  def timed[T](label: String, code: => T): T = {
    val start = System.currentTimeMillis()
    val result = code
    val stop = System.currentTimeMillis()
    timing.append(s"Processing $label took ${stop - start} ms.\n")
    result
  }  
}


/** The parsing and kmeans methods */
class StackOverflow extends Serializable {

  /** Languages */
  val langs =
    List(
      "JavaScript", "Java", "PHP", "Python", "C#", "C++", "Ruby", "CSS",
      "Objective-C", "Perl", "Scala", "Haskell", "MATLAB", "Clojure", "Groovy")

  /** K-means parameter: How "far apart" languages should be for the kmeans algorithm? */
  def langSpread = 50000
  assert(langSpread > 0, "If langSpread is zero we can't recover the language from the input data!")

  /** K-means parameter: Number of clusters */
  def kmeansKernels = 45

  /** K-means parameter: Convergence criteria */
  def kmeansEta: Double = 20.0D

  /** K-means parameter: Maximum iterations */
  def kmeansMaxIterations = 120


  //
  //
  // Parsing utilities:
  //
  //

  /** Load postings from the given file */
  def rawPostings(lines: RDD[String]): RDD[Posting] =
    lines.map(line => {
      val arr = line.split(",")
      Posting(postingType =    arr(0).toInt,
              id =             arr(1).toInt,
              acceptedAnswer = if (arr(2) == "") None else Some(arr(2).toInt),
              parentId =       if (arr(3) == "") None else Some(arr(3).toInt),
              score =          arr(4).toInt,
              tags =           if (arr.length >= 6) Some(arr(5).intern()) else None)
    })


  /** Group the questions and answers together */
  def groupedPostings(postings: RDD[Posting]): RDD[(QID, Iterable[(Question, Answer)])] = {
    val questions = postings.filter(p => p.postingType == 1).map(f => (f.id, f))
    val answers = postings.filter(p => p.postingType == 2 && !p.parentId.isEmpty).map(f => (f.parentId.get, f))
    questions.join(answers).groupByKey()    
  }


  /** Compute the maximum score for each posting */
  def scoredPostings(grouped: RDD[(QID, Iterable[(Question, Answer)])]): RDD[(Question, HighScore)] = {

    def answerHighScore(as: Array[Answer]): HighScore = {
      var highScore = 0
          var i = 0
          while (i < as.length) {
            val score = as(i).score
                if (score > highScore)
                  highScore = score
                  i += 1
          }
      highScore
    }
    grouped.map{case(id, it) => (it.head._1, it.map(f => f._2))}.map{case(q, l) => (q, answerHighScore(l.toArray))}    
  }


  /** Compute the vectors for the kmeans */
  def vectorPostings(scored: RDD[(Question, HighScore)]): RDD[(LangIndex, HighScore)] = {
    /** Return optional index of first language that occurs in `tags`. */
    def firstLangInTag(tag: Option[String], ls: List[String]): Option[Int] = {
      if (tag.isEmpty) None
      else if (ls.isEmpty) None
      else if (tag.get == ls.head) Some(0) // index: 0
      else {
        val tmp = firstLangInTag(tag, ls.tail)
        tmp match {
          case None => None
          case Some(i) => Some(i + 1) // index i in ls.tail => index i+1
        }
      }
    }
    scored.map{case(q, hs) => (firstLangInTag(q.tags,langs).get * langSpread, hs)}    
  }


  /** Sample the vectors */
  def sampleVectors(vectors: RDD[(LangIndex, HighScore)]): Array[(Int, Int)] = {

    assert(kmeansKernels % langs.length == 0, "kmeansKernels should be a multiple of the number of languages studied.")
    val perLang = kmeansKernels / langs.length

    // http://en.wikipedia.org/wiki/Reservoir_sampling
    def reservoirSampling(lang: Int, iter: Iterator[Int], size: Int): Array[Int] = {
      val res = new Array[Int](size)
      val rnd = new util.Random(lang)

      for (i <- 0 until size) {
        assert(iter.hasNext, s"iterator must have at least $size elements")
        res(i) = iter.next
      }

      var i = size.toLong
      while (iter.hasNext) {
        val elt = iter.next
        val j = math.abs(rnd.nextLong) % i
        if (j < size)
          res(j.toInt) = elt
        i += 1
      }

      res
    }

    val res =
      if (langSpread < 500)
        // sample the space regardless of the language
        vectors.takeSample(false, kmeansKernels, 42)
      else
        // sample the space uniformly from each language partition
        vectors.groupByKey.flatMap({
          case (lang, vectors) => reservoirSampling(lang, vectors.toIterator, perLang).map((lang, _))
        }).collect()

    assert(res.length == kmeansKernels, res.length)    
    res
  }


  //
  //
  //  Kmeans method:
  //
  //

  /** Main kmeans computation 
   * 1. Pick k points called means. This is called initialization.
	 * 2. Associate each input point with the mean that is closest to it.
	 *    We obtain k clusters of points, and we refer to this process as
	 *    classifying the points.
	 * 3. Update each mean to have the average value of the corresponding cluster.
	 *    If the k means have significantly changed, go back to step 2.
	 *    If they did not, we say that the algorithm converged.
	 *    The k means represent different clusters -- every point is in the cluster
	 *    corresponding to the closest mean.
   */
  @tailrec final def kmeans(means: Array[(Int, Int)], vectors: RDD[(Int, Int)], iter: Int = 1, debug: Boolean = false): Array[(Int, Int)] = {    
    //val newMeans = means.clone() // you need to compute newMeans
    //Associate each input point with the mean that is closest to it.
    //We obtain k clusters of points, and we refer to this process as
    //classifying the points.
    val aux = vectors.map(f => (findClosest(f, means), f)).groupByKey().map({case(idx, iter) => (idx,averageVectors(iter))}).collect().sortBy(f => f._1)
    
    //means.foreach(println)
    
    //aux.foreach(println)

    // TODO: Fill in the newMeans array
    var c = 0
    val newMeans = (for{i <- 0 until means.length
                 e = aux(c)
                 elem = if(i == e._1){
                   c += 1
                   e._2
                 } else means(i)      
               }yield elem).toArray
    //newMeans.foreach(println)           
    
    val distance = euclideanDistance(means, newMeans) //temporal
    //val distance = euclideanDistance(means, newMeans)

    if (debug) {
      println(s"""Iteration: $iter
                 |  * current distance: $distance
                 |  * desired distance: $kmeansEta
                 |  * means:""".stripMargin)
      for (idx <- 0 until kmeansKernels)
      println(f"   ${means(idx).toString}%20s ==> ${newMeans(idx).toString}%20s  " +
              f"  distance: ${euclideanDistance(means(idx), newMeans(idx))}%8.0f")
    }

    if (converged(distance))
      newMeans
    else if (iter < kmeansMaxIterations)
      kmeans(newMeans, vectors, iter + 1, debug)
    else {
      if (debug) {
        println("Reached max iterations!")
      }
      newMeans
    }
  }




  //
  //
  //  Kmeans utilities:
  //
  //

  /** Decide whether the kmeans clustering converged */
  def converged(distance: Double) =
    distance < kmeansEta


  /** Return the euclidean distance between two points */
  def euclideanDistance(v1: (Int, Int), v2: (Int, Int)): Double = {
    val part1 = (v1._1 - v2._1).toDouble * (v1._1 - v2._1)
    val part2 = (v1._2 - v2._2).toDouble * (v1._2 - v2._2)
    part1 + part2
  }

  /** Return the euclidean distance between two points */
  def euclideanDistance(a1: Array[(Int, Int)], a2: Array[(Int, Int)]): Double = {
    //println("means: " + a1.length + "---newMeans: " + a2.length)
    assert(a1.length == a2.length)
    var sum = 0d
    var idx = 0
    while(idx < a1.length) {
      sum += euclideanDistance(a1(idx), a2(idx))
      idx += 1
    }
    sum
  }

  /** Return the closest point */
  def findClosest(p: (Int, Int), centers: Array[(Int, Int)]): Int = {
    var bestIndex = 0
    var closest = Double.PositiveInfinity
    for (i <- 0 until centers.length) {
      val tempDist = euclideanDistance(p, centers(i))
      if (tempDist < closest) {
        closest = tempDist
        bestIndex = i
      }
    }
    bestIndex
  }


  /** Average the vectors */
  def averageVectors(ps: Iterable[(Int, Int)]): (Int, Int) = {
    val iter = ps.iterator
    var count = 0
    var comp1: Long = 0
    var comp2: Long = 0
    while (iter.hasNext) {
      val item = iter.next
      comp1 += item._1
      comp2 += item._2
      count += 1
    }
    ((comp1 / count).toInt, (comp2 / count).toInt)
  }




  //
  //
  //  Displaying results:
  //
  //
  def clusterResults(means: Array[(Int, Int)], vectors: RDD[(LangIndex, HighScore)]): Array[(String, Double, Int, Int)] = {
    val closest = vectors.map(p => (findClosest(p, means), p))
    val closestGrouped = closest.groupByKey()   
    val median = closestGrouped.mapValues { vs =>
      val listTam = vs.size 
      val idxLang = vs.groupBy(f => f._1).map({case(i, iter) => (i, iter.size)}).maxBy(f => f._2)
      val filTam = vs.filter({case(idx, hig) => idx == idxLang._1 }).size
      val tamMaxLang = vs.filter({case(idx, hig) => idx == idxLang._1 }).size
      val lstMLang = vs.filter({case(idx, hig) => idx == idxLang._1 }).toList.sortBy(f => f._2)
      
      val langLabel: String   = langs(idxLang._1 / langSpread) // most common language in the cluster
      val langPercent: Double = if(listTam == 0) 0 else (filTam / (listTam / 100.0)).round // percent of the questions in the most common language
      val clusterSize: Int    = listTam
      val medianScore: Int    = if(tamMaxLang % 2 != 0) lstMLang(tamMaxLang/2)._2 else (lstMLang(tamMaxLang/2)._2 + lstMLang( (tamMaxLang/2) - 1)._2) / 2

      (langLabel, langPercent, clusterSize, medianScore)
    }

    median.collect().map(_._2).sortBy(_._4)
  }

  def printResults(results: Array[(String, Double, Int, Int)]): Unit = {
    println("Resulting clusters:")
    println("  Score  Dominant language (%percent)  Questions")
    println("================================================")
    for ((lang, percent, size, score) <- results)
      println(f"${score}%7d  ${lang}%-17s (${percent}%-5.1f%%)      ${size}%7d")
  }
}
