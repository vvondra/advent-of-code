import java.io.File

val input = File("04.input")
    .readText()
    .split("\n\n")

val MARKER = -1

typealias Board = List<List<Int>>

val draws = input.first().split(",").map(String::toInt)
val inputBoards = input.drop(1).map {
    it.lines().map {
        it.trim().split(Regex("\\s+")).map(String::toInt)
    }
}

fun turn(board: Board, draw: Int): Board = board.map { line -> line.map { if (it == draw) MARKER else it } }
fun isWinning(board: Board): Boolean = board.withIndex().any {
    it.value.all { el -> el == MARKER } || board.all { line -> line[it.index] == MARKER }
}
fun score(board: Board, draw: Int) = board.flatten().filterNot { it == MARKER }.sum() * draw

tailrec fun play(boards: List<Board>, draws: List<Int>): Int {
    val newTurn = boards.map { turn(it, draws.first()) }
    val winner = newTurn.find(this::isWinning)

    if (winner != null) {
        return score(winner, draws.first())
    } else {
        return play(newTurn, draws.drop(1))
    }
}

tailrec fun playLast(boards: List<Board>, draws: List<Int>): Int {
    val newTurn = boards.map { turn(it, draws.first()) }.filterNot(this::isWinning)
    if (newTurn.size == 1) {
        return play(newTurn, draws.drop(1))
    } else {
        return playLast(newTurn, draws.drop(1))
    }
}

println(play(inputBoards, draws))
println(playLast(inputBoards, draws))