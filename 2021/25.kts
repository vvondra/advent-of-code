import java.io.File
import java.util.*


data class XY(val x: Int, val y: Int)
val input: Map<XY, Char> = File("25.input")
    .readLines()
    .withIndex().flatMap { (line, row) ->
        row.withIndex().map { (col, char) ->
            XY(line, col) to char
        }
    }
    .toMap()

fun render(map: Map<XY, Char>) {
    for (x in map.keys.minOf { it.x }..map.keys.maxOf { it.x }) {
        for (y in map.keys.minOf { it.y }..map.keys.maxOf { it.y }) {
            print(map.getValue(XY(x, y)))
        }
        println()
    }
}

val limits = XY(input.maxOf { it.key.x }, input.maxOf { it.key.y} )
var i = 0
val cucumbers = generateSequence(input) { state ->
    val east = state.filterValues { it == '>' }
        .filter { (xy, _) -> state[XY(xy.x, (xy.y + 1) % (limits.y + 1))]!! == '.' }
        .flatMap { (xy, char) ->
            listOf(
                XY(xy.x, (xy.y + 1) % (limits.y + 1)) to char,
                XY(xy.x, xy.y) to '.'
            )
        }
        .toMap()

    val step = state.plus(east)

    val south = step.filterValues { it == 'v' }
        .filter { (xy, _) -> step[XY((xy.x + 1) % (limits.x + 1), xy.y)]!! == '.' }
        .flatMap { (xy, char) ->
            listOf(
                XY((xy.x + 1) % (limits.x + 1), xy.y) to char,
                XY(xy.x, xy.y) to '.'
            )
        }
        .toMap()

    val turn = step.plus(south)

    if (turn == state) null else turn
}

println(cucumbers.count())