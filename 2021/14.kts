import java.io.File

val (pattern, ruleInput) = File("14.input")
    .readText()
    .split("\n\n")
    .map(String::trim)
    .let { it[0] to it[1] }

val rules = ruleInput.lines()
    .map { it.split(" -> ") }
    .associate { it[0] to it[1] }

fun polymerize(str: String): String = str.windowed(2)
    .map { pair ->
        if (rules.containsKey(pair)) {
            pair[0] + rules[pair]!!
        } else pair[0]
    }
    .plus(pattern.last())
    .joinToString("")

fun polymerize(str: String, n: Int) = (0 until n).fold(str) { acc, _ -> polymerize(acc) }

val res = polymerize(pattern, 10)
val freqs = res.groupingBy { it }.eachCount()
val min = freqs.entries.minOf { it.value }
val max = freqs.entries.maxOf { it.value }

println(max - min)
