import java.io.File

enum class State(val char: Char) {
  Floor('.'),
  Occupied('#'),
  Empty('L');

  companion object {
    private val map = State.values().associateBy { it.char }
    fun fromChar(char: Char) = map[char]
  }
}

data class Point(val x: Int, val y: Int)
data class Seat(val xy: Point, val state: State)

val seats: Map<Point, Seat> = File("11.input").readLines()
  .withIndex().flatMap { (line, row) ->
    row.withIndex().map { (col, char) -> Seat(Point(line, col), State.fromChar(char)!!) }
  }
  .associate { it.xy to it }

typealias SeatSelector = (candidate: Seat, state: State, seats: Map<Point, Seat>) -> Map<Point, Seat>

fun vectors(): Sequence<Point> =
  sequence {
    listOf(-1, 0, 1).forEach { x ->
      listOf(-1, 0, 1).forEach { y ->
        if (!(x == 0 && y == 0)) yield(Point(x, y))
      }
    }
  }

fun visibleNeighbors(candidate: Seat, state: State, seats: Map<Point, Seat>): Map<Point, Seat> {
 return vectors()
   .map { (x, y) ->
     generateSequence(candidate) seq@{ seat ->
       if (seat.state != State.Floor && seat != candidate) return@seq null
       val next = seats[Point(seat.xy.x + x, seat.xy.y + y)]
       if (next == null) null
       else next
     }.last()
   }
   .filterNotNull()
   .filter { seat -> seat != candidate && seat.state == state }
   .associate { it.xy to it }
}

fun neighbors(candidate: Seat, state: State, seats: Map<Point, Seat>): Map<Point, Seat> {
  return vectors()
    .map { (x, y) -> seats[Point(candidate.xy.x + x, candidate.xy.y + y)] }
    .filterNotNull()
    .filter { seat -> seat != candidate && seat.state == state }
    .associate { it.xy to it }
}

fun getSequence(selector: SeatSelector, minToEmpty: Int): Sequence<Map<Point, Seat>> =
  generateSequence(seats) { prev ->
    val next = prev.mapValues { (_, seat) ->
      when (seat.state) {
        State.Occupied -> if (selector(seat, State.Occupied, prev).size >= minToEmpty) seat.copy(state = State.Empty) to true else seat to false
        State.Empty -> if (selector(seat, State.Occupied, prev).size == 0) seat.copy(state = State.Occupied) to true else seat to false
        State.Floor -> seat to false
      }
    }

    if (next.count { it.value.second } == 0) null
    else next.mapValues { it.value.first }
  }

getSequence(::neighbors, 4).last().let { println(it.count { it.value.state == State.Occupied }) }
getSequence(::visibleNeighbors, 5).last().let { println(it.count { it.value.state == State.Occupied }) }
