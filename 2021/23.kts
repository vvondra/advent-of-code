import java.io.File
import java.lang.Math.abs
import java.util.*
import kotlin.system.measureTimeMillis

typealias State = Set<Pod>

val input: State = File("23.input")
    .readLines()
    .withIndex().flatMap { (line, row) ->
        row.withIndex().map { (col, char) ->
            if (char.isLetter()) Pod(char, line - 1, col - 1) else null
        }
    }
    .filterNotNull()
    .toSet()

val maxRow = input.maxOf { it.row }

data class Pod(val char: Char, val row: Int, val col: Int, val steps: Int = 0) {
    val costPerStep = costs.getValue(char)
    val intermediateCost = costPerStep * steps
    val home = homeCol.getValue(char)
    val isHome = col == home
    val inHallway = row == 0
    val isInOtherHome = !isHome && !inHallway

    fun isTopmost(pods: State) = row < 2 || row == pods.filter { it.col == col }.minOf { it.row }
    fun hasOtherUnderneath(pods: State) = pods.any { it.col == col && it.row > row && it.char != char }
    fun totalEstimatedCost(pods: State) = remainingEstimatedCost(pods) + intermediateCost

    // Important piece of code for branch and bound
    // It puts lower bound on the additional cost this solution will need to get to final state
    // Cannot be higher than final cost (will break branch and bound)
    // I simply for each pod out of home location take the shortest path if there were no blockers in its way
    // and assume it will only have to go to first cell of its home for those reasons
    fun remainingEstimatedCost(pods: State) = when {
        // safeguard for thrashing scenario - scenario when you move a pod more than 20 times is unlikely
        steps > 20 -> 10000
        // stay home or move out for another different pod under this guy
        isHome -> when {
            hasOtherUnderneath(pods) -> (row + 2 + 1) * costPerStep
            else -> 0
        }
        // just move horizontally and one down
        inHallway -> abs(col - home) * costPerStep + costPerStep
        // in somebody elses home
        else -> abs(col - home) * costPerStep + (row + 1) * costPerStep
    }

    companion object {
        val homeCol = mapOf('A' to 2, 'B' to 4, 'C' to 6, 'D' to 8)
        val costs = mapOf('A' to 1, 'B' to 10, 'C' to 100, 'D' to 1000)
    }
}

infix fun Int.towards(to: Int) = IntProgression.fromClosedRange(this, to, if (this > to) -1 else 1)

// costs and heuristics
fun State.inversions() = this.map { pod -> this.count { it.col == pod.col && it.row > pod.row && it.char != pod.char && !it.isHome } }.sum()
fun State.totalEstimatedCost() = this.sumOf { it.totalEstimatedCost(this) }
fun State.remainingEstimatedCost() = this.sumOf { it.remainingEstimatedCost(this) }
fun State.intermediateCost() = this.sumOf { it.intermediateCost }

// This needs refactoring badly
fun State.candidateMoves() = this.flatMap { pod ->
    val rest = this - pod

    // possible columns to move in the hallway
    val occupiedHallway = (rest.filter { it.inHallway }.map { it.col } + listOf(-1, 11))
    fun hallwayOptions(col: Int) =
        ((occupiedHallway.filter { it < col }.maxOrNull()!! + 1)..(occupiedHallway.filter { it > col }.minOrNull()!! - 1))
            .filter { it % 2 == 1 || it == 0 || it == 10 }

    val states = mutableListOf<State>()
    when {
        // in hallway and the path home is clear
        pod.inHallway && rest.none { it.inHallway && it.col in pod.col towards pod.home } -> {
            val top = rest.filter { it.col == pod.home }.minByOrNull { it.row }

            if (top == null) {
                // Move from hallway to bottom cell of home
                states += rest + pod.copy(row = maxRow, col = pod.home, steps = pod.steps + abs(pod.col - pod.home) + maxRow)
            } else if (top.char == pod.char && rest.filter { it.col == pod.home }.all { it.char == pod.char }) { // move on top of homie
                states += rest + pod.copy(row = top.row - 1, col = pod.home, steps = pod.steps + abs(pod.col - pod.home) + top.row - 1)
            }
        }
        // at home but move out for pod undertneath
        pod.isHome && pod.isTopmost(this) && pod.hasOtherUnderneath(this) -> {
            hallwayOptions(pod.col).forEach { hallwayCol ->
                states += rest + pod.copy(row = 0, col = hallwayCol, steps = pod.steps + pod.row + abs(pod.col - hallwayCol))
            }
        }
        // move into hallway or to (empty home implemented in 2 steps from hallway to home)
        pod.isInOtherHome && pod.isTopmost(this) -> {
            hallwayOptions(pod.col).forEach { hallwayCol ->
                states += rest + pod.copy(
                    row = 0,
                    col = hallwayCol,
                    steps = pod.steps + pod.row + abs(pod.col - hallwayCol)
                )
            }
        }
    }

    states
}.toSet()

// just debugging render code
fun render(state: State) {
    println("#############")
    print("#")
    (0..10).forEach { i ->
        print(state.find { it.col == i && it.inHallway }?.let { it.char } ?: '.')
    }
    println("#")
    (1..4).forEach { j ->
        (-1..11).forEach { i ->
            print(state.find { it.col == i && it.row == j }?.let { it.char } ?: if (i in Pod.homeCol.values) '.' else '#')
        }
        println()
    }
    println()
    println("  #########")
}

// A branch and bound approach using a heap to choose most promising branch
fun explore(): State? {
    var upperBound = Int.MAX_VALUE
    var bestKnown: State? = null
    val frontier = PriorityQueue<State> { a, b -> a.inversions() - b.inversions() }
    val visited = mutableMapOf(input to 0)

    frontier.add(input)

    while (frontier.isNotEmpty()) {
        val next = frontier.remove()
        val remainingCost = next.remainingEstimatedCost()
        visited.put(next, next.intermediateCost())

        if (remainingCost == 0) {
            val totalCost = next.totalEstimatedCost()
            if (totalCost < upperBound) {
                println(totalCost)
                upperBound = totalCost
                bestKnown = next
            }
        } else {
            next.candidateMoves().forEach { candidate ->
                if (candidate.totalEstimatedCost() <= upperBound) {
                    if (!visited.containsKey(candidate) || visited.getValue(candidate) > candidate.intermediateCost()) {
                        frontier.add(candidate)
                    }
                }
            }
        }
    }

    return bestKnown
}

val elapsed = measureTimeMillis {
    explore()?.let {
        println(it.totalEstimatedCost())
    }
}

println("${elapsed / 1000.0}s")