import java.io.File
import java.math.BigInteger

val (pattern, ruleInput) = File("14.input")
    .readText()
    .split("\n\n")
    .map(String::trim)
    .let { it[0] to it[1] }

val rules = ruleInput.lines()
    .map { it.split(" -> ") }
    .associate { it[0] to it[1] }

fun polymerize(pair: String): String = pair[0] + rules[pair]!! + pair[1]
fun megamerize(str: String, limit: Int): Map<Char, BigInteger> {
    val cache = mutableMapOf<Pair<String, Int>, Map<Char, BigInteger>>()

    fun loop(pair: String, level: Int): Map<Char, BigInteger> {
        if (level == limit) {
            return pair.groupingBy { it }.eachCount().mapValues { (_, v) -> BigInteger.valueOf(v.toLong()) }
        } else {
            var polymerized = polymerize(pair)

            if (cache.containsKey(polymerized to level)) {
                return cache[polymerized to level]!!
            }

            return loop(polymerized.substring(0, 2), level + 1).toMutableMap()
                .apply {
                    loop(polymerized.substring(1, 3), level + 1)
                        .forEach { merge(it.key, it.value) { a, b -> a + b } }

                    set(polymerized[1], getValue(polymerized[1]) - BigInteger.ONE)
                    cache.put(polymerized to level, this)
                }
        }
    }

    return str.windowed(2) { it.toString() to loop(it.toString(), 0) }
        .fold(emptyMap<Char, BigInteger>()) { acc, (pair, next) ->
            acc.toMutableMap()
                .apply {
                    next.forEach { merge(it.key, it.value) { a, b -> a + b } }
                    set(pair[1], getValue(pair[1]) - BigInteger.ONE)
                }
        }
        .toMutableMap()
        .apply { merge(str.last(), BigInteger.ONE) { a, b -> a + b} }
}

megamerize(pattern, 10)
    .let { println(it.entries.maxOf { it.value } - it.entries.minOf { it.value }) }
megamerize(pattern, 40)
    .let { println(it.entries.maxOf { it.value } - it.entries.minOf { it.value }) }