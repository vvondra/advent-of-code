import java.io.File

val input = File("10.input").readLines()

val braces = mapOf('}' to '{', ']' to '[', ')' to '(', '>' to '<')

sealed class Res
data class Illegal(val char: Char) : Res() {
    fun score() = when (char) {
        ')' -> 3
        ']' -> 57
        '}' -> 1197
        '>' -> 25137
        else -> 0
    }
}

data class Incomplete(val stack: List<Char>) : Res() {
    fun point(char: Char) = when (char) {
        '(' -> 1
        '[' -> 2
        '{' -> 3
        '<' -> 4
        else -> 0
    }

    fun score(): Long = stack.fold(0L) { acc, c -> 5 * acc + point(c) }
}

fun parse(line: String): Res {
    tailrec fun loop(tail: String, stack: List<Char>): Res =
        when {
            tail.isEmpty() -> Incomplete(stack)
            setOf('(', '<', '[', '{').contains(tail.first()) -> {
                loop(tail.drop(1), listOf(tail.first()) + stack)
            }
            setOf(')', '>', ']', '}').contains(tail.first()) -> {
                val s = stack.first()
                if (braces[tail.first()] != s) {
                    Illegal(tail.first())
                } else {
                    loop(tail.drop(1), stack.drop(1))
                }
            }
            else -> throw Exception("nono")
        }

    return loop(line, emptyList())
}

val parsed = input.map { parse(it) }
parsed.filterIsInstance<Illegal>().map { it.score() }.sum().let(::println)
parsed.filterIsInstance<Incomplete>().map { it.score() }.sortedDescending().let { println(it[it.size / 2]) }
