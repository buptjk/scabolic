package regolic.sat

import scala.reflect.ClassTag

/*
 * Optimized Vector for storing watching clauses in SAT algorithm
 */
class Vector[A : ClassTag](initialSize: Int = 50) {
  require(initialSize > 0)

  private var underlying: Array[A] = new Array[A](initialSize)
  private var next: Int = 0

  def size = next

  /* Can be called manually for optimal performance */
  def allocate(space: Int) {
    require(space > 0)
    val newSize = underlying.size + space
    val newArray = new Array[A](newSize)
    Array.copy(underlying, 0, newArray, 0, underlying.size)
    underlying = newArray
  }

  def append(el: A) {
    if(next >= underlying.size)
      allocate(underlying.size)
    underlying(next) = el
    next += 1
  }

  def apply(i: Int) = {
    require(i >= 0 && i < next)
    underlying(i)
  }

  def update(i: Int, el: A) {
    require(i >= 0 && i < next)
    underlying(i) = el
  }

  /* Remove n last elements of the vector */
  def shrink(n: Int) {
    require(n >= 0 && n <= size)
    next -= n        
  }

}