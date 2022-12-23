import java.io.File
import java.util.*

val initial = File("input/23.in")
    .readLines()
    .flatMapIndexed { row, line ->
        line.mapIndexedNotNull { col, char -> if (char == '#') XY(row, col) else null }
    }
    .toSet()

data class XY(val row: Int, val col: Int) {
    fun right() = copy(col = col + 1)
    fun left() = copy(col = col - 1)
    fun down() = copy(row = row + 1)
    fun up() = copy(row = row - 1)
    operator fun plus(other: XY) = copy(row = row + other.row, col = col + other.col)

    val dirs = listOf(1 to 0, -1 to 0, 0 to 1, 0 to -1, -1 to 1, -1 to -1, 1 to 1, 1 to -1)
    fun adjacent(): Set<XY> = dirs.map { XY(this.row + it.first, this.col + it.second) }.toSet()
    fun dir(dir: String): XY = dir.fold(this, XY::dir)
    fun dir(dir: Char): XY = when (dir) {
        'N' -> up()
        'E' -> right()
        'S' -> down()
        'W' -> left()
        'C' -> this
        else -> throw Exception("$dir")
    }
}

fun print(grid: Set<XY>) {
    for (row in grid.minOf { it.row }..grid.maxOf { it.row }) {
        for (col in grid.minOf { it.col }..grid.maxOf { it.col }) {
            print(if (XY(row, col) in grid) '#' else '.')
        }
        println()
    }
    println()
}

val rules = listOf(
    listOf("N", "NE", "NW"),
    listOf("S", "SE", "SW"),
    listOf("W", "NW", "SW"),
    listOf("E", "NE", "SE"),
)

data class Step(val grid: Set<XY>, val ruleIdx: Int = 0)
fun game() = generateSequence(Step(initial)) { (grid, ruleIdx) ->
    val (move, stay) = grid.partition { xy -> xy.adjacent().any { it in grid } }
    val candidates = move.map { xy ->
        xy to ((ruleIdx..ruleIdx + 3)
            .map { rule -> rules[rule % rules.size] }
            .firstOrNull { rule -> rule.all { xy.dir(it) !in grid } } ?: listOf("C"))
        }
        .map { (xy, rule) -> xy to xy.dir(rule.first()) }.toMap() + stay.associateBy { it }

    val whitelist = candidates.values.groupBy { it }.filterValues { it.size == 1 }.mapValues { it.value.single() }.values.toSet()
    val step = Step(candidates.entries.map { if (it.value in whitelist) it.value else it.key }.toSet(), ruleIdx + 1)

    if (step.grid == grid) null else step
}

game().drop(10)
    .first()
    .let { it.grid }
    .let { grid ->
        (grid.maxOf { it.col } - grid.minOf { it.col } + 1) * (grid.maxOf { it.row } - grid.minOf { it.row } + 1) - grid.size
    }
    .let(::println)

game().count()
