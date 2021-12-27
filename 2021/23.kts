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

data class Pod(val char: Char, val row: Int, val col: Int, val steps: Int = 0) {
    fun costPerStep() = costs.getValue(char)
    fun intermediateCost() = costPerStep() * steps
    fun getHome() = home.getValue(char)
    fun isHome() = col == getHome()
    fun isInOtherHome() = !isHome() && !inHallway()
    fun inBottomCell() = row == 2
    fun inUpperCell() = row == 1
    fun inHallway() = row == 0
    fun totalEstimatedCost(pods: State) = remainingEstimatedCost(pods) + intermediateCost()

    // Important piece of code for branch and bound
    // It tries to put a lower bound on the additional cost this solution will need to get to final state
    // I simply for each pod out of home location take the shortest path if there were no blockers in its way
    fun remainingEstimatedCost(pods: State) = when {
        // stay home or move out for another different pod under this guy
        isHome() -> {
            if (inBottomCell()) 0
            else pods
                .find { it.col == col && it.inUpperCell() && it.char != char } // there's somebody under us we need to move for
                ?.let { (row * 2 + 2) * costPerStep() } // we'll move to hallway and back
                ?: 0
        }
        // just move horizontally and one down
        inHallway() -> abs(col - getHome()) * costPerStep() + costPerStep()
        // in somebody elses home
        else -> abs(col - getHome()) * costPerStep() + row * costPerStep() * 2
    }

    companion object {
        val home = mapOf('A' to 2, 'B' to 4, 'C' to 6, 'D' to 8)
        val costs = mapOf('A' to 1, 'B' to 10, 'C' to 100, 'D' to 1000)
    }
}

infix fun Int.towards(to: Int) = IntProgression.fromClosedRange(this, to, if (this > to) -1 else 1)

fun State.totalEstimatedCost() = this.sumOf { it.totalEstimatedCost(this) }
fun State.remainingEstimatedCost() = this.sumOf { it.remainingEstimatedCost(this) }

// This needs refactoring badly
fun State.candidateMoves() = this.flatMap { pod ->
    val rest = this - pod

    // possible columns to move in the hallway
    val occupiedHallway = (rest.filter { it.inHallway() }.map { it.col } + listOf(-1, 11))
    fun hallwayOptions(col: Int) =
        ((occupiedHallway.filter { it < col }.maxOrNull()!! + 1)..(occupiedHallway.filter { it > col }.minOrNull()!! - 1))
            .filter { it % 2 == 1 || it == 0 || it == 10}

    val states = mutableListOf<State>()
    when {
        // in hallway and the path home is clear
        pod.inHallway() && rest.none { it.inHallway() && it.col in pod.col towards pod.getHome() } -> {
            // home is actually empty
            if (rest.none { it.col == pod.getHome() }) {
                // Move from hallway to bottom cell of home
                states += rest + pod.copy(row = 2, col = pod.getHome(), steps = pod.steps + abs(pod.col - pod.getHome()) + 2)
            } else if (rest.filter { it.col == pod.getHome() }.let { it.all { it.char == pod.char } }) {
                // Move from hallway to top cell of home
                states += rest + pod.copy(row = 1, col = pod.getHome(), steps = pod.steps + abs(pod.col - pod.getHome()) + 1)
            }
        }
        // at home but move out for pod undertneath
        pod.isHome() && pod.inUpperCell() && (rest.any { it.col == pod.col && it.inBottomCell() && it.char != pod.char }) -> {
            hallwayOptions(pod.col).forEach { hallwayCol ->
                states += rest + pod.copy(row = 0, col = hallwayCol, steps = pod.steps + pod.row + abs(pod.col - hallwayCol))
            }
        }
        // move into hallway or to (empty home implemented in 2 steps from hallway to home)
        pod.isInOtherHome() -> when {
            pod.inUpperCell() -> {
                hallwayOptions(pod.col).forEach { hallwayCol ->
                    states += rest + pod.copy(row = 0, col = hallwayCol, steps = pod.steps + pod.row + abs(pod.col - hallwayCol))
                }
            }
            pod.inBottomCell() && rest.none { it.inUpperCell() && it.col == pod.col } -> {
                hallwayOptions(pod.col).forEach { hallwayCol ->
                    states += rest + pod.copy(row = 0, col = hallwayCol, steps = pod.steps + pod.row + abs(pod.col - hallwayCol))
                }
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
        print(state.find { it.col == i && it.inHallway() }?.let { it.char } ?: '.')
    }
    println("#")
    (-1..11).forEach { i ->
        print(state.find { it.col == i && it.row == 1 }?.let { it.char } ?: if (i in Pod.home.values) '.' else '#')
    }
    println()
    (-1..11).forEach { i ->
        print(state.find { it.col == i && it.row == 2 }?.let { it.char } ?: if (i in Pod.home.values) '.' else '#')
    }
    println()
    println("  #########")
}

// A branch and bound approach using a heap to choose most promising branch
fun explore(): State? {
    var upperBound = Int.MAX_VALUE
    var bestKnown: State? = null
    val frontier = PriorityQueue<State> { a, b -> a.totalEstimatedCost() - b.totalEstimatedCost() }
    frontier.add(input)

    while (frontier.isNotEmpty()) {
        val next = frontier.remove()
        val remainingCost = next.remainingEstimatedCost()
        if (remainingCost == 0) {
            val totalCost = next.totalEstimatedCost()
            if (totalCost < upperBound) {
                upperBound = totalCost
                bestKnown = next
            }
        } else {
            next.candidateMoves().forEach { candidate ->
                // TODO: 900 is a correction for the fact that my heuristic is not great
                // it needs debugging where I overshoot and keep the heuristic to be always lower than min possible real cost
                // ideally there is no constant here
                if (candidate.totalEstimatedCost() - 900 <= upperBound) {
                    frontier.add(candidate)
                }
            }
        }
    }

    return bestKnown
}

val elapsed = measureTimeMillis {
    explore()?.let {
        //render(it)
        //it.forEach(::println)
        println(it.totalEstimatedCost())
    }
}

println("${elapsed / 1000.0}s")