package jaxfunc.monads.f

/**
 * An algebraic data type for Maybe. In Haskell:
 *    data Maybe a = Just a | Nothing
 *
 * Algebraic data types are achieved in Scala using the keyword "sealed."
 * This simply means that the trait can only be mixed in to types defined
 * in the same file.
 *
 * This is only for demonstration, as Scala already provides Option[A].
 *
 * Chris Lewis / chris@thegodcode.net
 */ 
sealed trait Maybe[+A]
final case class Just[A](a: A) extends Maybe[A]
case object Nada extends Maybe[Nothing]

/**
 * The Maybe monad defines a Monad instance, providing Maybe as the
 * type constructor.
 */
object MaybeM extends Monad[Maybe] {
  
  def unit[A](a: A) = Just(a)
  
  def bind[A, B](ma: Maybe[A], f: A => Maybe[B]) = ma match {
    case Just(a) => f(a)
    case Nada => Nada
  }
}
