package jaxfunc.monads.f

import org.specs._

class MonadSpec extends Specification {
  
  import ListM._
  def f(i: Int) = unit(i)
  def g(i: Int) = unit(i.toString)
  
  "A monad instance" in {
    "must satisfy" in {
      "left identity" in {
        bind(unit(1), f) must_== f(1) 
      }
      
      "right identity" in {
        bind(unit(1), unit[Int]) must_== unit(1) 
      }
      
      "associativity" in {
        bind(bind(unit(1), f), g) must_== bind(unit(1), (x:Int) => bind(f(x), g))
      }
    }
  }
}
