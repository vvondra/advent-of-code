import java.io.File
import java.util.*
import kotlin.math.abs

val cmds = File("input/09.in")
    .readLines()
    .flatMap {
        val (dir, n) = it.split(" ")
        List(n.toInt()) { _ -> dir.single() }
    }

data class XY(val x: Int, val y: Int) {
    operator fun plus(b: XY) = XY(x + b.x, y + b.y)
    fun touches(b: XY) = abs(b.x - x) <= 1 && abs(b.y - y) <= 1
    fun distance(b: XY) = abs(b.x - x) + abs(b.y - y)
    fun move(c: Char): XY = when (c) {
        'U' -> copy(x = x - 1)
        'D' -> copy(x = x + 1)
        'L' -> copy(y = y - 1)
        'R' -> copy(y = y + 1)
        else -> throw Exception()
    }
    fun follow(b: XY): XY = if (touches(b)) this else {
            setOf(-1, 0, 1).flatMap { i -> setOf(-1, 0, 1).map { j -> XY(i, j) } }
                .map { this + it }
                .minByOrNull { it.distance(b) }!!
        }
}

data class StepN(val knots: List<XY>, val visited: Set<XY> = setOf(knots.last()))
fun visited(n: Int) = cmds
    .fold(StepN(List(n) { _ -> XY(0, 0)})) { (knots, visited), cmd ->
        val new = knots.drop(1).runningFold(knots.first().move(cmd)) { acc, xy -> xy.follow(acc) }

        StepN(new, visited + new.last())
    }
    .let { it.visited.size }

println(visited(2))
println(visited(10))
