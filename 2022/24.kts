import java.io.File
import java.util.*
import kotlin.math.abs
@file:KotlinOptions("-J-Xmx5g")
fun IntRange.length(): Int = if (this.isEmpty()) 0 else endInclusive - start + 1
val map: Map<XY, Char> = File("input/24.in").readLines()
    .flatMapIndexed { row, line ->
        line.mapIndexed { col, char -> XY(row, col) to char }
    }
    .toMap()

val colBounds = 1..(map.maxOf { it.key.col } - 1)
val rowBounds = 1..(map.maxOf { it.key.row } - 1)
val beginning = map.entries.filter { it.value == '.' && it.key.row == 0 }.single().key
val end = map.entries.filter { it.value == '.' && it.key.row == rowBounds.endInclusive + 1}.single().key

data class XY(val row: Int, val col: Int) {
    fun distance(other: XY) = abs(row - other.row) + abs(col - other.col)
    fun right() = copy(col = col + 1)
    fun left() = copy(col = col - 1)
    fun down() = copy(row = row + 1)
    fun up() = copy(row = row - 1)
    fun adjacent() = sequenceOf(down(), left(), up(), right())
    fun valid() = col in colBounds && row in rowBounds
    operator fun plus(other: XY) = copy(row = row + other.row, col = col + other.col)
}
data class Blizzard(val xy: XY, val dir: Char) {
    fun moveTo(xy: XY): Blizzard = copy(xy = xy)
    fun move(): Blizzard = when(dir) {
        '>' -> xy.right().let { if (it.col !in colBounds) it.copy(col = 1) else it }.let(::moveTo)
        '<' -> xy.left().let { if (it.col !in colBounds) it.copy(col = colBounds.endInclusive) else it }.let(::moveTo)
        'v' -> xy.down().let { if (it.row !in rowBounds) it.copy(row = 1) else it }.let(::moveTo)
        '^' -> xy.up().let { if (it.row !in rowBounds) it.copy(row = rowBounds.endInclusive) else it }.let(::moveTo)
        else -> throw Exception("$dir")
    }
}

data class State(val xy: XY, val steps: Int) {
    fun done(target: XY) = xy == target
    fun candidateMoves(target: XY) = mutableSetOf<State>().apply {
        addAll(xy.adjacent().filter { it.valid() || it == target }.map { State(it, steps + 1) })
        add(copy(steps = steps + 1))
    }
}

val firstBlizzard = map.filterValues { it in setOf('<', '>', 'v', '^') }.map { Blizzard(it.key, it.value) }
val blizzardCache = mutableMapOf<Int, Set<XY>>(0 to firstBlizzard.map { it.xy }.toSet())
var lastBlizzard = 0 to firstBlizzard
fun getBlizzards(step: Int): Set<XY> {
    if (step in blizzardCache) return blizzardCache[step]!!

    var next = lastBlizzard.second
    ((lastBlizzard.first + 1)..step).forEach { it ->
        next = next.map { it.move() }
        blizzardCache.put(it, next.map { it.xy }.toSet())
    }

    lastBlizzard = step to next

    return blizzardCache[step]!!
}

fun print(blizzards: List<Blizzard>) {
    rowBounds.forEach { row ->
        print("#")
        colBounds.forEach { col ->
            val matching = blizzards.filter { it.xy == XY(row, col) }
            print(when (matching.size) {
                0 -> '.'
                1 -> matching.single().dir
                else -> matching.size
            })
        }
        println("#")
    }
}

fun explore(start: XY, target: XY, entrance: State = State(start, 0)): State? {
    val scoring = compareBy<State> { it.steps + it.xy.distance(target) }
    val frontier = PriorityQueue<State>(scoring).apply { add(entrance) }
    val visited = mutableSetOf<State>().apply { add(entrance) }
    var frontierSteps = 0
    while (frontier.isNotEmpty()) {
        val next = frontier.remove()
        visited.add(next)

        if (next.steps > frontierSteps) {
            frontierSteps = next.steps
            println("${next.steps} -> Frontier: ${frontier.size}, visited: ${visited.size}")
        }

        if (next.done(target)) return next else {
            next.candidateMoves(target).forEach { candidate ->
                val blizzards = getBlizzards(candidate.steps + 1)
                if (candidate !in visited && candidate.xy !in blizzards) {
                    frontier.add(candidate)
                }
            }
        }
    }

    return null
}

val there = explore(beginning, end)
println(there?.let { it.steps + 1 })

there
    ?.let { back -> println(back); explore(back.xy, beginning, back) }
    ?.let { again -> explore(again.xy, end, again) }
    ?.let { println(it.steps + 1) }
