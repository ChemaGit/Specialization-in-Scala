package principles_FP_scala.week_5

import scala.math.Ordering

object ImplicitParameters {
  
   def msort[T](xs: List[T])(lt: (T,T) => Boolean): List[T] = {
   val n = xs.length / 2
   if(n == 0) xs
   else {
     def merge(xs: List[T], ys: List[T]): List[T] = {
       (xs, ys) match {
         case (List(), ys) => ys
         case (xs, List()) => xs
         case (x :: xs1, y :: ys1) =>
           if(lt(x,y)) x :: merge(xs1, ys)
           else y :: merge(xs, ys1)
      }
     }
      
     val (fst, snd) = xs splitAt n
     merge(msort(fst)(lt), msort(snd)(lt))
   }
 }
   /**
    * Another way more efficient
    */
   def msortB[T](xs: List[T])(ord: Ordering[T]): List[T] = {
   val n = xs.length / 2
   if(n == 0) xs
   else {
     def merge(xs: List[T], ys: List[T]): List[T] = {
       (xs, ys) match {
         case (List(), ys) => ys
         case (xs, List()) => xs
         case (x :: xs1, y :: ys1) =>
           if(ord.lt(x,y)) x :: merge(xs1, ys)
           else y :: merge(xs, ys1)
      }
     }
      
     val (fst, snd) = xs splitAt n
     merge(msortB(fst)(ord), msortB(snd)(ord))
   }
 }   
   
   /**
    * Another way Much Better with implicit parameters
    */   
   def msortC[T](xs: List[T])(implicit ord: Ordering[T]): List[T] = {
   val n = xs.length / 2
   if(n == 0) xs
   else {
     def merge(xs: List[T], ys: List[T]): List[T] = {
       (xs, ys) match {
         case (List(), ys) => ys
         case (xs, List()) => xs
         case (x :: xs1, y :: ys1) =>
           if(ord.lt(x,y)) x :: merge(xs1, ys)
           else y :: merge(xs, ys1)
      }
     }
      
     val (fst, snd) = xs splitAt n
     merge(msortC(fst)(ord), msortC(snd)(ord))
   }
 }      
   
    
	def main(args: Array[String]) {
		val xs = List(-5,6,3,2,7)
		val fruit = List("apple","pear","orange","pineapple")
		
		println(msort(xs)((x: Int, y: Int) => x < y).mkString(","))
		println(msort(fruit)((x: String, y: String) => x.compareTo(y) < 0).mkString(","))
		println("*****************")
		println(msortB(xs)(Ordering.Int).mkString(","))
		println(msortB(fruit)(Ordering.String).mkString(","))
		println("*****************")
		println(msortC(xs).mkString(","))
		println(msortC(fruit).mkString(","))		
		
	}
}