/*                     __                                               *\
**     ________ ___   / /  ___     Scala API                            **
**    / __/ __// _ | / /  / _ |    (c) 2003-2011, LAMP/EPFL             **
**  __\ \/ /__/ __ |/ /__/ __ |    http://scala-lang.org/               **
** /____/\___/_/ |_/____/_/ | |                                         **
**                          |/                                          **
\*                                                                      */

package scala.collection
package mutable

import generic._

/** $factoryInfo
 *  @define Coll immutable.TreeSet
 *  @define coll immutable tree set
 */
object TreeSet extends MutableSortedSetFactory[TreeSet] {
  /** The empty set of this type
   */
  def empty[A](implicit ordering: Ordering[A]) = new TreeSet[A]()(ordering)
}

/**
 *  @author Lucien Pereira
 */
class TreeSet[A](base: Option[TreeSet[A]] = None, from: Option[A] = None, until: Option[A] = None)(implicit val ordering: Ordering[A]) extends SortedSet[A] with SortedSetLike[A, TreeSet[A]] {

  private var avl: AVLTree[A] = Leaf

  def resolve(): TreeSet[A] = base.getOrElse(this)

  private def isLeftAcceptable(from: Option[A], ordering: Ordering[A])(a: A): Boolean =
    from.map(x => ordering.gteq(a, x)).getOrElse(true)

  private def isRightAcceptable(until: Option[A], ordering: Ordering[A])(a: A): Boolean =
    until.map(x => ordering.lt(a, x)).getOrElse(true)

  override def stringPrefix = "TreeSet"

  override def empty: TreeSet[A] = TreeSet.empty

  override def rangeImpl(from: Option[A], until: Option[A]): TreeSet[A] = new TreeSet(Some(this), from, until)

  override def -(elem: A): TreeSet[A] = {
    try {
      resolve.avl = AVLTree.remove(elem, resolve.avl, ordering)
    } catch {
      case e: NoSuchElementException => ()
      case a: Any => a.printStackTrace
    }
    assert(2 > math.abs(resolve.avl.balance))
    this
  }

  override def +(elem: A): TreeSet[A] = {
    try {
      resolve().avl = AVLTree.insert(elem, resolve.avl, ordering)
    } catch {
      case e: IllegalArgumentException => ()
      case a: Any => a.printStackTrace
    }
    assert(2 > math.abs(resolve.avl.balance))
    this
  }

  override def contains(elem: A): Boolean = AVLTree.contains(elem, resolve.avl, ordering)

  override def iterator: Iterator[A] = AVLTree.iterator(resolve.avl, isLeftAcceptable(from, ordering), isRightAcceptable(until, ordering))

}
