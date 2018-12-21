import scala.annotation.tailrec

sealed trait State
case object Lumberyard extends State
case object Clear extends State
case object Tree extends State

case class Coord(x: Int, y: Int)
case class Cell(coord: Coord, state: State)

def neighboursOfState(candidate: Cell, state: State, cells: Set[Cell]): Set[Cell] =
  cells
    .filter {
      cell =>
        cell != candidate &&
          math.abs(cell.coord.x - candidate.coord.x) <= 1 &&
          math.abs(cell.coord.y - candidate.coord.y) <= 1
    }
    .filter(_.state == state)

def evolve(start: Set[Cell], times: Int = 1): Set[Cell] = {
  @tailrec
  def evolveMemoized(cells: Set[Cell], times: Int, values: Map[Set[Cell], Int]): Set[Cell] =
    if (times == 0) {
      cells
    } else {
      val evolved = cells.map {
        cell =>
          cell.state match {
            case Clear => if (neighboursOfState(cell, Tree, cells).size >= 3) cell.copy(state = Tree) else cell
            case Tree => if (neighboursOfState(cell, Lumberyard, cells).size >= 3) cell.copy(state = Lumberyard) else cell
            case Lumberyard => if (neighboursOfState(cell, Lumberyard, cells).nonEmpty && neighboursOfState(cell, Tree, cells).nonEmpty) {
              cell
            } else cell.copy(state = Clear)
          }
      }

      evolveMemoized(
        evolved,
        values.get(evolved).map(prev => (times % (prev - times)) - 1).getOrElse(times - 1),
        values + (evolved -> times)
      )
    }

  evolveMemoized(start, times, Map.empty)
}

def resourceValue(cells: Set[Cell]): Int = cells.count(_.state == Tree) * cells.count(_.state == Lumberyard)

val states = Map(
  '.' -> Clear,
  '|' -> Tree,
  '#' -> Lumberyard
)

val cells = io.Source.stdin.getLines
  .zipWithIndex
  .flatMap {
    case (line, row) => line.toList.zipWithIndex.map {
      case (char, col) => Cell(Coord(row, col), states(char))
    }
  }
  .toSet

// A
println(resourceValue(evolve(cells, 10)))

// B
println(resourceValue(evolve(cells, 1000 * 1000 * 1000)))
