import scala.collection.mutable

val regex = io.Source.stdin.mkString.trim.toList.drop(1).dropRight(1)

case class Coord(x: Int, y: Int)

class Room(val coord: Coord, val doors: mutable.Set[Room], val distance: Int) {
  def connect(dir: Char, rooms: Map[Coord, Room]): Room = {
    val connected = dir match {
      case 'N' => Coord(coord.x - 1, coord.y)
      case 'S' => Coord(coord.x + 1, coord.y)
      case 'W' => Coord(coord.x, coord.y - 1)
      case 'E' => Coord(coord.x, coord.y + 1)
    }

    val room = rooms.getOrElse(connected, Room(connected, distance + 1))
    doors.add(room)

    room
  }
}
object Room { def apply(coord: Coord, distance: Int): Room = new Room(coord, mutable.Set.empty, distance) }

case class Traversal(rooms: Map[Coord, Room], branches: List[Room], last: Room, maxDistance: Int, farawayRooms: Int)

// Build a graph representation of the base and hold on to the starting vertex
val start = Room(Coord(0, 0), 0)
val result = regex.foldLeft(Traversal(Map(start.coord -> start), List.empty[Room], start, 0, 0)) {
  case (Traversal(rooms, stack, last, max, farawayRooms), char) =>
    char match {
      case '(' => Traversal(rooms, last :: stack, last, max, farawayRooms)
      case ')' => Traversal(rooms, stack.tail, stack.head, max, farawayRooms)
      case '|' => Traversal(rooms, stack, stack.head, max, farawayRooms)
      case c =>
        val newLast = last.connect(c, rooms)
        Traversal(
          rooms + (newLast.coord -> newLast),
          stack,
          newLast,
          Math.max(max, newLast.distance),
          farawayRooms + (if (newLast.distance >= 1000 && !rooms.contains(newLast.coord)) 1 else 0)
        )
    }
}

println(result.maxDistance)
println(result.farawayRooms)