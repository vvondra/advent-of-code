import java.io.File

val (drawing, steps) =  File("input/05.in")
    .readText()
    .split("\n\n")
    .map { it.split("\n") }

val initial = drawing
    .last()
    .withIndex()
    .filter { it.value.isDigit() }
    .map { it.index to it.value.digitToInt() }
    .fold(emptyMap<Int, List<Char>>()) { stacks, stack ->
        stacks + (stack.second to drawing.dropLast(1).reversed().fold(emptyList<Char>()) { deque, line ->
            if (line.length > stack.first && !line[stack.first].isWhitespace()) deque + line[stack.first] else deque
        })
    }

data class Move(val count: Int, val from: Int, val to: Int)
val moves = steps
    .filter { it.isNotEmpty() }
    .map { it.split(" ").filter { it.all { it.isDigit() } }.map { it.toInt() } }
    .map { Move(it[0], it[1], it[2]) }

fun stacker(is9001Mode: Boolean) = moves
    .fold(initial) { state, move ->
        state +
            (move.from to state[move.from]!!.dropLast(move.count)) +
            (move.to to (state[move.to]!! + state[move.from]!!.takeLast(move.count).run { if (is9001Mode) this else reversed() }))
    }
    .toSortedMap()
    .map { it.value.last() }
    .joinToString("")

stacker(is9001Mode = false).let(::println)
stacker(is9001Mode = true).let(::println)
