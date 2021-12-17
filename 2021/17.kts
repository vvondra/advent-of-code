import kotlin.math.sign

val targetX = 70..96
val targetY = -179..-124

data class XY(val x: Int, val y: Int)

fun shoot(init: XY): Sequence<XY> =
    generateSequence(XY(0,0) to init) { (pos, vel) ->
        XY(pos.x + vel.x, pos.y + vel.y) to XY(vel.x - (1 * vel.x.sign), vel.y - 1)
    }
        .map { (pos, _) -> pos }
        .takeWhile { it.x <= targetX.endInclusive && it.y >= targetY.start }

fun height(vec: XY): Int = shoot(vec).maxOf { it.y }
fun hits(vec: XY) = shoot(vec).any { (x, y) -> x in targetX && y in targetY }

val attempts = sequence {
    for (i in -1000..1000) {
        for (j in -1000..1000) {
            if (hits(XY(i, j))) yield(height(XY(i, j)))
        }
    }
}.toList()

attempts.maxOrNull().let(::println)
attempts.count().let(::println)
