!SLIDE
<div id="lambda">m ⋆ λa. n</div>

# Monads for Real

(featuring an overly-specific look at using monads for working with crashy functions)

<br/>

###### chris lewis λ [JaxFunc](http://www.meetup.com/JaxFunc/) 2011
###### [@chrslws](http://twitter.com/chrslws)
###### [chrsl.ws](http://chrsl.ws/)
###### [github.com/chrislewis](https://github.com/chrislewis)   


!SLIDE

* Scala
* Terminology
    * Laws
    * Type Constructors
    * Polymorphism
* Monads
    * Pure Functional Examples
    * Monads in the Scala Core
* Error Control
    * The "Maybe" Monad ``scala.Option[+A]``
    * Disjoint Unions ``scala.Either[+A, +B]``
    * Monadic Disjoint Unions
* Language Support

!SLIDE
### Scala
    
    class List[A](a: A) {
      def concat(other: List[A]): List[A] = // implementation
      
      def map[B](f: A => B): List[B] = // implementation
    }

!SLIDE

### Laws  
    
From the notes in Java's foundational [Object#equals](http://bit.ly/mhzALl):
    
* It is reflexive: for any non-null reference value x, x.equals(x) should return true.
* It is symmetric: for any non-null reference values x and y, x.equals(y) should return true if and only if y.equals(x) returns true.
* ... et al 

!SLIDE

### Type Constructors
    
    class List<T> {
      // ...
    }
    
Value instances of ``List`` cannot be instantiated because it is just a type constructor.
    
We can construct new types by invoking a type constructor with a type argument.
    
    List<Integer> intList = new List<Integer>();
    List<String> stringList = new List<String>();
    

!SLIDE

### Common Type Constructors
    
    java.util.List<T>
    java.util.Set<T>
    java.util.Map<K, V>
    
    System.Collections.Generic.List(Of T)
    System.Collections.Generic.Dictionary(Of TKey, TValue)
    
You've used these before...
    
    List<String> list = new LinkedList<String>();
    Map<Integer, String> list =
      new HashMap<Integer, String>();

!SLIDE

### Kinds
    
    java.util.List<T>
    // has kind * -> *
    java.util.Map<K, V>
    // has kind (*, *) -> *
    
- Type constructors have a "kind"
- ``* -> *`` Given a type, construct a new type
- ``(*, *) -> *`` Given two types, construct a new type

!SLIDE

### Higher Order Polymorphism
    
    trait Monad<T<_>> {
      // ... won't compile
    }
    
In Java and C# we can abstract over type *parameters*, but we cannot abstract over type *constructors*.
    
This is possible in Scala.

!SLIDE

"A monad is a triple (M,unit,★) consisting of a type constructor M and two operations of the given polymorphic types. These operations must satisfy three laws ..." [1]
    
<cite>[1]: Philip Wadler, Monads for functional programming</cite>

!SLIDE

### A Pure Functional Monad
    
    trait Monad[M[_]] {
      def unit[A](a: A): M[A]
      
      def bind[A, B](ma: M[A], f: A => M[B]): M[B]
    }

!SLIDE

### A List Monad
    
    import scala.collection.mutable.ListBuffer
    
    val ListM = new Monad[List] {
      def unit[A](a: A) = List(a)
      
      def bind[A, B](ma: List[A], f: A => List[B]) = {
        val buf = new ListBuffer[B]()
        for (a <- ma)
          buf.appendAll(f(a))
        buf.toList
      }
    }
    
!SLIDE

Every collection in Scala is a kind of monad because they satisfy the monad laws*.
Consider ``List``:
    
* ``List[A]``
* ``List(1)``
* ``List(1).flatMap(i => List(i.toString))``

<br/>
And ``Set``:  
    
* ``Set[A]``
* ``Set(1)``
* ``Set(1).flatMap(i => Set(i.toString))``

<br/>
<cite>* left identity, right identity, associativity</cite>
!SLIDE

### Scala's Maybe Monad: ``Option[+A]``
    
* ``Some[A]``
* ``Some(1)``
* ``Some(1).flatMap(i => Some(i.toString))``
* ``None``

!SLIDE

### Scala's Maybe Monad: ``Option[+A]``
    
    sealed trait Option[+A] {
      def get: A
      
      def flatMap[B](f: A => Option[B]): Option[B] =
        if (isEmpty) None else f(this.get)
    }
    
    case class Some[+A](a: A) extends Option[A] {
      def get = a
    }
    
    case object Nones extends Option[Nothing] {
      def get = throw new NoSuchElementException("None.get")
    }

!SLIDE
    
    def strToInt(s: String): Option[Int] =
      try {
        Some(s.toInt)
      } catch {
        case _ => None
      }
      
    def asBoolean(i: Int): Option[Boolean] =
      i match {
        case 1 => Some(true)
        case 0 => Some(false)
        case _ => None
      }
    
!SLIDE

    strToInt("0").flatMap(asBoolean)
    // Some(false)
    
    strToInt("1").flatMap(asBoolean)
    // Some(true)
    
    strToInt("2").flatMap(asBoolean)
    // None
    
    strToInt("HI!").flatMap(asBoolean)
    // None

