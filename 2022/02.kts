import java.io.File

val strategy = File("input/02.in")
    .readLines()
    .map { it.split(" ").let { it.first().single() - 'A' to it.last().single() - 'X' } }

fun shapeScore(x: Int) = x + 1
fun resultScore(x: Int, y: Int) = when {
    x == y -> 3
    x == (y + 1) % 3 -> 0
    else -> 6
}
fun totalScore(game: List<Pair<Int, Int>>) = game.sumOf { (a, b) -> shapeScore(b) + resultScore(a, b) }

totalScore(strategy).let(::println)

val resolved = strategy.map { (a, b) ->
    when (b) {
        0 -> a to Math.floorMod(a - 1, 3)
        1 -> a to a
        2 -> a to Math.floorMod(a + 1, 3)
        else -> throw Exception()
    }
}
totalScore(resolved).let(::println)
