import java.io.File
import java.util.*

// just copied most code from 2021, day 15
data class XY(val x: Int, val y: Int) {
    val dirs = listOf(1 to 0, -1 to 0, 0 to 1, 0 to -1)
    fun adjacent(): Set<XY> = dirs.map { XY(this.x + it.first, this.y + it.second) }.toSet()
}

val input: Map<XY, Char> = File("input/12.in")
    .readLines()
    .flatMapIndexed { line, row ->
        row.mapIndexed { col, char ->
            XY(line, col) to char
        }
    }
    .toMap()


fun shortestPath(map: Map<XY, Char>, start: XY): Int? {
    val target = map.entries.find { it.value == 'E' }!!.key
    val cleanMap = map.mapValues { when(it.value) {
        'S' -> 'a'
        'E' -> 'z'
        else -> it.value
    } }
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
                .filter { cleanMap.containsKey(it) }
                .filter { cleanMap[it]!! <= cleanMap[u]!! + 1 }
                .forEach { v ->
                    val alt = distance[u]!! + 1
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

    return distance[target]
}

println(shortestPath(input, input.entries.find { it.value == 'S' }!!.key))

input.entries.filter { it.value in setOf('S', 'a') }
    .minOfOrNull { shortestPath(input, it.key) ?: Int.MAX_VALUE }
    .let(::println)
