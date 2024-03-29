import java.io.File
import java.lang.Math.max
import java.lang.Math.min

infix fun Int.towards(to: Int) = IntProgression.fromClosedRange(this, to, if (this > to) -1 else 1)

data class XY(val x: Int, val y: Int)
data class Line(val a: XY, val b: XY) {
    fun points(withDiagonals: Boolean): Set<XY> = when {
        a.x == b.x -> (min(a.y, b.y)..max(a.y, b.y)).map { XY(a.x, it) }.toSet()
        a.y == b.y -> (min(a.x, b.x)..max(a.x, b.x)).map { XY(it, a.y) }.toSet()
        withDiagonals -> {
            val xRange = IntProgression.fromClosedRange(a.x, b.x, if (a.x > b.x) -1 else 1)
            val yRange = IntProgression.fromClosedRange(a.y, b.y, if (a.y > b.y) -1 else 1)

            (xRange zip yRange)
                .map { XY(it.first, it.second) }
                .toSet()
        }
        else -> emptySet()
    }
}

val input = File("05.input")
    .readLines()
    .map {
        it.split(" -> ").map {
            it.split(",").let { XY(it[0].toInt(), it[1].toInt()) }
        }.let { Line(it[0], it[1]) }
    }

fun overlaps(lines: List<Line>, withDiagonals: Boolean = false): Int = mutableMapOf<XY, Int>()
    .apply {
        lines
            .flatMap { it.points(withDiagonals) }
            .forEach { xy -> this.merge(xy, 1) { a, b -> a + b } }
    }
    .count { it.value > 1 }

println(overlaps(input))
println(overlaps(input, withDiagonals = true))
