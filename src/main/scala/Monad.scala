package jaxfunc.monads.f

/**
 * In programming, monads generally provide the following:
 * 
 *  1) A type constructor M[_] that provides a means to obtain a
 *      monadic type M[A].
 *  2) A unit function that "lifts" some underlying value of type A
 *    into a monadic value of type M[A]
 *  3) A binding function which, when given a monad M[A] and a function
 *    A => M[B], will yield an instance of type M[B]
 * 
 * Further, any monad must satisfy the monad laws:
 *
 * "Left identity":  return a >>= f    ≡  f a
 * "Right identity": m >>= return      ≡  m
 * "Associativity":  (m >>= f) >>= g   ≡  m >>= (\x -> f x >>= g)
 *
 * The following Monad trait provides explicit 'unit' and 'bind' funcitons,
 * while the type constructor is provided as some higher-kinded type M
 * when the corresponding Monad instance is defined.
 *
 * See MaybeM.scala and ListM.scala for example implementations.
 *
 * Further reading:
 * http://homepages.inf.ed.ac.uk/wadler/papers/marktoberdorf/baastad.pdf
 * http://www.haskell.org/haskellwiki/Monad_Laws
 * http://en.wikipedia.org/wiki/Kleisli_triple
 *
 * Chris Lewis / chris@thegodcode.net
 */
trait Monad[M[_]] {
  /*
   * The unit operation. Unrelated to Scala's Unit type; isomorphic to
   * Haskell's return function. The unit operation lifts basic values
   * into monadic ones.
   */
  def unit[A](a: A): M[A]
  
  /*
   * The bind operation. Isomorphic to >>= in Haskell. Scala's native
   * equivalent is flatMap, where flatMap is effectively >>= partially
   * applied to the underlying value. 
   */
  def bind[A, B](ma: M[A], f: A => M[B]): M[B]
}
