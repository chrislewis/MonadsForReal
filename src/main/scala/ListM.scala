package jaxfunc.monads.f

/**
 * A monad instance for Scala's immutable list (scala.collection.immutable.List).
 *
 * List is a type constructor that takes a single type argument (* -> *),
 * so we pass this to the Monad type constructor.
 * 
 * Chris Lewis / chris@thegodcode.net
 */
object ListM extends Monad[List] {
  
  def unit[A](a: A) = List(a)
  
  /*
   * The binding strategy for the list monad is to simply
   * create a local (mutable) list where we'll buffer the results
   * of f. We invoke f on each element of ma (List[A]), append
   * the value stored inside the result of f to the buffer,
   * and finally yield an immutable list of the results.
   * For the sake of familiarity, we treat the for-comprehension
   * as a bland loop.
   */
  def bind[A, B](ma: List[A], f: A => List[B]) = {
    val buf = new scala.collection.mutable.ListBuffer[B]()
    for (a <- ma)
      buf ++= f(a)
    buf.toList
  }
  
}