!SLIDE

### What is a Disjoint Union?
    
    sealed trait Either[+A, +B] {
      def fold[X](lf: A => X, rf: B => X)
      // .. the rest ..
    }
    
    class Left[+A, +B](a: A) {
      def fold[X](lf: A => X, rf: B => X) = lf(a)
    }
    
    class Right[+A, +B](b: B) {
      def fold[X](lf: A => X, rf: B => X) = rf(b)
    }
    
!SLIDE

    def strToInt(s: String): Either[String, Int] =
      try {
        Right(s.toInt)
      } catch {
        case _ =>
          Left("'%s' does not represent an Int!".format(s))
      }
      
    def asBoolean(i: Int): Either[String, Boolean] =
      i match {
        case 1 => Right(true)
        case 0 => Right(false)
        case x =>
          Left("Cannot deal with %s!".format(x))
      }
    
!SLIDE
    
### We can ``fold`` an ``Either`` Into a Result:
    
    strToInt("5").fold(_ => -1, x => x * x)
    // Int = 25
    
    strToInt("5hi").fold(_ => -1, x => x * x)
    // Int = -1
    
    strToInt("5")
      .fold(x => Left("Not an Int"), asBoolean)
        .fold(println, println)
        
``strToInt("5")``  .. fails ``asBoolean`` with "Cannot deal with '5'!"
``strToInt("x5")`` .. fails ``strToInt`` with "Not an Int"

!SLIDE
### A Monadic Disjoint Union
    
    sealed trait Result[+L, +R] {
      def fold[X](fl: L => X, ff: R => X): X
      
      def map[LL >: L, B](f: R => B): Result[LL, B]
      
      def flatMap[LL >: L, B](f: R => Result[LL, B]): Result[LL, B]
    }
    
!SLIDE
    
    case class Success[+L, +R](r: R) extends Result[L, R] {
      def fold[X](fl: L => X, fr: R => X) =
        fr(r)
        
      def map[LL >: L, B](f: R => B) =
        Success(f(r))
      
      def flatMap[LL >: L, B](f: R => Result[LL, B]) =
        f(r)
    }
    
!SLIDE
    
    case class Failure[+L, +R](l: L) extends Result[L, R] {
      def fold[X](fl: L => X, fr: R => X) =
        fl(l)
        
      def map[LL >: L, B](f: R => B) =
        Failure(l)
      
      def flatMap[LL >: L, B](f: R => Result[LL, B]) =
        Failure(l)
    }
    
!SLIDE

    def strToInt(s: String): Result[String, Int] =
      try {
        Success(s.toInt)
      } catch {
        case _ =>
          Failure("'%s' does not represent an Int!".format(s))
      }
      
    def asBoolean(i: Int): Result[String, Boolean] =
      i match {
        case 1 => Success(true)
        case 0 => Success(false)
        case x =>
          Failure("Cannot deal with %s!".format(x))
      }
    
!SLIDE

    strToInt("0").flatMap(asBoolean)
    // Success(false)
    
    strToInt("1").flatMap(asBoolean)
    // Success(true)
    
    strToInt("5").flatMap(asBoolean)
    // Failure(Cannot deal with 5!)
    
    strToInt("HI!").flatMap(asBoolean)
    // Failure('HI!' does not represent an Int!)

!SLIDE

### Scala's ``for`` Comprehension
    
Note that this:
    
    strToInt("0").flatMap(asBoolean)
    
Is the same as this:
    
    strToInt("0").flatMap(
      i => asBoolean(i).map(b => b)
    )
    
!SLIDE

### Scala's ``for`` Comprehension
    
The compiler translates expressions like this:
    
    for {
      i <- strToInt("0")
      b <- asBoolean(i)
    } yield b
    
Into an expression like this:
    
    strToInt("0").flatMap(
      i => asBoolean(i).map(b => b)
    )
      
!SLIDE
### A Realistic Workflow With Exceptions
    
    val purchase =
      try {
        val u = fetchUser(1)
        val a = fetchAccount(u)
        val p = makePurchase(a, item)
        val t = recordTransaction(a, p)
        p
      } catch {
        case e: UserException         => println(e)
        case e: AccountException      => println(e)
        case e: PurchaseException     => println(e)
        case e: TransactionException  => println(e)
      }
    
!SLIDE
### The Same Workflow
    
    val purchase =
      for {
        u <- fetchUser(1)
        a <- fetchAccount(u)
        p <- makePurchase(a, item)
        t <- recordTransaction(a, p)
      } yield p
    
    purchase.fold(println, println)

!SLIDE
### Worth Reading
    
* John Hughes
  [Why functional programming matters](http://www.cs.utexas.edu/~shmat/courses/cs345/whyfp.pdf)
* Philip Wadler
  [Monads for functional programming](http://homepages.inf.ed.ac.uk/wadler/papers/marktoberdorf/baastad.pdf)

!SLIDE
### Questions?
