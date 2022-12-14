import java.io.File
import java.util.*
import kotlin.math.exp

infix fun Int.towards(to: Int) = IntProgression.fromClosedRange(this, to, if (this > to) -1 else 1)
data class XY(val x: Int, val y: Int) {
    fun down() = copy(y = y + 1)
    fun downLeft() = down().copy(x = x - 1)
    fun downRight() = down().copy(x = x + 1)
}

val start = XY(500, 0)
val walls = File("input/14.in")
    .readLines()
    .flatMap(::parse)
    .toSet()
val abyss = walls.maxOf { it.y } + 1

fun parse(line: String) = line.split(" -> ")
        .map { it.split(",").map(String::toInt).let { XY(it[0], it[1]) } }
        .zipWithNext()
        .flatMap { (a, b) ->
            (a.x towards b.x).flatMap { i ->
                (a.y towards b.y).map { j -> XY(i, j) }
            }
        }

fun drop(map: Set<XY>, hasFloor: Boolean): Set<XY> {
    var grain = start
    while (true) {
        if  (grain.down().y == abyss && !hasFloor) return map
        else if (grain.down() !in map) grain = grain.down()
        else if (grain.downLeft() !in map) grain = grain.downLeft()
        else if (grain.downRight() !in map) grain = grain.downRight()
        else return map + grain
    }
}

fun dropAll(): Int {
    var state = walls
    while (true) {
        val next = drop(state, hasFloor = false)
        if (next.size == state.size) break
        state = next
    }

    return state.size - walls.size
}

println(dropAll())


fun stabilize(): Int {
    val floor = (-500..1500).map { XY(it, abyss + 1) }.toSet()
    var state = walls + floor
    while (true) {
        val next = drop(state, hasFloor = true)
        state = next
        if (start in next) break
    }

    return state.size - walls.size - floor.size
}

println(stabilize())
