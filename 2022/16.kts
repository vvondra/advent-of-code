import java.io.File
import java.util.*

val input: Map<String, Valve> = File("input/16.in")
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

typealias PathMap = Map<String, Map<String, Path>>
data class Path(val length: Int, val path: List<String>)
fun pathLengths(start: String, valveMap: Map<String, Valve>): Map<String, Path> {
    val frontier = ArrayDeque<Pair<String, Path>>().apply { add(start to Path(0, emptyList())) }
    val distances = mutableMapOf<String, Path>()
    while (frontier.isNotEmpty()) {
        val next = frontier.removeFirst()

        if (next.first !in distances) {
            distances.put(next.first, next.second)
        }

        valveMap[next.first]!!.tunnels.forEach {
            if (it !in distances) {
                frontier.add(it to Path(next.second.length + 1, next.second.path + it))
            }
        }
    }

    return distances.filter { valveMap[it.key]!!.flow > 0 }.filterKeys { it != start }
}


data class Valve(val name: String, val flow: Int, val tunnels: Set<String>)
data class State(val open: Set<String>, val current: String, val minute: Int, val released: Int = 0) {
    fun done(totalTime: Int) = minute == totalTime
    fun tick(valves: Map<String, Valve>): State = copy(minute = minute + 1, released = released + open.sumOf { valves[it]!!.flow })
    fun tick(valves: Map<String, Valve>, n: Int): State = (1..n).fold(this) { e, _ -> e.tick(valves) }
    fun candidateMoves(valves: Map<String, Valve>, pathMap: PathMap, totalTime: Int) = mutableSetOf<State>().apply {
        val valve = valves[current]!!
        if (current !in open && valve.flow > 0) add(tick(valves).copy(open = open + current))
        else add(tick(valves, totalTime - minute))

        if (current !in pathMap) {
            println("ERROR!!! $current $pathMap")
        }
        pathMap[current]!!
            .filterNot { (target, _) -> target in open }
            .forEach { (target, path) -> add(tick(valves, path.length).copy(current = target)) }
    }

    fun scoreUpperBound(valves: Map<String, Valve>, totalTime: Int) = released +
            (open.sumOf { valves[it]!!.flow } * (totalTime - minute + 1)) +
            valves.keys
                .filterNot { it in open }
                .sumOf { valves[it]!!.flow * (totalTime - minute - 1) } // optimizable
}

fun explore(valves: Map<String, Valve>, totalTime: Int): State? {
    val pathMap: PathMap = valves
        .filterValues { it.flow > 0 || it.name == startingValve }
        .mapValues { pathLengths(it.key, valves) }

    var lowerBound = 0
    var bestKnown: State? = null
    val start = State(emptySet(), startingValve, 0, 0)
    val scoring = compareByDescending<State> { it.open.sumOf { valves[it]!!.flow } }.thenByDescending { it.scoreUpperBound(valves, totalTime) }
    val frontier = PriorityQueue<State>(scoring).apply { add(start) }
    val visited = mutableSetOf<State>().apply { add(start) }

    if (pathMap.size < 1) return start

    while (frontier.isNotEmpty()) {
        val next = frontier.remove()
        visited.add(next)

        if (next.done(totalTime) && next.released > lowerBound) {
            bestKnown = next
            //println(bestKnown)

            lowerBound = bestKnown.released
        } else {
            next.candidateMoves(valves, pathMap, totalTime).forEach { candidate ->
                if (candidate.scoreUpperBound(valves, totalTime) >= lowerBound && candidate.minute <= totalTime) {
                    if (candidate !in visited) {
                        frontier.add(candidate)
                    }
                }
            }
        }
    }

    return bestKnown
}


fun <T> split(list: List<T>) = sequence {
        val max = Math.pow(2.0, (list.size - 1).toDouble()).toLong()
        (0 until max).forEach { signature ->
            val setA = mutableSetOf<T>(list[0])
            val setB = mutableSetOf<T>()
            (1 until list.size).forEach {
                if (signature and (1L shl (it - 1)) == 0) setA.add(list[it]) else setB.add(list[it])
            }

            yield(setA to setB)
        }
    }


explore(input, 30)?.let(::println)

fun exploreTwo(): Int {
    return split((input.filter { it.value.flow > 0 }.keys).toList())
        .toList()
        .parallelStream()
        .map{ (me, elephant) ->
            val meValves = input.mapValues { if (it.key !in me) it.value.copy(flow = 0) else it.value }

            val elephantValves = input.mapValues { if (it.key !in elephant) it.value.copy(flow = 0) else it.value }

            val one = (explore(meValves, 26)?.released ?: 0)
            val two = (explore(elephantValves, 26)?.released ?: 0)
            print(me to elephant)
            println(" $one + $two = ${one + two}")

            one + two
        }
        .mapToInt { it }
        .max().asInt
}

exploreTwo().let(::println)
