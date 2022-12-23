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
    operator fun plus(other: XY) = copy(row = row + other.row, col = col + other.col)
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

fun overflow2d(current: Position): Position = when (current.direction) {
    0 -> current.copy(xy = current.xy.copy(col = map.filterKeys { it.row == current.xy.row }.minOf { it.key.col }))
    1 -> current.copy(xy = current.xy.copy(row = map.filterKeys { it.col == current.xy.col }.minOf { it.key.row }))
    2 -> current.copy(xy = current.xy.copy(col = map.filterKeys { it.row == current.xy.row }.maxOf { it.key.col }))
    3 -> current.copy(xy = current.xy.copy(row = map.filterKeys { it.col == current.xy.col }.maxOf { it.key.row }))
    else -> throw Exception("$current")
}

fun score(overflow: (Position) -> Position) = instructions
    .fold(Position(topLeft, 0)) { current, ins ->
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
                        else -> overflow(next).takeIf { it.xy in map && map[it.xy] == '.' } ?: return@forEach
                    }
                }
                next
            }
        }
    }
    .let { it.score }

println(score(::overflow2d))

val faceIdx = mapOf(
    1 to (0 to 1),
    2 to (0 to 2),
    3 to (1 to 1),
    4 to (2 to 0),
    5 to (2 to 1),
    6 to (3 to 0)
)

val squareSize = 50
val faceXY = faceIdx.mapValues { (_, idx) -> (idx.first * squareSize + 1)..((idx.first + 1) * squareSize) to (idx.second * squareSize + 1)..((idx.second + 1) * squareSize) }

val mappings = mapOf(
    (1 to 3) to ((6 to 2) to fun(xy: XY): XY = XY(xy.col, xy.row)),
    (1 to 2) to ((4 to 2) to fun(xy: XY): XY = XY(squareSize - 1 - xy.row, xy.col)), // invert
    (3 to 2) to ((4 to 3) to fun(xy: XY): XY = XY(xy.col, xy.row)),
    (3 to 0) to ((2 to 1) to fun(xy: XY): XY = XY(xy.col, xy.row)),
    (2 to 0) to ((5 to 0) to fun(xy: XY): XY = XY(squareSize - 1 - xy.row, xy.col)), //invert
    (2 to 1) to ((3 to 0) to fun(xy: XY): XY = XY(xy.col, xy.row)),
    (2 to 3) to ((6 to 1) to fun(xy: XY): XY = XY(squareSize - 1 - xy.row, xy.col)),
    (5 to 1) to ((6 to 0) to fun(xy: XY): XY = XY(xy.col, xy.row)),
    ).let { it + it.map { (key, value) -> value.first to (key to value.second) } }

fun overflow3d(current: Position): Position {
    val face = faceXY.entries.first { (_, value) -> current.xy.row in value.first && current.xy.col in value.second }.key
    //println("Located on face $face: $current ${map[current.xy]}")
    val target = mappings[face to current.direction]!!
    //println("Going to $target")
    val offset = XY(current.xy.row - faceXY[face]!!.first.first, current.xy.col - faceXY[face]!!.second.start)
    //println("Offset in original face is $offset")
    val newFace = target.first.first
    val base = XY(faceXY[newFace]!!.first.start, faceXY[newFace]!!.second.start)
    //println("Base to add is $base")

    return Position(target.second(offset).let { it + base }, (target.first.second + 2) % 4)
        //.also { println("Resolved is $it") }
}

println(score(::overflow3d))
