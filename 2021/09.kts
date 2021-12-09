import java.io.File
import java.util.*

data class XY(val x: Int, val y: Int) {
    val dirs = listOf(1 to 0, -1 to 0, 0 to 1, 0 to -1)
    fun adjacent(): Set<XY> = dirs.map { XY(this.x + it.first, this.y + it.second) }.toSet()
}

val input: Map<XY, Int> = File("09.input")
    .readLines()
    .withIndex().flatMap { (line, row) ->
        row.withIndex().map { (col, char) ->
            XY(line, col) to char.digitToInt()
        }
    }
    .toMap()

fun lows(): Map<XY, Int> = input.filter { spot ->
    spot.key.adjacent().all { adj -> !input.containsKey(adj) || input[adj]!! > spot.value }
}

lows().values.map { it + 1 }.sum().let(::println)

fun basin(xy: XY): Set<XY> {
    fun largerAdjacent(point: XY): Set<XY> = point.adjacent()
        .filter { input.containsKey(it) && input[it]!! > input[point]!! && input[it]!! < 9 }
        .toSet()

    val basin = mutableSetOf(xy)
    val queue = LinkedList(largerAdjacent(xy))

    while (!queue.isEmpty()) {
        val next = queue.remove()
        basin.add(next)

        largerAdjacent(next)
            .filterNot { basin.contains(it) }
            .forEach { queue.add(it) }
    }

    return basin.toSet()
}

lows()
    .keys
    .map(::basin)
    .sortedByDescending { it.size }
    .take(3)
    .map { it.size }
    .reduce(Int::times)
    .let(::println)
