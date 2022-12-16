import java.io.File
import java.util.*

val valves = File("input/16.in")
    .readLines()
    .map {
        val parts = it.split(" ")
        Valve(
            parts[1],
            parts[4].split("=").last().dropLast(1).toInt(),
            parts.drop(9).map { it.replace(",", "") }.toSet()
        )
    }
    .associate { it.name to it }

val startingValve = "AA"
val totalTime = 30

fun pathLengths(start: String): Map<String, Int> {
    val frontier = ArrayDeque<Pair<String, Int>>().apply { add(start to 0) }
    val distances = mutableMapOf<String, Int>()
    while (frontier.isNotEmpty()) {
        val next = frontier.removeFirst()

        if (next.first !in distances) {
            distances.put(next.first, next.second)

            valves[next.first]!!.tunnels.forEach {
                if (it !in distances) {
                    frontier.add(it to next.second + 1)
                }
            }
        }
    }

    return distances.filter { valves[it.key]!!.flow > 0 }.filterKeys { it != start }
}

val paths = valves
    .filterValues { it.flow > 0 || it.name == startingValve }
    .mapValues {
        pathLengths(it.key)
    }

data class Valve(val name: String, val flow: Int, val tunnels: Set<String>)
data class State(val open: Set<String>, val current: String, val minute: Int, val released: Int = 0) {
    fun done() = minute == totalTime
    fun tick(): State = copy(minute = minute + 1, released = released + open.sumOf { valves[it]!!.flow })
    fun tick(n: Int): State = (1..n).fold(this) { e, _ -> e.tick() }
    fun candidateMoves() = mutableSetOf<State>().apply {
        val valve = valves[current]!!
        if (current !in open && valve.flow > 0) add(tick().copy(open = open + current))
        paths[current]!!.forEach { (target, distance) -> add(tick(distance).copy(current = target)) }
    }
    fun scoreUpperBound() = released +
            (open.sumOf { valves[it]!!.flow } * (totalTime - minute)) +
            valves.keys
                .filterNot { it in open }
                .sumOf { valves[it]!!.flow * (totalTime - minute - (paths.get(current)?.get(it) ?: 1)) }
}

fun explore(): State? {
    var lowerBound = 0
    var bestKnown: State? = null
    val start = State(emptySet(), startingValve, 0, 0)
    val scoring = compareByDescending<State> { it.minute }.thenByDescending { it.scoreUpperBound() }
    val frontier = PriorityQueue<State>(scoring).apply { add(start) }
    val visited = mutableSetOf<State>().apply { add(start) }

    while (frontier.isNotEmpty()) {
        val next = frontier.remove()
        if (next.done()) {
            bestKnown = next
            println(bestKnown)

            lowerBound = bestKnown.released
        } else {
            next.candidateMoves().forEach { candidate ->
                if (candidate.scoreUpperBound() >= lowerBound && candidate.minute <= totalTime) {
                    if (candidate !in visited /* add cost function */) {
                        frontier.add(candidate)
                    }
                }
            }
        }
    }

    return bestKnown
}

paths.forEach(::println)
explore()?.let(::println)

// 1219 too low
