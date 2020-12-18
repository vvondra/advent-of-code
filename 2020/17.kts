import java.io.File

enum class State(val char: Char) {
  Inactive('.'),
  Active('#');

  companion object {
    private val map = State.values().associateBy { it.char }
    fun fromChar(char: Char) = map[char]
  }
}

typealias Point = List<Int>
data class Cube(val xy: Point, val state: State)

fun initial(dims: Int): Map<Point, Cube> = File("17.input").readLines()
  .withIndex().flatMap { (line, row) ->
    row.withIndex().map { (col, char) -> Cube(listOf(line, col).plus(List(dims - 2, { 0 })), State.fromChar(char)!!) }
  }
  .associate { it.xy to it }

fun vectors(dims: Int): Sequence<Point> {
  val elements = generateSequence { listOf(-1, 0, 1) }.take(dims).toList()

  return elements.fold(listOf(listOf<Int>())) { acc, set ->
    acc.flatMap { list -> set.map { element -> list + element } }
  }.asSequence()
}

fun neighbors(dims: Int, candidate: Cube, cubes: Map<Point, Cube>): Sequence<Cube> {
  return vectors(dims)
    .filterNot { it.all { it == 0 } }
    .map { offset ->
      val point = candidate.xy.zip(offset).map { (a, b) -> a + b }
      cubes.getOrDefault(point, Cube(point, State.Inactive))
    }
}

fun getSequence(start: Map<Point, Cube>, dims: Int): Sequence<Map<Point, Cube>> =
  generateSequence(start) { prev ->
    val expanded = prev.values.fold(prev) { space, cube -> space.plus(neighbors(dims, cube, space).associateBy { it.xy }) }

    expanded
      .mapValues { (_, cube) ->
        when (cube.state) {
          State.Active -> when {
            neighbors(dims, cube, prev).count { it.state == State.Active } in 2..3 -> cube
            else -> cube.copy(state = State.Inactive)
          }
          State.Inactive -> when {
            neighbors(dims, cube, prev).count { it.state == State.Active } == 3 -> cube.copy(state = State.Active)
            else -> cube
          }
        }
      }
  }

getSequence(initial(3), 3).elementAt(6).count { it.value.state == State.Active }.let(::println)
getSequence(initial(4), 4).elementAt(6).count { it.value.state == State.Active }.let(::println)
