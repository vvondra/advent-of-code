import java.io.File
import java.util.*

typealias Tiles = Map<XY, Char>
val (mapText, instructionsText) = File("input/22.in").readText().split("\n\n")
val map: Tiles = mapText.lines()
    .flatMapIndexed { row, line ->
        line.mapIndexed { col, char -> if (char in setOf('.', '#')) XY(row + 1, col + 1) to char else null }
    }
    .filterNotNull()
    .toMap()

data class XY(val row: Int, val col: Int) {
    fun right() = copy(col = col + 1)
    fun left() = copy(col = col - 1)
    fun down() = copy(row = row + 1)
    fun up() = copy(row = row - 1)
}

data class Position(val xy: XY, val direction: Int) {
    fun next(): Position = when(direction) {
        0 -> copy(xy = xy.right())
        1 -> copy(xy = xy.down())
        2 -> copy(xy = xy.left())
        3 -> copy(xy = xy.up())
        else -> throw Exception("$direction")
    }
    fun turn(char: Char): Position = copy(direction = Math.floorMod(direction - (if (char == 'L') 1 else - 1), 4))
    val score = 1000 * xy.row + 4 * xy.col + direction
}


val instructions = instructionsText.trim().fold(emptyList<String>()) { list, char ->
    if (list.isEmpty()) listOf(char.toString())
    else {
        if (list.last().last().isDigit() && char.isDigit()) list.dropLast(1) + (list.last() + char)
        else list + listOf(char.toString())
    }
}

val topLeft = XY(1, map.filterKeys { it.row == 1 }.minOf { it.key.col })
instructions.fold(Position(topLeft, 0)) { current, ins ->
    when (ins) {
        "L" -> current.turn(ins.single())
        "R" -> current.turn(ins.single())
        else -> {
            val move = ins.toInt()
            var next = current
            (1..move).forEach {
                val candidate = next.next()
                next = when (map[candidate.xy]) {
                    '.' -> candidate
                    '#' -> return@forEach
                    else -> when (current.direction) {
                                0 -> current.copy(xy = current.xy.copy(col = map.filterKeys { it.row == current.xy.row }.minOf { it.key.col }))
                                1 -> current.copy(xy = current.xy.copy(row = map.filterKeys { it.col == current.xy.col }.minOf { it.key.row }))
                                2 -> current.copy(xy = current.xy.copy(col = map.filterKeys { it.row == current.xy.row }.maxOf { it.key.col }))
                                3 -> current.copy(xy = current.xy.copy(row = map.filterKeys { it.col == current.xy.col }.maxOf { it.key.row }))
                                else -> throw Exception("$current")
                            }
                            .let { wrapped -> if (map[wrapped.xy]!! == '.') wrapped else return@forEach }
                }
            }

            next
        }
    }
}.let { println(it.score) }
