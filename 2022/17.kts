import java.io.File

//@file:KotlinOptions("-J-Xmx5g")

val cmds = File("input/17.in").readText().trim()

typealias Board = Set<XY>
data class XY(val row: Int, val col: Int) {
    fun left() = copy(col = col - 1)
    fun right() = copy(col = col + 1)
    fun down() = copy(row = row - 1)
}

val initial = List(7) { i -> XY(0, i) }.toSet()

val shapes = listOf(
    setOf(XY(0, 0), XY(0, 1), XY(0, 2), XY(0, 3)), // "-"
    setOf(XY(0, 1), XY(1, 0), XY(1, 1), XY(1, 2), XY(2, 1)), // "+"
    setOf(XY(0, 0), XY(0, 1), XY(0, 2), XY(1, 2), XY(2, 2)), // "L"
    setOf(XY(0, 0), XY(1, 0), XY(2, 0), XY(3, 0)), // "|"
    setOf(XY(0, 0), XY(0, 1), XY(1, 0), XY(1, 1)) // "#"
)

fun Board.topRows() = (0..6).associate { col -> col to filter { it.col == col }.maxOf { it.row } } // can be rewritten as groupBy
fun Board.newRow() = maxOf { it.row + 4 }
fun Board.initShape(shape: Set<XY>): Set<XY> {
    val rowOffset = newRow()
    return shape.map { it.copy(col = it.col + 2, row = it.row + rowOffset) }.toSet()
}
fun Board.trim(): Pair<Board, Int> {
    val fullRow = (newRow() downTo 1)
        .find { row -> (0..6).map { XY(row, it) }.all { it in this } }

    return if (fullRow != null) map { it.copy(row = it.row - fullRow) }.filter { it.row >= 0 }.toSet() to fullRow else this to 0
}
fun Board.print() {
    (newRow() downTo 0).forEach { row ->
        (0..6).forEach { col ->
            print(if (XY(row, col) in this) "#" else ".")
        }
        println()
    }
}

data class State(val map: Set<XY>, val shape: Set<XY>, val shapeIdx: Int, val cmdIdx: Int, val settled: Int = 0, val trimmed: Int = 0, val height: Int = 0)
data class History(val cmdIdx: Int)

val game = generateSequence(State(initial, initial.initShape(shapes[0]), 1, 0)) { (state, shape, nextIndex, cmdIdx, settled, trimmed) ->
    val cmd = cmds[cmdIdx]

    val shifted = when (cmd) {
        '>' -> if (shape.none { it.col == 6 } && shape.none { it.right() in state }) shape.map { it.right() } else shape
        '<' -> if (shape.none { it.col == 0 } && shape.none { it.left() in state }) shape.map { it.left() } else shape
        else -> throw Exception()
    }.toSet()

    val nextCmd = (cmdIdx + 1) % cmds.length

    val lowered = if (shifted.none { XY(it.row - 1, it.col) in state }) shifted.map { it.down() }.toSet() else shifted

    val height = state.maxOf { it.row } + trimmed

    if (lowered == shifted) {
        val (newState, newTrimmed) = (state + lowered).trim()
        val newShape = newState.initShape(shapes[nextIndex])

        State(newState, newShape, (nextIndex + 1) % shapes.size, nextCmd, settled + 1, trimmed + newTrimmed, height)
    } else State(state, lowered, nextIndex, nextCmd, settled, trimmed, height)
}
/*
print("\u001b[H\u001b[2J")
game.take(100).forEach {
    (it.map + it.shape).print()
    println("Next: ${cmds[it.cmdIdx]}")
    Thread.sleep(750)
    print("\u001b[H\u001b[2J")
}
*/

game.dropWhile { it.settled != 2022 }
    .first()
    .let { println(it.map.maxOf { it.row } + it.trimmed) }

val (baseHeight, nextHeight) = game.take(cmds.length * 5)
    .filter { it.cmdIdx == 0 }
    .zipWithNext()
    .map { it.second.height - it.first.height }
    .take(2)
    .toList()

val (baseSettled, nextSettled) = game.take(cmds.length * 5)
    .filter { it.cmdIdx == 0 }
    .zipWithNext()
    .map { it.second.settled - it.first.settled }
    .take(2)
    .toList()

val limit = 1000000000000L
val tail = (limit - baseSettled) % nextSettled

val tailGame = game.dropWhile { it.settled != tail.toInt() + baseSettled }.first()
val tailHeight = (tailGame.map.maxOf { it.row } + tailGame.trimmed) - baseHeight
val stack = baseHeight + ((limit - baseSettled) / nextSettled) * nextHeight + tailHeight

println(stack)

// todo: height != maxOf(row) + trimmed for some reason
// maybe i just get rid of trimming altogether
