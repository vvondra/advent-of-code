import scala.sys.process._

def clear() = "clear".!

type Coord = (Int, Int)

sealed trait Square
case object Sand extends Square {
  override def toString: String = "."
}
case object Clay extends Square {
  override def toString: String = "#"
}

sealed trait Water extends Square

case object Settled extends Water {
  override def toString: String = "~"
}
case object Flowing extends Water {
  override def toString: String = "|"
}
case object Settling extends Water {
  override def toString: String = "/"
}

sealed trait Direction
case object Down extends Direction
case object Left extends Direction
case object Right extends Direction

type Grid = Vector[Vector[Square]]

val pattern = "([xy])=(\\d+), ([xy])=(\\d+)..(\\d+)".r

val map = io.Source.stdin.getLines
  .flatMap { case pattern(first, value, _, rangeStart, rangeEnd) =>
    (rangeStart.toInt to rangeEnd.toInt).map(c => if (first == "x") (value.toInt, c) else (c, value.toInt))
  }
  .map(_ -> Clay)
  .toMap
  .withDefaultValue(Sand)

val minY = map.minBy(_._1._2)._1._2
val maxY = map.maxBy(_._1._2)._1._2

def settle(squares: Map[Coord, Square], current: Coord): Map[Coord, Square] = {
  squares(current) match {
    case Settling => settle(settle(squares + (current -> Settled), current.copy(_1 = current._1 - 1)), current.copy(_1 = current._1 + 1))
    case _ => squares
  }
}

def flood(squares: Map[Coord, Square], current: Coord, prev: Coord): Map[Coord, Square] = {
  if (current._2 > maxY) {
    squares
  } else {
    squares(current) match {
      case Clay | _: Water => squares
      case Sand =>
        val below = current.copy(_2 = current._2 + 1)
        val downStream = flood(squares + (current -> Flowing), below, current)
        downStream(below) match {
          case Flowing | Settling | Sand => downStream
          case Clay | Settled =>
            val left = current.copy(_1 = current._1 - 1)
            val right = current.copy(_1 = current._1 + 1)
            val leftStream = flood(downStream, left, current)
            val rightStream = flood(leftStream, right, current)
            (rightStream(left), rightStream(right)) match {
              case (Clay | Settled | Settling, _) | (_, Clay | Settled | Settling) if prev == left || prev == right =>
                rightStream + (current -> Settling)
              case (Clay | Settled | Settling, Clay | Settled | Settling) =>
                settle(settle(rightStream, left), right) + (current -> Settled)
              case _ => rightStream
            }
        }
    }
  }
}

val flooded = flood(map, (500, 0), (500, -1))
println(flooded.count { case (c, square) => c._2 >= minY && square.isInstanceOf[Water] })
println(flooded.count { case (c, square) => c._2 >= minY && square == Settled })