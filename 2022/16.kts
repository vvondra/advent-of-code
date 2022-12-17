import java.io.File
import java.util.*
@file:KotlinOptions("-J-Xmx5g")

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

data class Path(val length: Int, val path: List<String>)
fun pathLengths(start: String): Map<String, Path> {
    val frontier = ArrayDeque<Pair<String, Path>>().apply { add(start to Path(0, emptyList())) }
    val distances = mutableMapOf<String, Path>()
    while (frontier.isNotEmpty()) {
        val next = frontier.removeFirst()

        if (next.first !in distances) {
            distances.put(next.first, next.second)
        }

        valves[next.first]!!.tunnels.forEach {
            if (it !in distances) {
                frontier.add(it to Path(next.second.length + 1, next.second.path + it))
            }
        }
    }

    return distances.filter { valves[it.key]!!.flow > 0 }.filterKeys { it != start }
}

val paths = valves
    .filterValues { it.flow > 0 || it.name == startingValve }
    .mapValues { pathLengths(it.key) }

data class Valve(val name: String, val flow: Int, val tunnels: Set<String>)
data class State(val open: Set<String>, val current: String, val minute: Int, val released: Int = 0) {
    fun done() = minute == totalTime
    fun tick(): State = copy(minute = minute + 1, released = released + open.sumOf { valves[it]!!.flow })
    fun tick(n: Int): State = (1..n).fold(this) { e, _ -> e.tick() }
    fun candidateMoves() = mutableSetOf<State>().apply {
        val valve = valves[current]!!
        if (current !in open && valve.flow > 0) add(tick().copy(open = open + current))
        else add(tick(totalTime - minute))
        paths[current]!!
            .filterNot { (target, _) -> target in open }
            .forEach { (target, path) -> add(tick(path.length).copy(current = target)) }
    }

    fun scoreUpperBound() = released +
            (open.sumOf { valves[it]!!.flow } * (totalTime - minute + 1)) +
            valves.keys
                .filterNot { it in open }
                .sumOf { valves[it]!!.flow * (totalTime - minute - 1) } // optimizable
}

fun explore(): State? {
    var lowerBound = 0
    var bestKnown: State? = null
    val start = State(emptySet(), startingValve, 0, 0)
    val scoring = compareByDescending<State> { it.open.sumOf { valves[it]!!.flow } }.thenByDescending { it.scoreUpperBound() }
    val frontier = PriorityQueue<State>(scoring).apply { add(start) }
    val visited = mutableSetOf<State>().apply { add(start) }

    while (frontier.isNotEmpty()) {
        val next = frontier.remove()
        visited.add(next)

        if (next.done() && next.released > lowerBound) {
            bestKnown = next
            println(bestKnown)

            lowerBound = bestKnown.released
        } else {
            next.candidateMoves().forEach { candidate ->
                if (candidate.scoreUpperBound() >= lowerBound && candidate.minute <= totalTime) {
                    if (candidate !in visited) {
                        frontier.add(candidate)
                    }
                }
            }
        }
    }

    return bestKnown
}

explore()?.let(::println)
