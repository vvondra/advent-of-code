import java.io.File
import java.util.*
import kotlin.system.measureTimeMillis

data class XY(val x: Int, val y: Int) {
    val dirs = listOf(1 to 0, -1 to 0, 0 to 1, 0 to -1)
    fun adjacent(): Set<XY> = dirs.map { XY(this.x + it.first, this.y + it.second) }.toSet()
}

val input: Map<XY, Int> = File("15.input")
    .readLines().withIndex()
    .flatMap { (line, row) ->
        row.withIndex().map { (col, char) ->
            XY(line, col) to char.digitToInt()
        }
    }
    .toMap()


fun shortestPath(map: Map<XY, Int>): Int {
    val target = XY(map.maxOf { it.key.x }, map.maxOf { it.key.y })
    val start = XY(0, 0)
    val frontier = PriorityQueue<Pair<XY, Int>> { a, b -> a.second - b.second }
    val distance = mutableMapOf(start to 0)
    val prev = mutableMapOf<XY, XY>()
    val visited = mutableSetOf<XY>()
    var enqueue = true

    frontier.add(start to 0)

    while (frontier.isNotEmpty()) {
        val u = frontier.remove().first
        if (!visited.contains(u)) {
            visited.add(u)

            u.adjacent()
                .filter { map.containsKey(it) }
                .forEach { v ->
                val alt = distance[u]!! + map[v]!!
                if (alt < distance.getOrDefault(v, Int.MAX_VALUE)) {
                    distance.put(v, alt)
                    prev.put(v, u)

                    if (v == target) {
                        enqueue = false
                    } else {
                        if (enqueue) {
                            frontier.add(v to alt)
                        }
                    }
                }
            }
        }
    }

    return distance[target]!!
}
println(shortestPath(input))

fun risk(risk: Int, distance: Int): Int = (risk + distance - 1) % 9 + 1
fun tiled(map: Map<XY, Int>): Map<XY, Int> =
    (0 until 5).flatMap { a -> (0 until 5).map { b -> a to b } }
        .flatMap { (a, b) ->
            map.map { (point, risk) ->
                XY(a * (map.maxOf { it.key.x } + 1) + point.x, b * (map.maxOf { it.key.y } + 1) + point.y) to risk(risk, a + b)
            }
        }
        .toMap()

println(shortestPath(tiled(input)))
