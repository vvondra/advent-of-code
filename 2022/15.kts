import java.io.File
import java.util.*
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.max

fun IntRange.length(): Int = if (this.isEmpty()) 0 else endInclusive - start + 1
infix fun IntRange.intersect(b: IntRange): IntRange = IntRange(max(start, b.start), min(endInclusive, b.endInclusive))
infix fun IntRange.intersectingUnion(b: IntRange): IntRange = IntRange(min(start, b.start), max(endInclusive, b.endInclusive))

data class Zone(val sensor: XY, val beacon: XY) {
    val range = sensor.distance(beacon)
    fun range(y: Int) = sensor.distance(beacon) - min(range, abs(sensor.y - y))
    val xRange = sensor.x - range..sensor.x + range
    fun xRange(y: Int): IntRange {
        val yRange = range(y)
        return if (yRange == 0) IntRange.EMPTY else sensor.x - yRange..sensor.x + yRange
    }
    operator fun contains(point: XY) = sensor.distance(point) <= range
    fun coversRow(y: Int) = abs(sensor.y - y) < range
}
data class XY(val x: Int, val y: Int) {
    fun distance(other: XY) = abs(x - other.x) + abs(y - other.y)
}

val zones = File("input/15.in")
    .readLines()
    .map {
        val r = Regex("x=(-?\\d+), y=(-?\\d+)")
        r.findAll(it).map {
            val (x, y) = it.destructured
            XY(x.toInt(), y.toInt())
        }.let { Zone(it.first(), it.last()) }
    }


val row = 2000000
val bottom = 0
val top = 4000000
val bounds = bottom..top

val xRange = zones.minOf { it.xRange(row).start }..zones.maxOf { it.xRange(row).endInclusive }
xRange
    .map { XY(it, row) }
    .count { xy -> zones.any { xy in it } && zones.none { it.beacon == xy }}
    .let(::println)

bounds.forEach { y ->
    val covers = zones.filter { it.coversRow(y) }.map { it.xRange(y) }.toMutableSet()
    while (covers.size > 1) {
        val candidate = covers.first()
            .apply { covers.remove(this) }
        val intersection = covers.find { (it intersect candidate).length() > 0 }
        if (intersection != null) {
            with(covers) {
                remove(intersection)
                add(candidate intersectingUnion intersection)
            }
        } else {
            val x = covers.single().endInclusive.toLong() + 1 // this could be much more generic in case candidate is the "left" range
            println(4000000 * x + y)
            return@forEach
        }
    }
}
