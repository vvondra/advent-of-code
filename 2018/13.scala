import scala.annotation.tailrec
import scala.collection.immutable.Queue

type Grid = List[List[Char]]

val Up = 0
val Left = 1
val Down = 2
val Right = 3

val Directions = Map(
  '^' -> Up,
  '<' -> Left,
  '>' -> Right,
  'v' -> Down
)

case class Cart(x: Int, y: Int, orientation: Int, turnState: Int) extends Ordered[Cart] {
  import scala.math.Ordered.orderingToOrdered // so I can compare tuples on the line below

  override def compare(that: Cart): Int = (x, y) compare (that.x, that.y)

  def next(cells: Grid): Cart = {
    val dest = orientation match {
      case Up => (x - 1, y)
      case Left => (x, y - 1)
      case Down => (x + 1, y)
      case Right => (x, y + 1)
      case _ => (x, y)
    }

    val next = cells(dest._1)(dest._2) match {
      // Careful that Scala % won't handle negative numbers
      case '+' => Cart(dest._1, dest._2, Math.floorMod(orientation + turnState, 4), nextTurnState)
      case '/' => Cart(
        dest._1,
        dest._2,
        orientation match {
          case Up => Right
          case Left => Down
          case Down => Left
          case Right => Up
        },
        turnState
      )
      case '\\' => Cart(
        dest._1,
        dest._2,
        orientation match {
          case Up => Left
          case Left => Up
          case Down => Right
          case Right => Down
        },
        turnState
      )
      case _ => Cart(dest._1, dest._2, orientation, turnState)
    }

    next
  }

  def nextTurnState = turnState match {
    case 1 => 0
    case 0 => -1
    case -1 => 1
  }
}

def detectCrash(carts: Seq[Cart]): Option[(Int, Int)] = {
  carts
    .groupBy(c => (c.y, c.x))
    .filter(c => c._2.length > 1)
    .keys
    .headOption
}

val cells = io.Source.stdin.getLines.map(line => line.toList).toList

val carts =
  for {
    i <- cells.indices
    j <- cells(i).indices
    if Vector('>', '<', 'v', '^').contains(cells(i)(j))
  } yield Cart(i, j, Directions(cells(i)(j)), 1)


def moveUntilCrash(carts: Seq[Cart]): (Int, Int) = {
  @tailrec
  def tick(movedCarts: Seq[Cart]): Seq[Cart] = {
    val cartsAfterTick = movedCarts.zipWithIndex.foldLeft((movedCarts, false)) {
      case ((cartsInTick, crashed), (cart, index)) =>
        if (crashed) {
          // In case a crash happened this tick, we don't move any other cart
          (cartsInTick, true)
        } else {
          val updatedCart = cartsInTick.updated(index, cart.next(cells))
          (updatedCart, detectCrash(updatedCart).isDefined)
        }
    }._1

    if (detectCrash(cartsAfterTick).isDefined) {
      cartsAfterTick
    } else {
      tick(cartsAfterTick.sorted)
    }
  }

  detectCrash(tick(carts.sorted)).get
}

println(moveUntilCrash(carts))

def lastCartAfterCrashes(carts: Seq[Cart]): (Int, Int) = {
  @tailrec
  def tick(movedCarts: Seq[Cart]): Seq[Cart] = {

    // Another level of recursion is used to process the list of carts to tick
    // The approach from Task A can't be used since the list of carts changes
    // during the tick evaluation
    @tailrec
    def tickCart(cartsToTick: Queue[Cart], tickedCarts: Seq[Cart]): Seq[Cart] = {
      if (cartsToTick.isEmpty) {
        // We've processed all carts in this tick
        tickedCarts
      } else {
        val (head, tail) = cartsToTick.dequeue
        val movedCart = head.next(cells)
        val newTickedCarts = movedCart +: tickedCarts
        val crash = detectCrash(tail ++ newTickedCarts)
        if (crash.isDefined) {
          // Remove the crashes from ticked and to tick
          val filter = (c: Cart) => c.x == movedCart.x && c.y == movedCart.y
          tickCart(tail.filterNot(filter), newTickedCarts.filterNot(filter))
        } else {
          tickCart(tail, newTickedCarts)
        }
      }
    }

    val cartsAfterTick = tickCart(Queue(movedCarts: _*), Seq.empty)

    // Only one cart left
    if (cartsAfterTick.length == 1) {
      cartsAfterTick
    } else {
      tick(cartsAfterTick.sorted)
    }
  }

  val last = tick(carts.sorted).head
  (last.y, last.x)
}

println(lastCartAfterCrashes(carts))