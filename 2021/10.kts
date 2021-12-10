import java.io.File
import java.util.*

val input = File("10.input").readLines()

val braces = mapOf('}' to '{', ']' to '[', ')' to '(', '>' to '<')
val reverseBraces = braces.entries.associate { (k, v) -> v to k }
val incomplete = ' '

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

data class Incomplete(val stack: Stack<Char>) : Res() {
    fun point(char: Char) = when(char) {
        ')' -> 1
        ']' -> 2
        '}' -> 3
        '>' -> 4
        else -> 0
    }
    fun score() = stack.mapNotNull { reverseBraces[it] }.fold(0) { acc, c -> 5 * acc + point(c) }
}

fun parse(line: String): Res {
    tailrec fun loop(tail: String, stack: Stack<Char>): Res =
        when {
            tail.isEmpty() -> Incomplete(stack)
            setOf('(', '<', '[', '{').contains(tail.first()) -> {
                stack.push(tail.first())
                loop(tail.drop(1), stack)
            }
            setOf(')', '>', ']', '}').contains(tail.first()) -> {
                val s = stack.pop()
                if (braces[tail.first()] != s) {
                    Illegal(tail.first())
                } else {
                    loop(tail.drop(1), stack)
                }
            }
            else -> throw Exception("nono")
        }

    return loop(line, Stack())
}

val parsed = input.map { parse(it) }
parsed.filterIsInstance<Illegal>().map { it.score() }.sum().let(::println)
parsed.filterIsInstance<Incomplete>().map { it.score() }.maxOrNull()?.let(::println) // todo median
