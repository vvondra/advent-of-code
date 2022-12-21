import java.util.*

data class XY(val x: Int, val y: Int) {
    operator fun plus(b: XY) = XY(x + b.x, y + b.y)
}
val input = java.io.File("input/08.in")
    .readLines()
    .withIndex()
    .flatMap { (i, row) ->
        row.withIndex().map { (j, num) -> XY(i, j) to num.digitToInt() }
    }
    .toMap()

val xBound = input.maxOf { (k, _) -> k.x }
val yBound = input.maxOf { (k, _) -> k.y }

data class Flare(val xy: XY, val vector: XY, val max: Int = Int.MIN_VALUE) {
    fun next(max: Int) = Flare(xy + vector, vector, max)
}

val queue = LinkedList<Flare>()
(0..xBound).forEach { x ->
    queue.add(Flare(XY(x, 0), XY(0, 1)))
    queue.add(Flare(XY(x, yBound), XY(0, -1)))
}
(0..yBound).forEach { y ->
    queue.add(Flare(XY(0, y), XY(1, 0)))
    queue.add(Flare(XY(xBound, y), XY(-1, 0)))
}

val visible = mutableSetOf<XY>()
while (!queue.isEmpty()) {
    val current = queue.pop()
    if (!input.containsKey(current.xy)) {
        continue
    }

    val height = input[current.xy]!!
    if (height > current.max) {
        visible.add(current.xy)
    }

    queue.add(current.next(maxOf(height, current.max)))
}

println(visible.size)

fun score(xy: XY): Long {
    val queue = LinkedList<Flare>()
    val flares = listOf(
        Flare(xy, XY(0, 1)).next(input[xy]!!),
        Flare(xy, XY(0, -1)).next(input[xy]!!),
        Flare(xy, XY(1, 0)).next(input[xy]!!),
        Flare(xy, XY(-1, 0)).next(input[xy]!!)
    )
    queue.addAll(flares)
    val distances = flares.map { (_, vector, _) -> vector to 0L }.toMap().toMutableMap()

    while (!queue.isEmpty()) {
        val current = queue.pop()
        if (!input.containsKey(current.xy)) {
            continue
        }

        val height = input[current.xy]!!
        distances[current.vector] = distances[current.vector]!! + 1

        if (height < current.max) {
            queue.add(current.next(maxOf(height, current.max)))
        }
    }

    return distances.values.reduce { x, y -> x * y }
}

input.keys.maxOf(::score).let(::println)
